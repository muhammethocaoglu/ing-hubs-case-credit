package com.casestudy.credit.controller.dto.loan;

import com.casestudy.credit.controller.validator.OneOf;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.math.BigDecimal;

import static com.casestudy.credit.util.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateLoanRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @DecimalMin(value = MIN_INTEREST_RATE)
    @DecimalMax(value = MAX_INTEREST_RATE)
    @NotNull(message = "Interest rate is required")
    private BigDecimal interestRate;

    @NotNull(message = "Number of installment is required")
    @OneOf({6, 9, 12, 24})
    private int numberOfInstallment;
}
