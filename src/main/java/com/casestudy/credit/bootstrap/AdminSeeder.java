package com.casestudy.credit.bootstrap;

import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .name("Admin")
                .surname("User")
                .email("admin.user@email.com")
                .password("123456")
                .build();
        Optional<User> optionalUser = userRepository.findByEmail(registerUserRequest.getEmail());

        if (optionalUser.isPresent()) {
            return;
        }

        userRepository.save(User.builder()
                .name(registerUserRequest.getName())
                .surname(registerUserRequest.getSurname())
                .email(registerUserRequest.getEmail())
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .role(RoleEnum.ADMIN)
                .build());
    }
}
