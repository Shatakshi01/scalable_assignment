package com.scalable.loan.dto;


import com.scalable.loan.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoanDetails {
    private Loan loan;
    private CustomerDTo customer;
    private CreditDto credit;
    private VerificationDto kyc;

}
