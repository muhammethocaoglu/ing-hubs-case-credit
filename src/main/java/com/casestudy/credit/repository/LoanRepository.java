package com.casestudy.credit.repository;

import com.casestudy.credit.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
