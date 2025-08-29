package com.casestudy.credit.controller;

import com.casestudy.credit.controller.dto.ListLoanInstallmentResponseItem;
import com.casestudy.credit.service.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/loans/{id}")
@RequiredArgsConstructor
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @GetMapping("/installments")
    public ResponseEntity<List<ListLoanInstallmentResponseItem>> list(@PathVariable Long id) {
        return new ResponseEntity<>(loanInstallmentService.list(id), HttpStatus.OK);
    }
}
