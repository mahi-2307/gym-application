package com.epam.edp.demo.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.epam.edp.demo.exception.subexceptions.ValueNotValidException;
import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CancelSessionServiceImpl implements CancelSessionService {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private DynamoDB dynamoDB;

    @Override
    public void cancelSession(int id, String authorizationHeader) throws WorkoutNotFoundException {

        String token = authorizationHeader.substring(7);  // Strip 'Bearer ' from the token
        String clientEmail = jwtUtils.extractEmail(token);

        if (clientEmail == null) {
            throw new WorkoutNotFoundException("Invalid token: Unable to extract client email");
        }
        Table booking_db = dynamoDB.getTable("tm2-coach-book-db");
        Item item = booking_db.getItem("id", id);
        if (item == null) {
            throw new ValueNotValidException("Booking doesn't exist");
        }
        String clientEmailfromDb = item.getString("clientEmail");
        if (!clientEmailfromDb.equals(clientEmail)) {
            throw new ValueNotValidException("Booking doesn't belong to the user " + clientEmail);
        }
        CoachBookEntity coachBookEntity = dynamoDBMapper.load(CoachBookEntity.class, id);

        if (coachBookEntity == null) {
            throw new WorkoutNotFoundException("Session not found for ID: " + id);
        }

        if (!clientEmail.equals(coachBookEntity.getClientEmail())) {
            throw new WorkoutNotFoundException("Unauthorized: Session does not belong to the client");
        }

        if (coachBookEntity != null) {
            coachBookEntity.setStatus("Cancelled");
            dynamoDBMapper.save(coachBookEntity);
        }

    }


}
