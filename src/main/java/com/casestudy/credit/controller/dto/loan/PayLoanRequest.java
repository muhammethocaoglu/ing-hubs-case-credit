package com.casestudy.credit.controller.dto.loan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

import static com.casestudy.credit.util.Constants.MIN_PAY_LOAN_AMOUNT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayLoanRequest {

    @NotNull(message = "Loan id is required")
    private Long id;

    @DecimalMin(value = MIN_PAY_LOAN_AMOUNT, inclusive = false)
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
