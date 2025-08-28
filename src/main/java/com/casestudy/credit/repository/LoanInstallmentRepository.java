package com.casestudy.credit.repository;

import com.casestudy.credit.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

}
