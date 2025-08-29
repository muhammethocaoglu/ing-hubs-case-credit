package com.casestudy.credit.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListLoanRequest {
    @NotNull(message = "Customer id is required")
    private Long customerId;

    private Integer numberOfInstallment;

    private Boolean isPaid;
}
