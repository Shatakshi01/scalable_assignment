package com.scalable.loan.service;

import com.scalable.loan.dto.CreditDto;
import com.scalable.loan.dto.CustomerDTo;
import com.scalable.loan.dto.LoanDetails;
import com.scalable.loan.dto.VerificationDto;
import com.scalable.loan.entity.Loan;
import com.scalable.loan.repository.LoanRepository;
import com.scalable.loan.requests.LoanRequest;
import org.springframework.stereotype.Service;

@Service
public class LoanService {


    private final LoanRepository loanRepository;

    private final ClientService clientService;

    public LoanService(LoanRepository loanRepository, ClientService clientService) {
        this.loanRepository = loanRepository;
        this.clientService = clientService;
    }


    public Long applyLoan(LoanRequest loanRequest) {

        Loan loan = Loan.builder()
            .loanAmount(loanRequest.getLoanAmount())
            .loanBranch(loanRequest.getLoanBranch())
            .build();
        Loan savedLoan = loanRepository.save(loan);

        try {
            clientService.saveVerificationDetails(loanRequest, savedLoan.getId()).block();
            clientService.saveCreditDetails(loanRequest, savedLoan.getId()).block();
            clientService.saveCustomerData(loanRequest , savedLoan.getId()).block();
        } catch (Exception e) {
            loanRepository.delete(savedLoan);
            throw new RuntimeException("Failed to process loan application", e);
        }


        return savedLoan.getId();
    }


    public LoanDetails getLoanDetails(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        CustomerDTo customer = clientService.getCustomer(loanId);
//        clientService.debugGetCustomerRaw(loanId);
        CreditDto credit = clientService.getCredit(loanId);
        VerificationDto kyc = clientService.getKyc(loanId);


        return LoanDetails.builder()
            .loan(loan)
            .customer(customer)
            .credit(credit)
            .kyc(kyc)
            .build();
    }

}

