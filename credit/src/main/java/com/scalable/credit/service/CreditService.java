package com.scalable.credit.service;


import com.scalable.credit.repository.CreditRepository;
import com.scalable.credit.entity.Credit;
import com.scalable.credit.request.CreditRequest;
import org.springframework.stereotype.Service;

@Service
public class CreditService {

    private final CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public void saveDetails(CreditRequest request) {
        creditRepository.save(Credit.builder()
                .loanId(request.getLoanId())
                .cardOwner(request.getCardOwner())
                .cardNumber(request.getCardNumber())
                .build());
    }

    public Credit get(Long loanId) {
        return creditRepository.findByLoanId(loanId);
    }
}
