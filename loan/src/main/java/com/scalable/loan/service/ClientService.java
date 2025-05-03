package com.scalable.loan.service;


import com.scalable.loan.dto.AddressDto;
import com.scalable.loan.dto.CreditDto;
import com.scalable.loan.dto.CustomerDTo;
import com.scalable.loan.dto.LoanDetails;
import com.scalable.loan.dto.VerificationDto;
import com.scalable.loan.requests.LoanRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientService {

    private final WebClient customerWebClient;
    private final WebClient creditWebClient;
    private final WebClient verificationWebClient;

    public ClientService(WebClient customerServiceWebClient,
                         WebClient creditServiceWebClient,
                         WebClient verificationServiceWebClient) {
        this.customerWebClient = customerServiceWebClient;
        this.creditWebClient = creditServiceWebClient;
        this.verificationWebClient = verificationServiceWebClient;
    }

    public Mono<Void> saveCustomerData(LoanRequest loanRequest , Long loanId) {
        log.info("Calling Customer service with data: {}", loanRequest.getCustomerDetails());
        return customerWebClient.post()
            .uri("/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCustomerDto(loanRequest.getCustomerDetails(),loanId))
            .retrieve()
            .onStatus(
                httpStatusCode -> httpStatusCode.isError(),
                response -> response.bodyToMono(String.class)
                    .map(errorBody -> new RuntimeException(
                        "Customer service error: " + response.statusCode() + " - " + errorBody
                    ))
            )
            .bodyToMono(Void.class);
    }

    public Mono<Void> saveCreditDetails(LoanRequest loanRequest, Long loanId) {
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

    public Mono<Void> saveVerificationDetails(LoanRequest loanRequest, Long loanId) {
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


    public CustomerDTo getCustomer(Long loanId) {
        return customerWebClient
            .get()
            .uri("/customer/{loanId}", loanId)
            .retrieve()
            .bodyToMono(CustomerDTo.class).block();
    }

    public void debugGetCustomerRaw(Long loanId) {
        log.info("Calling Customer service TO GET DATA");
        customerWebClient
            .get()
            .uri("/customer/{loanId}", loanId)
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(json -> System.out.println("Raw Customer JSON: " + json))
            .doOnError(err -> System.err.println("Error: " + err.getMessage()))
            .subscribe();
    }
    public CreditDto getCredit(Long loanId) {
        return creditWebClient
            .get()
            .uri("/credit/{loanId}", loanId)
            .retrieve()
            .bodyToMono(CreditDto.class).block();
    }

    public VerificationDto getKyc(Long loanId) {
        return verificationWebClient
            .get()
            .uri("/kyc/{loanId}", loanId)
            .retrieve()
            .bodyToMono(VerificationDto.class)
            .block();
    }
    private VerificationDto buildKyRequest(LoanRequest loanRequest, Long id) {
        return VerificationDto.builder()
            .loanId(id)
            .aadhar(loanRequest.getAadhar())
            .pan(loanRequest.getPan())
            .build();
    }


    private CustomerDTo buildCustomerDto(CustomerDTo customerDetails, Long loanId) {
        List<AddressDto> addressDetails = customerDetails.getAddresses().stream()
            .map(addressDto -> AddressDto.builder()
                .firstLine(addressDto.getFirstLine())
                .secondLine(addressDto.getSecondLine())
                .build())
            .collect(Collectors.toList());

        return CustomerDTo.builder()
            .loanId(loanId)
            .firstName(customerDetails.getFirstName())
            .secondName(customerDetails.getSecondName())
            .addresses(addressDetails)
            .build();
    }

    private CreditDto buildCreditDetails(LoanRequest loanRequest, Long id) {
        return CreditDto.builder()
            .cardNumber(loanRequest.getCardNumber())
            .cardOwner(loanRequest.getCardOwner())
            .loanId(id)
            .build();
    }

}
