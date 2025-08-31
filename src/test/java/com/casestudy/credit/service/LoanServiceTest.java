package com.casestudy.credit.service;

import com.casestudy.credit.config.AuthenticationService;
import com.casestudy.credit.controller.dto.customer.CreateCustomerRequest;
import com.casestudy.credit.controller.dto.customer.CreateCustomerResponse;
import com.casestudy.credit.controller.dto.loan.CreateLoanRequest;
import com.casestudy.credit.controller.dto.loan.CreateLoanResponse;
import com.casestudy.credit.controller.dto.loan.PayLoanRequest;
import com.casestudy.credit.controller.dto.loan.PayLoanResponse;
import com.casestudy.credit.entity.*;
import com.casestudy.credit.exception.BusinessException;
import com.casestudy.credit.exception.ResourceNotFoundException;
import com.casestudy.credit.repository.CustomerRepository;
import com.casestudy.credit.repository.LoanInstallmentRepository;
import com.casestudy.credit.repository.LoanRepository;
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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

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

    @Test
    public void test_should_return_pay_loan_response_when_pay_if_installment_is_paid_after_due_date() {
        // given
        Customer customer = Customer.builder()
                .name("John")
                .surname("Doe")
                .creditLimit(BigDecimal.valueOf(1000))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();
        User user = User.builder()
                .name(customer.getName())
                .surname(customer.getSurname())
                .email("jd@email.com")
                .password("123")
                .role(RoleEnum.CUSTOMER)
                .build();
        Loan loan = Loan.builder()
                .id(1L)
                .customer(customer)
                .loanAmount(new BigDecimal(600))
                .createdDate(LocalDate.now().minusMonths(12))
                .loanInstallments(Set.of(LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(11))
                                .amount(new BigDecimal(100)).build(),
                        LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(10))
                                .amount(new BigDecimal(100)).build(),
                        LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(9))
                                .amount(new BigDecimal(100)).build(),
                        LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(8))
                                .amount(new BigDecimal(100)).build(),
                        LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(7))
                                .amount(new BigDecimal(100)).build(),
                        LoanInstallment.builder()
                                .dueDate(LocalDate.now().minusMonths(6))
                                .amount(new BigDecimal(100)).build()))
                .numberOfInstallment(6)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        LoanRepository loanRepository = Mockito.mock(LoanRepository.class);
        LoanInstallmentRepository loanInstallmentRepository = Mockito.mock(LoanInstallmentRepository.class);
        CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        Mockito.when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        Mockito.when(loanInstallmentRepository.findByLoanIdAndIsPaidOrderByDueDateAsc(loan.getId(), false))
                .thenReturn(loan.getLoanInstallments().stream().sorted(Comparator.comparing(LoanInstallment::getDueDate)).toList());
        Mockito.when(loanRepository.save(loan)).thenReturn(loan);
        Mockito.when(loanInstallmentRepository.save(any(LoanInstallment.class)))
                .thenReturn(LoanInstallment.builder().build());
        PayLoanRequest payLoanRequest = PayLoanRequest.builder()
                .id(loan.getId())
                .amount(new BigDecimal(300))
                .build();
        LoanService loanService = new LoanService(customerRepository, loanRepository, loanInstallmentRepository);
        // when
        PayLoanResponse payLoanResponse = loanService.pay(payLoanRequest);
        // then
        assertEquals(2, payLoanResponse.getNumberOfInstallmentsPaid());
        assertEquals(false, payLoanResponse.getIsLoanPaid());
        assertTrue(payLoanResponse.getTotalAmountSpent().compareTo(new BigDecimal(200)) > 0);
    }
}
