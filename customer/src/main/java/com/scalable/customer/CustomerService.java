package com.scalable.customer;

import com.scalable.customer.entity.AddressDetails;
import com.scalable.customer.entity.Customer;
import com.scalable.customer.repository.CustomerRepository;
import com.scalable.customer.request.AddressDto;
import com.scalable.customer.request.CustomerRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
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
            .collect(Collectors.toList());
    }

    public Customer getCustomerDetails(Long loanId) {
        return customerRepository.findByLoanId(loanId);
    }
}
