package com.casestudy.credit.repository;

import com.casestudy.credit.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanId(Long loanId);

    List<LoanInstallment> findByLoanIdAndIsPaidOrderByDueDateAsc(Long loanId, Boolean isPaid);
}
