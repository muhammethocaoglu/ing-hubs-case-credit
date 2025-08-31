package com.casestudy.credit.controller;

import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.repository.UserRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
class UserControllerTest {
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
    void test_should_return_success_when_retrieveAuthenticatedUser() throws Exception {
        // given
        User testUser = userRepository.save(User.builder()
                .name("Test")
                .surname("Other")
                .email("to@email.com")
                .password("pass")
                .role(RoleEnum.CUSTOMER)
                .build());

        // when
        // then
        mockMvc.perform(get("/api/v1/users/current")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name", is("Test")));
    }

    @Test
    void test_should_return_success_when_list() throws Exception {
        // given
        User testUser = userRepository.save(User.builder()
                .name("Test")
                .surname("Other")
                .email("to@email.com")
                .password("pass")
                .role(RoleEnum.CUSTOMER)
                .build());
        User admin = setupUser();

        // when
        // then
        mockMvc.perform(get("/api/v1/users")
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void test_should_return_success_when_create_administrator() throws Exception {
        // given
        User adminUser = setupUser();

        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .name("Other")
                .surname("Admin")
                .email("oa@email.com")
                .password("pass")
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
                        .accept(MediaType.APPLICATION_JSON).with(user(adminUser)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is(registerUserRequest.getName())));
    }

}
