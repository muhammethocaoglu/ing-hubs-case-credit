package com.casestudy.credit.controller.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateCustomerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotNull
    private BigDecimal creditLimit;
}