package com.epam.edp.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotNull(message = "Email cannot be null")
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "Full name cannot be null")
    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @NotNull(message = "Target cannot be null")
    @NotEmpty(message = "Target cannot be empty")
    private String target;

    @NotNull(message = "Preferable activity cannot be null")
    @NotEmpty(message = "Preferable activity cannot be empty")
    private String preferableActivity;

    @NotNull(message = "Role cannot be null")
    @NotEmpty(message = "Role cannot be empty")
    private String role;
}
