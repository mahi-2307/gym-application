package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.epam.edp.demo.dto.request.RoleDto;
import com.epam.edp.demo.dto.request.UserRequestDto;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;
import com.epam.edp.demo.exception.subexceptions.ValidationException;
import com.epam.edp.demo.model.LoginEntity;
import com.epam.edp.demo.model.UserEntity;

import com.epam.edp.demo.validators.EmailValidator;
import com.epam.edp.demo.validators.PasswordValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    @Autowired
    private DynamoDB dynamoDB;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    UserEntity userEntity;
    @Autowired
    LoginEntity loginEntity;

    @Override
    public UserRequestDto signup(UserRequestDto signupDto) {
        try {
            // Validate email format
            String password = signupDto.getPassword();
            String email = signupDto.getEmail();
            if (!EmailValidator.validateEmail(email)) {
                throw new ValidationException("Invalid email format");
            }

            // Validate other fields
            if (signupDto.getFullName().isEmpty()) {
                throw new ValidationException("Full name cannot be empty");
            }

            if (signupDto.getPreferableActivity().isEmpty()) {
                throw new ValidationException("Preferable activity cannot be empty");
            }

            if (signupDto.getTarget().isEmpty()) {
                throw new ValidationException("Target cannot be empty");
            }

            // Validate password
            if (!PasswordValidator.validatePassword(password)) {
                throw new ValidationException("Password must contain at least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character");
            }

            // Check if the user already exists
            if (checkIfUserExists(signupDto.getEmail())) {
                throw new UserNotFoundException("User already exists");
            }

            // Get user role based on email
            RoleDto roleDto = roleService.getRoleByEmail(signupDto.getEmail());
            String role = roleDto != null && roleDto.getRole() != null ? roleDto.getRole() : "client"; // Default to "client" if no role found

            // Assign role based on the fetched role
            if (role.equals("admin")) {
                signupDto.setRole("admin");
            } else if (role.equals("coach")) {
                signupDto.setRole("coach");
            } else {
                signupDto.setRole("client");
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            signupDto.setPassword(passwordEncoder.encode(password));

            // Convert SignupDto to UserEntity and save to DynamoDB
            UserEntity signUp = objectMapper.convertValue(signupDto, UserEntity.class);
            userEntity.setEmail(signUp.getEmail());
            userEntity.setFullName(signUp.getFullName());
            userEntity.setPassword(signUp.getPassword());
            userEntity.setRole(signUp.getRole());
            userEntity.setTarget(signUp.getTarget());
            userEntity.setPreferableActivity(signUp.getPreferableActivity());
            dynamoDBMapper.save(userEntity);
            loginEntity.setEmail(userEntity.getEmail());
            loginEntity.setPassword(userEntity.getPassword());
            dynamoDBMapper.save(loginEntity);
            return objectMapper.convertValue(userEntity, UserRequestDto.class);

        } catch (ValidationException | UserNotFoundException e) {
            // Handle custom validation and user existence exceptions
            throw e;
        } catch (Exception e) {
            // Catch other potential exceptions
            throw new RuntimeException("An error occurred during signup", e);
        }
    }

    @Override
    public boolean checkIfUserExists(String email) {
        try {
            Table table = dynamoDB.getTable("registration");
            Item item = table.getItem("email", email);
            return item != null;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while checking if the user exists", e);
        }
    }
}
