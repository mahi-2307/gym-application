package com.epam.edp.demo.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientWorkoutServiceImpl implements ClientWorkoutService {

    @Autowired
    private DynamoDB dynamoDB;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private JwtUtils jwtUtils;
    public List<CoachBookDto> getClientWorkouts(String authorizationHeader) throws UserNotFoundException {
        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":clientEmail", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(email));

        scanExpression.setFilterExpression("clientEmail = :clientEmail");
        scanExpression.setExpressionAttributeValues(eav);

        List<CoachBookEntity> coachBookEntities = dynamoDBMapper.scan(CoachBookEntity.class, scanExpression);

        return coachBookEntities.stream()
                .filter(coachBookEntity -> coachBookEntity.getClientEmail().equals(email))
                .map(coachBookEntity -> {
                    CoachBookDto coachBookDto = new CoachBookDto();
                    coachBookDto.setId(coachBookEntity.getId());
                    coachBookDto.setClientEmail(coachBookEntity.getClientEmail());
                    coachBookDto.setCoachEmail(coachBookEntity.getCoachEmail());
                    coachBookDto.setWorkoutType(coachBookEntity.getWorkoutType());
                    coachBookDto.setDate(coachBookEntity.getDate());
                    coachBookDto.setDuration(coachBookEntity.getDuration());
                    coachBookDto.setStatus(coachBookEntity.getStatus());
                    return coachBookDto;
                })
                .collect(Collectors.toList());

    }

}
