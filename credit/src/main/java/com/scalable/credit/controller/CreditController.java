package com.scalable.credit.controller;

import com.scalable.credit.entity.Credit;
import com.scalable.credit.request.CreditRequest;
import com.scalable.credit.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//@RequestMapping("/credit")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping("/credit")
    public ResponseEntity<String> save(@RequestBody CreditRequest request) {
        try
        {
            creditService.saveDetails(request);
        return ResponseEntity.ok("Saved");
        } catch (Exception e) {
        log.error("KYC processing failed", e);
        return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/credit/{loanId}")
    public Credit get(@PathVariable Long loanId) {
        return creditService.get(loanId);
    }
}
