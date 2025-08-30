package com.casestudy.credit.controller.dto.loan;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListLoanResponseItem {

    private Long id;

    private BigDecimal amount;

    private Integer numberOfInstallment;

    private LocalDate createDate;

    private Boolean isPaid;
}
