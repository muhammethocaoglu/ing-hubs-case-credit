package com.casestudy.credit.controller.dto;

import com.casestudy.credit.controller.validator.OneOf;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

import static com.casestudy.credit.util.Constants.MAX_INTEREST_RATE;
import static com.casestudy.credit.util.Constants.MIN_INTEREST_RATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateLoanResponse {

    @NotNull(message = "Id is required")
    private Long id;
}
