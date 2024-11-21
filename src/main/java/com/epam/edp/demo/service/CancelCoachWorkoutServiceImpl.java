package com.epam.edp.demo.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.epam.edp.demo.exception.subexceptions.ValueNotValidException;
import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CancelCoachWorkoutServiceImpl implements CancelCoachWorkoutService {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private DynamoDB dynamoDB;

    @Override
    public void cancelCoachWorkout(int id, String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String coachEmail = jwtUtils.extractEmail(token);

        if(coachEmail == null){
            throw new WorkoutNotFoundException("Invalid token: Unable to extract coach email");
        }

        Table booking_db = dynamoDB.getTable("tm2-coach-book-db");
        Item item = booking_db.getItem("id", id);

        if(item == null){
            throw new WorkoutNotFoundException("Workout not found for ID: " + id);
        }

        String coachEmailfromDb = item.getString("coachEmail");
        if (!coachEmailfromDb.equals(coachEmail)) {
            throw new ValueNotValidException("Booking doesn't belong to the user " + coachEmail);
        }

        String status = item.getString("status");
        if(status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Waiting for feedback") || status.equalsIgnoreCase("Completed")){
            throw new ValueNotValidException("Booking workout is already " + status);
        }

        CoachBookEntity coachBookEntity = dynamoDBMapper.load(CoachBookEntity.class, id);
        if(coachBookEntity == null){
            throw new WorkoutNotFoundException("Workout not found for ID: " + id);
        }


        if (coachBookEntity != null && status.equalsIgnoreCase("Scheduled")) {
            coachBookEntity.setStatus("Cancelled");
            dynamoDBMapper.save(coachBookEntity);
        }


    }

}

