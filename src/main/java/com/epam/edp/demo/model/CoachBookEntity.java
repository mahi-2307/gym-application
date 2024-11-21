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
@DynamoDBTable(tableName = "tm2-coach-book-db")
public class CoachBookEntity {

    @DynamoDBHashKey(attributeName = "id")
    private int id;

    @DynamoDBAttribute(attributeName = "clientEmail")
    private String clientEmail;

    @DynamoDBAttribute(attributeName = "coachEmail")
    private String coachEmail;

    @DynamoDBAttribute(attributeName = "date")
    private String date;

    @DynamoDBAttribute(attributeName = "duration")
    private Integer duration;

    @DynamoDBAttribute(attributeName = "status")
    private String status;

    @DynamoDBAttribute(attributeName = "workoutType")
    private String workoutType;

}
