package com.casestudy.credit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "loan")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private Set<LoanInstallment> loanInstallments;

    private BigDecimal loanAmount;

    private Integer numberOfInstallment;

    @Column(nullable = false, updatable = false)
    protected LocalDate createdDate;

    @Builder.Default
    private Boolean isPaid = false;
}
