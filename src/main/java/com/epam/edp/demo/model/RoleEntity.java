package com.epam.edp.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "tm2-roles-db")
public class RoleEntity {
    @DynamoDBAttribute(attributeName = "email")
    @DynamoDBHashKey
    private String email;
    @DynamoDBAttribute(attributeName = "role")
    private String role;

}
