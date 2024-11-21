package com.epam.edp.demo.service;



import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.epam.edp.demo.dto.request.SignInRequestDto;
import com.epam.edp.demo.dto.response.SignInResponseDto;
import com.epam.edp.demo.exception.subexceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignInServiceImpl implements SignInService{
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final DynamoDB dynamoDB;
    @Override
    public SignInResponseDto signIn(SignInRequestDto signInDto) {
        if (signInDto == null) {
            throw new ValidationException("Sign-in request cannot be null");
        }

        if (signInDto.getEmail() == null || signInDto.getEmail().isEmpty()) {
            throw new ValidationException("Email cannot be empty or null");
        }

        if (signInDto.getPassword() == null || signInDto.getPassword().isEmpty()) {
            throw new ValidationException("Password cannot be empty or null");
        }
        Authentication authentication;
            authentication = this.authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(signInDto.getEmail(),
                    signInDto.getPassword()));

        Table userTable = dynamoDB.getTable("registration");
        Item item = userTable.getItem("email", signInDto.getEmail());
        if (item == null) {
            throw new IllegalArgumentException("User not found");
        }
        String role = item.getString("role");
        System.out.println(signInDto.getEmail());
        String token = this.tokenService.generateToken(authentication);
        return SignInResponseDto.builder().role(role).email(signInDto.getEmail()).token(token).build();
    }
}
