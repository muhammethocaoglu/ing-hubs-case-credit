package com.casestudy.credit.controller.dto.loan;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayLoanResponse {

    private Integer numberOfInstallmentsPaid;

    private BigDecimal totalAmountSpent;

    private Boolean isLoanPaid;
}
