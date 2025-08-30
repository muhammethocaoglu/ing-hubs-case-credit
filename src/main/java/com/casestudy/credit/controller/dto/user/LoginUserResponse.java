package com.casestudy.credit.controller.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginUserResponse {

    private String token;

    private Long expiresIn;
}
