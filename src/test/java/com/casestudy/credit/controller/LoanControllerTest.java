package com.casestudy.credit.controller;

import com.casestudy.credit.CleanupH2DatabaseTestListener;
import com.casestudy.credit.controller.dto.CreateLoanRequest;
import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanupH2DatabaseTestListener.class})
class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void test_should_return_created_when_create_loan() throws Exception {
        // given
        Customer customer = Customer.builder()
                .name("Jack")
                .surname("Black")
                .creditLimit(BigDecimal.valueOf(10000L))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();
        customerRepository.save(customer);
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(customer.getId())
                .amount(BigDecimal.valueOf(1000L))
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();
        // when
        // then
        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());
        customerRepository.deleteById(customer.getId());
    }

}
