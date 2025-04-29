package com.scalable.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreditDto {
    private String cardOwner;
    private String cardNumber;
    private Long loanId;
}
