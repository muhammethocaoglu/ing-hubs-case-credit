package com.casestudy.credit.controller;

import com.casestudy.credit.controller.dto.*;
import com.casestudy.credit.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("")
    public ResponseEntity<CreateLoanResponse> create(@Valid @RequestBody CreateLoanRequest createLoanRequest) {
        return new ResponseEntity<>(loanService.create(createLoanRequest), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<ListLoanResponseItem>> list(@Valid ListLoanRequest listLoanRequest) {
        return new ResponseEntity<>(loanService.list(listLoanRequest), HttpStatus.OK);
    }

    @PostMapping("pay")
    public ResponseEntity<PayLoanResponse> pay(@Valid @RequestBody PayLoanRequest payLoanRequest) {
        return new ResponseEntity<>(loanService.pay(payLoanRequest), HttpStatus.OK);
    }
}
