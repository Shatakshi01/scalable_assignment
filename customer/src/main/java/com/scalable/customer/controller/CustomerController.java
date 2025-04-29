package com.scalable.customer.controller;

import com.scalable.customer.CustomerService;
import com.scalable.customer.entity.Customer;
import com.scalable.customer.repository.CustomerRepository;
import com.scalable.customer.request.CustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<String> saveCustomer(@RequestBody CustomerRequest customer) {
        customerService.saveCustomer(customer);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/{loanId}")
    public Customer getCustomer(@PathVariable Long loanId) {
        return customerService.getCustomerDetails(loanId);
    }
}
