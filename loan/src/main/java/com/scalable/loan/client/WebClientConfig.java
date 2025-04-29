package com.scalable.loan.client;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {

    @Value("${customer-service.url}")
    private String customerServiceUrl;

    @Value("${credit-service.url}")
    private String creditServiceUrl;

    @Value("${verification-service.url}")
    private String verificationServiceUrl;

    @Bean
    public WebClient customerServiceWebClient() {
        System.out.println("Customer URL: " + customerServiceUrl);
        return WebClient.create(customerServiceUrl);
    }

    @Bean
    public WebClient creditServiceWebClient() {
        System.out.println("Credit URL: " + creditServiceUrl);
        return WebClient.create(creditServiceUrl);
    }

    @Bean
    public WebClient verificationServiceWebClient() {
        return WebClient.builder()
            .baseUrl(verificationServiceUrl)
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
            ))
            .build();
    }
}
