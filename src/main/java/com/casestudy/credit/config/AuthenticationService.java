package com.casestudy.credit.config;

import com.casestudy.credit.controller.dto.user.LoginUserRequest;
import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public User register(RegisterUserRequest registerUserRequest) {
        return userRepository.save(User.builder()
                .name(registerUserRequest.getName())
                .surname(registerUserRequest.getSurname())
                .email(registerUserRequest.getEmail())
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .role(RoleEnum.CUSTOMER)
                .build());
    }

    public User login(LoginUserRequest loginUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getEmail(),
                        loginUserRequest.getPassword()
                )
        );

        return userRepository.findByEmail(loginUserRequest.getEmail())
                .orElseThrow();
    }
}
