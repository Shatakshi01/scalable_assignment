package com.scalable.loan.requests;

import com.scalable.loan.dto.CustomerDTo;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    private BigDecimal loanAmount;
    private String loanBranch;
    private String aadhar;
    private String pan;
    private String cardOwner;
    private String cardNumber;
    private CustomerDTo customerDetails;
}
