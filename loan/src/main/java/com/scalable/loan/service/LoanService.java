package com.scalable.loan.service;

import com.scalable.loan.dto.AddressDto;
import com.scalable.loan.dto.CreditDto;
import com.scalable.loan.dto.CustomerDTo;
import com.scalable.loan.dto.VerificationDto;
import com.scalable.loan.entity.Loan;
import com.scalable.loan.repository.LoanRepository;
import com.scalable.loan.requests.LoanRequest;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LoanService {

    private final WebClient customerWebClient;
    private final WebClient creditWebClient;
    private final WebClient verificationWebClient;

    private final LoanRepository loanRepository;
    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    public LoanService(WebClient customerServiceWebClient,
                       WebClient creditServiceWebClient,
                       WebClient verificationServiceWebClient,
                       LoanRepository loanRepository) {
        this.customerWebClient = customerServiceWebClient;
        this.creditWebClient = creditServiceWebClient;
        this.verificationWebClient = verificationServiceWebClient;
        this.loanRepository = loanRepository;
    }
    public Long applyLoan(LoanRequest loanRequest) {

        Loan loan = Loan.builder()
            .loanAmount(loanRequest.getLoanAmount())
            .loanBranch(loanRequest.getLoanBranch())
            .build();
        Loan savedLoan = loanRepository.save(loan);

        try {
            saveVerificationDetails(loanRequest, savedLoan.getId()).block();
            saveCreditDetails(loanRequest, savedLoan.getId()).block();
            saveCustomerData(loanRequest , savedLoan.getId()).block();
        } catch (Exception e) {
            loanRepository.delete(savedLoan);
            throw new RuntimeException("Failed to process loan application", e);
        }


        return savedLoan.getId();
    }

    private Mono<Void> saveCustomerData(LoanRequest loanRequest , Long loanId) {
        log.info("Calling Customer service with data: {}", loanRequest.getCustomerDetails());
        return customerWebClient.post()
            .uri("/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCustomerDto(loanRequest.getCustomerDetails(),loanId))
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                    .map(errorBody -> new RuntimeException(
                        "Customer service error: " + response.statusCode() + " - " + errorBody
                    ))
            )
            .bodyToMono(Void.class);
    }

    private CustomerDTo buildCustomerDto(CustomerDTo customerDetails, Long loanId) {
        List<AddressDto> addressDetails = customerDetails.getAddress().stream()
            .map(addressDto -> AddressDto.builder()
                .firstLine(addressDto.getFirstLine())
                .secondLine(addressDto.getSecondLine())
                .build())
            .collect(Collectors.toList());

        return CustomerDTo.builder()
            .loanId(loanId)
            .firstName(customerDetails.getFirstName())
            .secondName(customerDetails.getSecondName())
            .address(addressDetails)
            .build();
    }

    private Mono<Void> saveCreditDetails(LoanRequest loanRequest, Long loanId) {
        log.info("CALIING CREDIT SERVICE WITH data {}" , loanRequest.getCardNumber());
        return creditWebClient.post()
            .uri("/credit")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreditDetails(loanRequest, loanId))
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                    .map(errorBody -> new RuntimeException(
                        "Credit service error: " + response.statusCode() + " - " + errorBody
                    ))
            )
            .bodyToMono(Void.class);
    }

    private Mono<Void> saveVerificationDetails(LoanRequest loanRequest, Long loanId) {
        VerificationDto verificationDto = buildKyRequest(loanRequest, loanId);
        log.info("Calling Verification service with: {}", verificationDto);

        return verificationWebClient.post()
            .uri("/kyc")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(verificationDto)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> {
                    log.error("Verification service failed with status: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                        .defaultIfEmpty("(no error body)")
                        .map(errorBody -> {
                            log.error("Verification service error response: {}", errorBody);
                            return new RuntimeException("Verification service error: " +
                                response.statusCode() + " - " + errorBody);
                        });
                })
            .bodyToMono(Void.class)
            .doOnError(e -> log.error("Failed to call Verification service", e))
            .doOnSuccess(v -> log.info("Successfully called Verification service"));
    }
    private VerificationDto buildKyRequest(LoanRequest loanRequest, Long id) {
        return VerificationDto.builder()
            .loanId(id)
            .aadhar(loanRequest.getAadhar())
            .pan(loanRequest.getPan())
            .build();
    }

    private CreditDto buildCreditDetails(LoanRequest loanRequest, Long id) {
                return CreditDto.builder()
            .cardNumber(loanRequest.getCardNumber())
            .cardOwner(loanRequest.getCardOwner())
            .loanId(id)
            .build();
    }

    public Loan getLoanDetails(Long loanId) {
        return loanRepository.findById(loanId).orElse(null);
    }

    public Mono<String> callverificationService() {
        return verificationWebClient.get()
            .uri("/actuator/health") // Or any simple endpoint
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorResume(e -> {
                log.error("Cannot connect to Verification service: {}", e.getMessage());
                return Mono.just("Connection failed: " + e.getMessage());
            });
    }
}

