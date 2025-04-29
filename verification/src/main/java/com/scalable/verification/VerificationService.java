package com.scalable.verification;


import com.scalable.verification.dto.VerificationDto;
import com.scalable.verification.entity.Kyc;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final KycRepository kycRepository;

    public VerificationService(KycRepository kycRepository) {
        this.kycRepository = kycRepository;
    }

    public void saveDetails(VerificationDto request) {
        kycRepository.save(Kyc.builder()
                .loanId(request.getLoanId())
                .aadhar(request.getAadhar())
                .pan(request.getPan())
            .build());

    }

    public Kyc get(Long loanId) {
        return kycRepository.findByLoanId(loanId);
    }
}
