package com.casestudy.credit.controller.dto.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private String password;

    private Collection<? extends GrantedAuthority>  authorities;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;

    private Boolean enabled;
}
