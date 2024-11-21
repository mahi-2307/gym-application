package com.epam.edp.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamoDBTable(tableName = "tm2-feedback-db")
public class FeedbackEntity {
    @DynamoDBHashKey(attributeName = "feedback_id")
    private Integer feedback_id;
    @DynamoDBAttribute(attributeName = "booking_id")
    private Integer booking_id;
    @DynamoDBAttribute(attributeName = "rating")
    private Integer rating;
    @DynamoDBAttribute(attributeName = "notes")
    private String notes;
    @DynamoDBAttribute(attributeName = "createdAt")
    private String createdAt;
}
