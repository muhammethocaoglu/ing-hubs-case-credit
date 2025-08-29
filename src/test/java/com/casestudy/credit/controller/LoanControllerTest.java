package com.casestudy.credit.controller;

import com.casestudy.credit.CleanupH2DatabaseTestListener;
import com.casestudy.credit.controller.dto.CreateLoanRequest;
import com.casestudy.credit.controller.dto.ListLoanResponseItem;
import com.casestudy.credit.controller.dto.PayLoanRequest;
import com.casestudy.credit.entity.Customer;
import com.casestudy.credit.repository.CustomerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class,
        CleanupH2DatabaseTestListener.class})
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

        createLoanRequest = CreateLoanRequest.builder()
                .customerId(customer.getId())
                .amount(BigDecimal.valueOf(5000L))
                .interestRate(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .build();

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("customerId", String.valueOf(customer.getId()));
        requestParams.add("numberOfInstallment",
                String.valueOf(createLoanRequest.getNumberOfInstallment()));
        requestParams.add("isPaid", String.valueOf(false));
        MvcResult listLoansResult = mockMvc.perform(get("/api/v1/loans").params(requestParams))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].amount", is(6000.0)))
                .andExpect(jsonPath("$[0].numberOfInstallment", is(createLoanRequest.getNumberOfInstallment())))
                .andExpect(jsonPath("$[0].createDate", is(LocalDate.now().toString())))
                .andReturn();


        mockMvc.perform(get("/api/v1/loans/2/installments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(createLoanRequest.getNumberOfInstallment())));

        List<ListLoanResponseItem> loanResponseItemList = objectMapper
                .readValue(listLoansResult.getResponse().getContentAsString(),
                        new TypeReference<>() {
                        });
        PayLoanRequest payLoanRequest = PayLoanRequest.builder()
                .id(loanResponseItemList.get(0).getId())
                .amount(new BigDecimal("6000"))
                .build();

        mockMvc.perform(post("/api/v1/loans/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payLoanRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("numberOfInstallmentsPaid", is(2)))
                .andExpect(jsonPath("totalAmountSpent", is(1000.0)))
                .andExpect(jsonPath("isLoanPaid", is(false)));
        customer = customerRepository.findById(customer.getId()).get();
        Assert.assertEquals(new BigDecimal("6100.00"), customer.getUsedCreditLimit());

    }

}
