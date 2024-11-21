package com.epam.edp.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "reports_db")
public class ReportsEntity {
    @DynamoDBHashKey(attributeName = "email")
    private String email;
    @DynamoDBAttribute(attributeName = "average_duration")
    private String averageDuration;
    @DynamoDBAttribute(attributeName = "average_rating")
    private String averageRating;
    @DynamoDBAttribute(attributeName = "total_feedbacks")
    private String totalFeedbacks;
    @DynamoDBAttribute(attributeName = "total_bookings")
    private String totalBookings;
    @DynamoDBAttribute(attributeName = "total_customers")
    private String totalCustomers;
}
