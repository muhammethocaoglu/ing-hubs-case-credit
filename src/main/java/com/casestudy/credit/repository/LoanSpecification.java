package com.casestudy.credit.repository;

import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.entity.Loan;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecification {
    public static Specification<Loan> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Loan, Customer> customerJoin = root.join("customer");
            return criteriaBuilder.equal(customerJoin.<Long> get("id"), customerId);
        };

    }

    public static Specification<Loan> hasNumberOfInstallment(Integer numberOfInstallment) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("numberOfInstallment"), numberOfInstallment);
    }

    public static Specification<Loan> hasIsPaid(Boolean isPaid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isPaid"), isPaid);
    }
}