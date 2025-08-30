package com.casestudy.credit.service;

import com.casestudy.credit.controller.dto.customer.CreateCustomerRequest;
import com.casestudy.credit.controller.dto.customer.CreateCustomerResponse;
import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CreateCustomerResponse create(CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerRepository.save(Customer.builder()
                .name(createCustomerRequest.getName())
                .surname(createCustomerRequest.getSurname())
                .creditLimit(createCustomerRequest.getCreditLimit())
                .usedCreditLimit(BigDecimal.ZERO)
                .build());
        return CreateCustomerResponse.builder().id(customer.getId()).build();
    }
}
