package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Component
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final DynamoDB dynamoDB;
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        // Extract the email from the Authentication's principal (assuming the principal has getUsername())
        String email = authentication.getName();
        Table userTable = dynamoDB.getTable("registration");
        Item item = userTable.getItem("email", email);
        if (item == null) {
            throw new IllegalArgumentException("User not found");
        }
        String role = item.getString("role");
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tm2-admin")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .claim("email", email)
                .claim("role", role)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
