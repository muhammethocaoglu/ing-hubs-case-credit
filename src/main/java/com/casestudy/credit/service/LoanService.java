package com.casestudy.credit.service;

import com.casestudy.credit.controller.dto.*;
import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.entity.Loan;
import com.casestudy.credit.entity.LoanInstallment;
import com.casestudy.credit.exception.BusinessException;
import com.casestudy.credit.exception.ResourceNotFoundException;
import com.casestudy.credit.repository.CustomerRepository;
import com.casestudy.credit.repository.LoanInstallmentRepository;
import com.casestudy.credit.repository.LoanRepository;
import com.casestudy.credit.repository.LoanSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Transactional
    public CreateLoanResponse create(CreateLoanRequest createLoanRequest) {
        Customer customer = customerRepository.findById(createLoanRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Customer with id %s not found",
                        createLoanRequest.getCustomerId())));
        BigDecimal totalLoanAmount = createLoanRequest.getAmount()
                .multiply(BigDecimal.ONE.add(createLoanRequest.getInterestRate()));
        if (customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(totalLoanAmount) < 0) {
            throw new BusinessException("Customer credit limit exceeded");
        }
        BigDecimal installmentAmount = totalLoanAmount
                .divide(BigDecimal.valueOf(createLoanRequest.getNumberOfInstallment()), RoundingMode.HALF_EVEN);
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
        customerRepository.save(customer);

        LocalDate currentDate = LocalDate.now();
        Loan loan = Loan.builder()
                .customer(customer)
                .loanAmount(totalLoanAmount)
                .numberOfInstallment(createLoanRequest.getNumberOfInstallment())
                .createdDate(currentDate)
                .build();
        Set<LoanInstallment> loanInstallmentSet = IntStream.rangeClosed(1, createLoanRequest.getNumberOfInstallment())
                .boxed()
                .map(installment -> LoanInstallment.builder()
                        .loan(loan)
                        .amount(installmentAmount)
                        .dueDate(currentDate.plusMonths(installment).withDayOfMonth(1))
                        .build())
                .collect(Collectors.toSet());
        loan.setLoanInstallments(loanInstallmentSet);
        loanRepository.save(loan);

        return CreateLoanResponse.builder()
                .id(loan.getId())
                .build();
    }

    public List<ListLoanResponseItem> list(ListLoanRequest listLoanRequest) {

        Specification<Loan> loanSpec = Specification
                .where(LoanSpecification.hasCustomerId(listLoanRequest.getCustomerId()));
        if (listLoanRequest.getNumberOfInstallment() != null) {
            loanSpec = loanSpec.and(LoanSpecification.hasNumberOfInstallment(listLoanRequest.getNumberOfInstallment()));
        }
        if (listLoanRequest.getIsPaid() != null) {
            loanSpec = loanSpec.and(LoanSpecification.hasIsPaid(listLoanRequest.getIsPaid()));
        }
        return loanRepository.findAll(loanSpec)
                .stream()
                .map(loanItem -> ListLoanResponseItem.builder()
                        .id(loanItem.getId())
                        .amount(loanItem.getLoanAmount())
                        .numberOfInstallment(loanItem.getNumberOfInstallment())
                        .createDate(loanItem.getCreatedDate())
                        .isPaid(loanItem.getIsPaid())
                        .build())
                .toList();
    }

    @Transactional
    public PayLoanResponse pay(PayLoanRequest payLoanRequest) {
        Loan loan = loanRepository.findById(payLoanRequest.getId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Loan with id %s not found",
                payLoanRequest.getId())));
        if (loan.getIsPaid()) {
            return PayLoanResponse.builder()
                    .isLoanPaid(true)
                    .numberOfInstallmentsPaid(0)
                    .totalAmountSpent(BigDecimal.ZERO)
                    .build();
        }
        BigDecimal totalAmountSpent = BigDecimal.ZERO;
        int numberOfInstallmentsPaid = 0;
        LocalDate currentDate = LocalDate.now();
        List<LoanInstallment> loanInstallmentList = loanInstallmentRepository
                .findByLoanIdAndIsPaidOrderByDueDateAsc(loan.getId(), false);
        for (LoanInstallment loanInstallment : loanInstallmentList) {
            if ((loanInstallment.getDueDate().getMonthValue() - currentDate.getMonthValue() >= 3) ||
                    (payLoanRequest.getAmount().subtract(totalAmountSpent).compareTo(loanInstallment.getAmount()) < 0)) {
                break;
            }
            loanInstallment.setPaidAmount(loanInstallment.getAmount());
            loanInstallment.setPaymentDate(currentDate);
            loanInstallment.setIsPaid(true);
            loanInstallmentRepository.save(loanInstallment);
            numberOfInstallmentsPaid++;
            totalAmountSpent = totalAmountSpent.add(loanInstallment.getAmount());
        }
        boolean isLoanPaid = numberOfInstallmentsPaid == loanInstallmentList.size();
        if (totalAmountSpent.compareTo(BigDecimal.ZERO) > 0) {
            loan.setIsPaid(isLoanPaid);
            Customer customer = loan.getCustomer();
            customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(totalAmountSpent));
            loanRepository.save(loan);
        }

        return PayLoanResponse.builder()
                .numberOfInstallmentsPaid(numberOfInstallmentsPaid)
                .totalAmountSpent(totalAmountSpent)
                .isLoanPaid(isLoanPaid)
                .build();
    }
}
