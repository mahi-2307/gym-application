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
@DynamoDBTable(tableName = "tm2-coach-feedback-db")
public class CoachFeedbackEntity {
    @DynamoDBHashKey(attributeName = "feedback_id")
    private Integer feedback_id;
    @DynamoDBAttribute(attributeName = "booking_id")
    private Integer booking_id;
    @DynamoDBAttribute(attributeName = "feedback")
    private String feedback;
}