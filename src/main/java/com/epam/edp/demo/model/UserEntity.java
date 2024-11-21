package com.epam.edp.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "registration")
@Component
public class UserEntity {
    @DynamoDBHashKey(attributeName = "email")
    @DynamoDBAttribute(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "password")
    private String password;

    @DynamoDBAttribute(attributeName = "fullName")
    private String fullName;

    @DynamoDBAttribute(attributeName = "target")
    private String target;

    @DynamoDBAttribute(attributeName = "preferableActivity")
    private String preferableActivity;

    @DynamoDBAttribute(attributeName = "role")
    private String role;

}
