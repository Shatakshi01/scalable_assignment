package com.scalable.verification;


import com.scalable.verification.dto.VerificationDto;
import com.scalable.verification.entity.Kyc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//@RequestMapping("/kyc")
public class VerificationController {


    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody VerificationDto request) {
        verificationService.saveDetails(request);
        return ResponseEntity.ok("Saved");
    }

    @PostMapping("/kyc")
    public ResponseEntity<?> processKyc(@RequestBody VerificationDto verificationDto) {
        try {
            // Process verification
            verificationService.saveDetails(verificationDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("KYC processing failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/kyc/{loanId}")
    public Kyc get(@PathVariable Long loanId) {
        return verificationService.get(loanId);
    }
}
