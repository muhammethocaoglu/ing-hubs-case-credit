package com.casestudy.credit.config;

import com.casestudy.credit.controller.dto.user.LoginUserRequest;
import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.exception.ResourceNotFoundException;
import com.casestudy.credit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public User register(RegisterUserRequest registerUserRequest) {
        return userRepository.findByEmail(registerUserRequest.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .name(registerUserRequest.getName())
                        .surname(registerUserRequest.getSurname())
                        .email(registerUserRequest.getEmail())
                        .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                        .role(RoleEnum.CUSTOMER)
                        .build()));
    }

    public User login(LoginUserRequest loginUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getEmail(),
                        loginUserRequest.getPassword()
                )
        );

        return userRepository.findByEmail(loginUserRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with email %s not found",
                        loginUserRequest.getEmail())));
    }
}
