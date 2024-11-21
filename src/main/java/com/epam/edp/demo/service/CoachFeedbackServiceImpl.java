package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.epam.edp.demo.dto.request.CoachFeedbackDto;
import com.epam.edp.demo.dto.response.CoachFeedbackResponseDto;
import com.epam.edp.demo.exception.subexceptions.ValueNotValidException;
import com.epam.edp.demo.model.CoachFeedbackEntity;
import com.epam.edp.demo.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoachFeedbackServiceImpl implements CoachFeedbackService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private DynamoDB dynamoDB;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CoachFeedbackResponseDto saveCoachFeedback(CoachFeedbackDto feedback, String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        if (token == null || token.isEmpty()) {
            throw new ValueNotValidException("Token cannot be null or empty");
        }

        if (feedback == null) {
            throw new ValueNotValidException("Feedback request cannot be null");
        }

        if (feedback.getBooking_id() == null) {
            throw new ValueNotValidException("Booking ID cannot be null");
        }

        if(feedback.getFeedback()==null || feedback.getFeedback().isEmpty()){
            throw new ValueNotValidException("Feedback cannot be null or empty");
        }

        Table booking_db = dynamoDB.getTable("tm2-coach-book-db");
        Item item = booking_db.getItem("id", feedback.getBooking_id());
        if( item == null){
            throw new ValueNotValidException("Booking doesn't exist");
        }
        String coachEmail = item.getString("coachEmail");
        if (!coachEmail.equals(email) ) {
            throw new ValueNotValidException("Booking doesn't belong to the user "+ email);
        }
        Table feedback_db = dynamoDB.getTable("tm2-coach-feedback-db");

        Item existingFeedbackItem = feedback_db.getItem("feedback_id", feedback.getBooking_id());

        if (existingFeedbackItem != null) {

            throw new ValueNotValidException("Feedback already present for this booking.");

        }

        CoachFeedbackEntity coachFeedbackEntity = objectMapper.convertValue(feedback, CoachFeedbackEntity.class);
        coachFeedbackEntity.setFeedback_id(feedback.getBooking_id());
        coachFeedbackEntity.setBooking_id(feedback.getBooking_id());
        coachFeedbackEntity.setFeedback(feedback.getFeedback());
        dynamoDBMapper.save(coachFeedbackEntity);

        return objectMapper.convertValue(coachFeedbackEntity, CoachFeedbackResponseDto.class);
    }
}
