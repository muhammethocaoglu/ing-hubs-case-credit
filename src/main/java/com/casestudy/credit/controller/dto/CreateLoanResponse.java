package com.casestudy.credit.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
