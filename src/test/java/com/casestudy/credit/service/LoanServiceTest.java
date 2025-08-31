package com.casestudy.credit.service;

import com.casestudy.credit.config.AuthenticationService;
import com.casestudy.credit.controller.dto.customer.CreateCustomerRequest;
import com.casestudy.credit.controller.dto.customer.CreateCustomerResponse;
import com.casestudy.credit.controller.dto.loan.CreateLoanRequest;
import com.casestudy.credit.controller.dto.loan.CreateLoanResponse;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.exception.BusinessException;
import com.casestudy.credit.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest()
@RunWith(SpringRunner.class)
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void test_should_return_create_loan_response_when_create() {
        // given
        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("John")
                .surname("Doe")
                .creditLimit(BigDecimal.valueOf(1000))
                .build();
        CreateCustomerResponse createCustomerResponse = customerService.create(createCustomerRequest);
        User user = User.builder()
                .name(createCustomerRequest.getName())
                .surname(createCustomerRequest.getSurname())
                .email("jd@email.com")
                .password("123")
                .role(RoleEnum.CUSTOMER)
                .build();
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(createCustomerResponse.getId())
                .amount(BigDecimal.ONE)
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        // when
        CreateLoanResponse createLoanResponse = loanService.create(createLoanRequest);
        // then
        assertEquals(1L, createLoanResponse.getId());
    }

    @Test
    public void test_should_return_error_when_create_if_customer_does_not_exist() {
        // given
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(1L)
                .amount(BigDecimal.ONE)
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();

        // when
        // then
        assertThrows(ResourceNotFoundException.class, () ->
                loanService.create(createLoanRequest)
        );
    }

    @Test
    public void test_should_return_error_when_create_if_credit_limit_is_exceeded() {
        // given
        CreateCustomerRequest createCustomerRequest = CreateCustomerRequest.builder()
                .name("John")
                .surname("Doe")
                .creditLimit(BigDecimal.valueOf(1000))
                .build();
        CreateCustomerResponse createCustomerResponse = customerService.create(createCustomerRequest);
        User user = User.builder()
                .name(createCustomerRequest.getName())
                .surname(createCustomerRequest.getSurname())
                .email("jd@email.com")
                .password("123")
                .role(RoleEnum.CUSTOMER)
                .build();
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(createCustomerResponse.getId())
                .amount(new BigDecimal("1000"))
                .interestRate(new BigDecimal("0.1"))
                .numberOfInstallment(6)
                .build();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        // when
        // then
        assertThrows(BusinessException.class, () ->
                loanService.create(createLoanRequest)
        );
    }
}
