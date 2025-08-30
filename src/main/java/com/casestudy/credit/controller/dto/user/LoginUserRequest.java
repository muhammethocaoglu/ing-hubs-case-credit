package com.casestudy.credit.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginUserRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
