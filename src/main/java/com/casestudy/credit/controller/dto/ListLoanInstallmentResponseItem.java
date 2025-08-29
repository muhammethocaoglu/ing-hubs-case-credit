package com.casestudy.credit.controller.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListLoanInstallmentResponseItem {

    private Long id;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    private LocalDate dueDate;

    private LocalDate paymentDate;

    private Boolean isPaid;
}
