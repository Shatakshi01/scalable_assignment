package com.scalable.loan.controller;

import com.scalable.loan.dto.LoanDetails;
import com.scalable.loan.requests.LoanRequest;
import com.scalable.loan.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan")
public class LoanController {


    public final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Long> applyLoan(@RequestBody LoanRequest request) {

        Long loanId = loanService.applyLoan(request);
        return ResponseEntity.ok(loanId);
    }

    @GetMapping("/{loanId}")
    public LoanDetails getLoan(@PathVariable Long loanId) {
        return loanService.getLoanDetails(loanId);
    }

}
