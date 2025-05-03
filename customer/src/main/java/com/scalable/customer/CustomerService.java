package com.scalable.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.customer.entity.AddressDetails;
import com.scalable.customer.entity.Customer;
import com.scalable.customer.repository.CustomerRepository;
import com.scalable.customer.request.AddressDto;
import com.scalable.customer.request.CustomerDTO;
import com.scalable.customer.request.CustomerRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void saveCustomer(CustomerRequest customer){

        customerRepository.save(buildCustomer(customer));
    }

    private Customer buildCustomer(CustomerRequest customerRequest) {

        Customer customer = Customer.builder()
            .loanId(customerRequest.getLoanId())
            .firstName(customerRequest.getFirstName())
            .secondName(customerRequest.getSecondName())
            .build();
        customer.setAddresses(buildAddressDetails(customerRequest.getAddress() , customer));
        return customer;
    }

    private List<AddressDetails> buildAddressDetails(List<AddressDto> addressDTOList , Customer customer) {
        if (addressDTOList == null || addressDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        return addressDTOList.stream()
            .map(dto -> AddressDetails.builder()
                .firstLine(dto.getFirstLine())
                .secondLine(dto.getSecondLine())
                .customer(customer)
                .build())
            .toList();
    }

    public CustomerDTO getCustomerDetails(Long loanId) {
        Customer customer = customerRepository.findByLoanId(loanId);

        List<AddressDto> address = customer.getAddresses().stream().map(dto -> AddressDto.builder()
            .firstLine(dto.getFirstLine())
            .secondLine(dto.getSecondLine())
            .build()).toList();
        CustomerDTO customerDTO = CustomerDTO.builder()
            .firstName(customer.getFirstName())
            .secondName(customer.getSecondName())
            .addresses(address)
            .build();
        try {
            log.info("CustomerDTO to return: {}", new ObjectMapper().writeValueAsString(customerDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return customerDTO;
    }
}
