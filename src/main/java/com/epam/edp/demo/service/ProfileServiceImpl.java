package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.epam.edp.demo.dto.request.UpdateRequestDto;
import com.epam.edp.demo.dto.response.UserResponseDto;
import com.epam.edp.demo.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    AmazonDynamoDB client;
    @Autowired
    DynamoDB dynamoDB;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    DynamoDBMapper dynamoDBMapper;

    public ResponseEntity<?> updateProfile(String authorizationHeader, UpdateRequestDto requestData) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        Table table = dynamoDB.getTable("registration");

        if (email == null || email.isEmpty()) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (!isValidFullName(requestData.getFullName())) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid full name format");
        }

        try {
            var updateItemSpec = createUpdateItemSpec(email, requestData);
            table.updateItem(updateItemSpec);
            UpdateRequestDto update = UpdateRequestDto.builder().fullName(requestData.getFullName()).target(requestData.getTarget()).preferableActivity(requestData.getPreferableActivity()).build();
            return createSuccessResponse(update);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update item");
        }
    }

    public ResponseEntity<?> getProfile(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        Table table = dynamoDB.getTable("registration");

        if (email == null || email.isEmpty()) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
        }

        try {
            Item user = table.getItem("email", email);
            UserResponseDto userDto = new UserResponseDto();
            userDto.setEmail(user.getString("email"));
            userDto.setFullName(user.getString("fullName"));
            userDto.setTarget(user.getString("target"));
            userDto.setPreferableActivity(user.getString("preferableActivity"));
            userDto.setRole(user.getString("role"));
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get item");
        }
    }

    private ResponseEntity<?> createSuccessResponse(UpdateRequestDto update) {
        return ResponseEntity.ok(update);
    }

    private ResponseEntity<?> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(status).body(error);
    }

    private boolean isValidFullName(String fullName) {
        String regex = "^[A-Za-z]+( [A-Za-z]+)*\\d{0,1}$";
        return fullName.matches(regex) && fullName.length() <= 100;
    }

    private UpdateItemSpec createUpdateItemSpec(String email, UpdateRequestDto requestData) {
        return new UpdateItemSpec()
                .withPrimaryKey("email", email)
                .withUpdateExpression("set fullName = :n, target = :t, preferableActivity = :p")
                .withValueMap(new ValueMap()
                        .withString(":n", requestData.getFullName())
                        .withString(":t", requestData.getTarget())
                        .withString(":p", requestData.getPreferableActivity()))
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }
}
