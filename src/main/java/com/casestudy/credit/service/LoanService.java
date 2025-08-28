package com.casestudy.credit.service;

import com.casestudy.credit.controller.dto.CreateLoanRequest;
import com.casestudy.credit.controller.dto.CreateLoanResponse;
import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.entity.Loan;
import com.casestudy.credit.entity.LoanInstallment;
import com.casestudy.credit.exception.BusinessException;
import com.casestudy.credit.exception.ResourceNotFoundException;
import com.casestudy.credit.repository.CustomerRepository;
import com.casestudy.credit.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;

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


}
