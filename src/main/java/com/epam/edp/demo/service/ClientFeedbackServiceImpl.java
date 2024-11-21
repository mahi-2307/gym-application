package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.epam.edp.demo.config.RabbitMQProducer;
import com.epam.edp.demo.config.SqsConfigMethods;
import com.epam.edp.demo.dto.request.FeedbackRequestDto;
import com.epam.edp.demo.dto.response.FeedbackResponseDto;
import com.epam.edp.demo.exception.subexceptions.ValueNotValidException;
import com.epam.edp.demo.model.FeedbackEntity;
import com.epam.edp.demo.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ClientFeedbackServiceImpl implements ClientFeedbackService {
    private final RabbitMQProducer rabbitMQProducer;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    DynamoDBMapper dynamoDBMapper;
    @Autowired
    DynamoDB dynamoDB;
    @Autowired
    JwtUtils jwtUtils;
    private final SqsConfigMethods sqsConfig;
    UpdateItemSpec updateItemSpec;

    public FeedbackResponseDto saveClientFeedback(FeedbackRequestDto feedback, String token) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new ValueNotValidException("Token cannot be null or empty");
        }

        if (feedback == null) {
            throw new ValueNotValidException("Feedback request cannot be null");
        }

        if (feedback.getBooking_id() == null) {
            throw new ValueNotValidException("Booking ID cannot be null");
        }

        if (feedback.getRating() == null) {
            throw new ValueNotValidException("Rating cannot be null");
        }

        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new ValueNotValidException("Rating should be between 1 and 5");
        }

        if (feedback.getNotes() == null || feedback.getNotes().isEmpty()) {
            throw new ValueNotValidException("Notes cannot be null or empty");
        }

        String email = jwtUtils.extractEmail(token);
        System.out.println(email);



        Table booking_db = dynamoDB.getTable("tm2-coach-book-db");
        Item item = booking_db.getItem("id", feedback.getBooking_id());
        if( item == null){
            throw new ValueNotValidException("Booking doesn't exist");
        }
        String clientEmail = item.getString("clientEmail");
        if (!clientEmail.equals(email) ) {
            throw new ValueNotValidException("Booking doesn't belong to the user "+ email);
        }

        updateToFinishedWorkout(feedback.getBooking_id());
        Instant createdAt = Instant.now();
        FeedbackEntity feedbackEntity = objectMapper.convertValue(feedback, FeedbackEntity.class);
        feedbackEntity.setFeedback_id(feedbackEntity.getBooking_id());
        feedbackEntity.setRating(feedbackEntity.getRating());
        feedbackEntity.setNotes(feedbackEntity.getNotes());
        feedbackEntity.setCreatedAt(createdAt.toString());
        String feedbackToSqs = objectMapper.writeValueAsString(feedbackEntity);
        rabbitMQProducer.sendFeedbackMessage(feedbackToSqs);
        dynamoDBMapper.save(feedbackEntity);
        return objectMapper.convertValue(feedbackEntity, FeedbackResponseDto.class);
    }

    public void updateToFinishedWorkout(Integer id) {
        Table booking_db = dynamoDB.getTable("tm2-coach-book-db");
        String updateExpression = "set #s = :status";

        updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", id)
                .withUpdateExpression(updateExpression)
                .withNameMap(new NameMap().with("#s", "status"))
                .withValueMap(new ValueMap().withString(":status", "finished"))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        booking_db.updateItem(updateItemSpec);
    }
}
