package com.casestudy.credit.controller;

import com.casestudy.credit.controller.dto.customer.CreateCustomerRequest;
import com.casestudy.credit.controller.dto.customer.CreateCustomerResponse;
import com.casestudy.credit.controller.dto.loan.CreateLoanRequest;
import com.casestudy.credit.controller.dto.loan.CreateLoanResponse;
import com.casestudy.credit.controller.dto.loan.ListLoanResponseItem;
import com.casestudy.credit.controller.dto.loan.PayLoanRequest;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LoanControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext).apply(springSecurity())
                .build();
    }

    public User setupUser() {
        return userRepository.save(User.builder()
                .name("Jack")
                .surname("Black")
                .email("jb@email.com")
                .password("pass")
                .role(RoleEnum.ADMIN)
                .build());
    }


    @Test
    void test_should_return_success_when_create_loan_get_loan_installments_and_pay_loan() throws Exception {
        // given
        User adminUser = setupUser();

        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("Mike")
                .surname("White")
                .creditLimit(BigDecimal.valueOf(10000L))
                .build();

        // when
        // then

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andReturn();
        CreateCustomerResponse createCustomerResponse = objectMapper
                .readValue(customerResult.getResponse().getContentAsString(),
                        CreateCustomerResponse.class);
        long customerId = createCustomerResponse.getId();
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(1000L))
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());

        createLoanRequest = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(5000L))
                .interestRate(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .build();

        MvcResult createLoanResult = mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty())
                .andReturn();

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("customerId", String.valueOf(customerId));
        requestParams.add("numberOfInstallment",
                String.valueOf(createLoanRequest.getNumberOfInstallment()));
        requestParams.add("isPaid", String.valueOf(false));
        MvcResult listLoansResult = mockMvc.perform(get("/api/v1/loans").params(requestParams).with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].amount", is(6000.0)))
                .andExpect(jsonPath("$[0].numberOfInstallment", is(createLoanRequest.getNumberOfInstallment())))
                .andExpect(jsonPath("$[0].createDate", is(LocalDate.now().toString())))
                .andReturn();

        CreateLoanResponse createLoanResponse = objectMapper
                .readValue(createLoanResult.getResponse().getContentAsString(),
                        CreateLoanResponse.class);

        mockMvc.perform(get(String.format("/api/v1/loans/%d/installments", createLoanResponse.getId()))
                        .with(user(adminUser)))
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
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("numberOfInstallmentsPaid", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("totalAmountSpent", greaterThan(0.0)))
                .andExpect(jsonPath("isLoanPaid", is(false)));

    }

    @Test
    void test_should_return_forbidden_when_create_loan_if_authorization_token_is_missing() throws Exception {
        // given
        User adminUser = setupUser();

        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("Mike")
                .surname("White")
                .creditLimit(BigDecimal.valueOf(10000L))
                .build();

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andReturn();

        CreateCustomerResponse createCustomerResponse = objectMapper
                .readValue(customerResult.getResponse().getContentAsString(),
                        CreateCustomerResponse.class);
        long customerId = createCustomerResponse.getId();

        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(customerId)
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
                .andExpect(status().isForbidden());

    }

    @Test
    void test_should_return_created_when_create_loan_if_user_with_customer_role_operates_for_himself() throws Exception {
        // given
        User adminUser = setupUser();

        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("Mike")
                .surname("White")
                .creditLimit(BigDecimal.valueOf(10000L))
                .build();

        User customerUser = userRepository.save(User.builder()
                .name(createCustomerRequest.getName())
                .surname(createCustomerRequest.getSurname())
                .email("mw@email.com")
                .password("123")
                .role(RoleEnum.CUSTOMER)
                .build());

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andReturn();

        CreateCustomerResponse createCustomerResponse = objectMapper
                .readValue(customerResult.getResponse().getContentAsString(),
                        CreateCustomerResponse.class);
        long customerId = createCustomerResponse.getId();

        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(1000L))
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();
        // when
        // then
        mockMvc.perform(post("/api/v1/loans").with(user(customerUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void test_should_return_forbidden_when_create_loan_if_user_with_customer_role_operates_for_other_customer() throws Exception {
        // given
        User adminUser = setupUser();
        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("Mick")
                .surname("Jagger")
                .creditLimit(BigDecimal.valueOf(10000L))
                .build();

        User otherUser = userRepository.save(User.builder()
                .name("Mike")
                .surname("White")
                .email("mw@email.com")
                .password("123")
                .role(RoleEnum.CUSTOMER)
                .build());

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andReturn();

        CreateCustomerResponse createCustomerResponse = objectMapper
                .readValue(customerResult.getResponse().getContentAsString(),
                        CreateCustomerResponse.class);
        long customerId = createCustomerResponse.getId();

        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(1000L))
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();
        // when
        // then
        mockMvc.perform(post("/api/v1/loans").with(user(otherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLoanRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
