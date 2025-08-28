package com.casestudy.credit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private BigDecimal creditLimit;

    private BigDecimal usedCreditLimit;

    @Builder.Default
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<Loan> loans = new HashSet<>();
}
