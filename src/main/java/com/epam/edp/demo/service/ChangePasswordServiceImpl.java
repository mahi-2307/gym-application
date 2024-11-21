package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.epam.edp.demo.dto.request.ChangePasswordDto;
import com.epam.edp.demo.exception.subexceptions.ValidationException;
import com.epam.edp.demo.model.LoginEntity;
import com.epam.edp.demo.model.UserEntity;
import com.epam.edp.demo.utils.JwtUtils;
import com.epam.edp.demo.validators.PasswordChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService{

    private final DynamoDBMapper dynamoDBMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;
    @Override
    public void changePassword(String token, ChangePasswordDto changePasswordDto) {

        String email = jwtUtils.extractEmail(token);
        // Retrieve user by email
        UserEntity userEntity = dynamoDBMapper.load(UserEntity.class, email);
        if (userEntity == null) {
            throw new ValidationException("User not found");
        }
        if (!bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), userEntity.getPassword())) {
            throw new ValidationException("The provided old password does not match the current password");
        }
        // Validate new password format
        PasswordChecker.validatePassword(changePasswordDto.getNewPassword());


        // Check if newPassword matches confirmPassword
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new ValidationException("New password and confirm password do not match");
        }
        if(changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())){
            throw new ValidationException("New password must be different from old password");
        }


        String encodedNewPassword = bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword());
        // Update userEntity and loginEntity with the new password
        userEntity.setPassword(encodedNewPassword);
        dynamoDBMapper.save(userEntity);

        LoginEntity loginEntity = dynamoDBMapper.load(LoginEntity.class, email);
        loginEntity.setPassword(encodedNewPassword);
        dynamoDBMapper.save(loginEntity);
    }
}
