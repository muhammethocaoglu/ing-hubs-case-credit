package com.casestudy.credit.service;

import com.casestudy.credit.controller.dto.user.ListUserResponseItem;
import com.casestudy.credit.controller.dto.user.RegisterUserRequest;
import com.casestudy.credit.controller.dto.user.UserResponse;
import com.casestudy.credit.entity.RoleEnum;
import com.casestudy.credit.entity.User;
import com.casestudy.credit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ListUserResponseItem> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> ListUserResponseItem.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getAuthorities())
                        .accountNonExpired(user.isAccountNonExpired())
                        .accountNonLocked(user.isAccountNonLocked())
                        .credentialsNonExpired(user.isCredentialsNonExpired())
                        .enabled(user.isEnabled())
                        .build())
                .toList();
    }

    public UserResponse createAdministrator(RegisterUserRequest registerUserRequest) {
        User user = userRepository.save(User.builder()
                .name(registerUserRequest.getName())
                .surname(registerUserRequest.getSurname())
                .email(registerUserRequest.getEmail())
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .role(RoleEnum.ADMIN)
                .build());
        return UserResponse.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .build();
    }
}
