package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.model.FeedbackEntity;
import com.epam.edp.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoachAllFeedbackServiceImpl implements CoachAllFeedbackService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private JwtUtils jwtUtils;

    public List<Map<String, Object>> getCoachAllFeedbacks(String authorizationHeader, Integer pageNumber, Integer pageSize) {

        String token = authorizationHeader.substring(7);
        String coachEmail = jwtUtils.extractEmail(token);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":coachEmail", new AttributeValue().withS(coachEmail));
        scanExpression.setFilterExpression("coachEmail = :coachEmail");
        scanExpression.setExpressionAttributeValues(eav);

        List<CoachBookEntity> coachBookEntities = dynamoDBMapper.scan(CoachBookEntity.class, scanExpression);

        List<Integer> workoutIds = coachBookEntities.stream()
                .filter(coachBookEntity -> coachBookEntity.getCoachEmail().equals(coachEmail))
                .map(CoachBookEntity::getId)
                .toList();

        if (workoutIds.isEmpty()) {
            throw new WorkoutNotFoundException("No workout found for coach email: " + coachEmail);
        }

        List<String> clientEmails = coachBookEntities.stream()
                .filter(coachBookEntity -> coachBookEntity.getCoachEmail().equals(coachEmail))
                .map(coachBookEntity -> {
                    String clientEmail = coachBookEntity.getClientEmail();
                    int indexOfAt = clientEmail.indexOf('@');
                    if (indexOfAt > 0) {
                        clientEmail = clientEmail.substring(0, indexOfAt);
                    }
                    return clientEmail.substring(0, 1).toUpperCase() + clientEmail.substring(1);
                })
                .toList();

        List<Map<String, Object>> feedbackResponseList = new ArrayList<>();

        for (int i = 0; i < workoutIds.size(); i++) {
            Integer workoutId = workoutIds.get(i);
            String clientEmail = clientEmails.get(i);

            Map<String, AttributeValue> eav1 = Map.of(":feedback_id", new AttributeValue().withN(String.valueOf(workoutId)));
            DynamoDBScanExpression scanExpression1 = new DynamoDBScanExpression()
                    .withFilterExpression("feedback_id = :feedback_id")
                    .withExpressionAttributeValues(eav1);

            dynamoDBMapper.scan(FeedbackEntity.class, scanExpression1).forEach(feedbackEntity -> {
                Map<String, Object> feedbackMap = Map.of(
                        "booking_id", feedbackEntity.getBooking_id(),
                        "feedback", feedbackEntity.getNotes(),
                        "rating", feedbackEntity.getRating(),
                        "createdAt", feedbackEntity.getCreatedAt(),
                        "clientName", clientEmail
                );
                feedbackResponseList.add(feedbackMap);
            });
        }

        int start = Math.min(pageNumber * pageSize, feedbackResponseList.size());
        int end = Math.min((pageNumber + 1) * pageSize, feedbackResponseList.size());
        List<Map<String, Object>> paginatedFeedback = feedbackResponseList.subList(start, end);

        return paginatedFeedback;
    }

}
