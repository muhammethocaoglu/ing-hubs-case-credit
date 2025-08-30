package com.casestudy.credit.controller;

import com.casestudy.credit.config.AuthenticationService;
import com.casestudy.credit.controller.dto.user.LoginUserRequest;
import com.casestudy.credit.controller.dto.user.LoginUserResponse;
import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserRequest registerUserRequest) {
        return new ResponseEntity<>(authenticationService.register(registerUserRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest loginUserRequest) {
        User authenticatedUser = authenticationService.login(loginUserRequest);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok(LoginUserResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build());
    }
}
