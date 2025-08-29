package com.casestudy.credit.service;

import com.casestudy.credit.controller.dto.ListLoanInstallmentResponseItem;
import com.casestudy.credit.repository.LoanInstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;

    public List<ListLoanInstallmentResponseItem> list(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId)
                .stream()
                .map(loanInstallment -> ListLoanInstallmentResponseItem.builder()
                        .id(loanInstallment.getId())
                        .amount(loanInstallment.getAmount())
                        .paidAmount(loanInstallment.getPaidAmount())
                        .dueDate(loanInstallment.getDueDate())
                        .paymentDate(loanInstallment.getPaymentDate())
                        .isPaid(loanInstallment.getIsPaid())
                        .build())
                .toList();
    }
}
