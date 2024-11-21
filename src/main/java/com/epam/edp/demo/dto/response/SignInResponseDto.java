package com.epam.edp.demo.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponseDto {
    private String email;
    private String role;
    private String token;
}
