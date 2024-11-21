package com.epam.edp.demo.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FinishWorkoutServiceImpl implements FinishWorkoutService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private JwtUtils jwtUtils;


    public void finishWorkout(int id, String authorizationHeader) throws WorkoutNotFoundException {

        String token = authorizationHeader.substring(7);
        String clientEmail = jwtUtils.extractEmail(token);

        if (clientEmail == null) {
            throw new WorkoutNotFoundException("Invalid token: Unable to extract client email");
        }

        CoachBookEntity coachBookEntity = dynamoDBMapper.load(CoachBookEntity.class, id);

        if (coachBookEntity == null) {
            throw new WorkoutNotFoundException("Session not found for ID: " + id);
        }

        if (!clientEmail.equals(coachBookEntity.getClientEmail())) {
            throw new WorkoutNotFoundException("Unauthorized: Session does not belong to the client");
        }

        if (coachBookEntity != null) {
            coachBookEntity.setStatus("Waiting for feedback");
            dynamoDBMapper.save(coachBookEntity);
        }
    }

}

