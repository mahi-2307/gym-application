package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.epam.edp.demo.dto.response.CoachResponseDto;
import com.epam.edp.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class CoachServiceImpl implements CoachService{
    @Autowired
    AmazonDynamoDB client;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    DynamoDB dynamoDB;
    @Override
    public List<CoachResponseDto> getCoaches(String token) {

        String email=jwtUtils.extractEmail(token);
        System.out.println("User Email:"+ email);

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("tm2-coaches-database");
        ScanResult result = client.scan(scanRequest);

        List<CoachResponseDto> coaches = new ArrayList<>();
        List<Map<String, AttributeValue>> items = result.getItems();

        for (Map<String, AttributeValue> item : items) {
            CoachResponseDto coach = new CoachResponseDto();
            coach.setEmail(item.get("email").getS());
            coach.setExpertiseDescription(item.get("expertiseDescription").getS());
            coach.setName(item.get("name").getS());
            coach.setRating(Double.parseDouble(item.get("rating").getN()));
            coach.setShortSummary(item.get("shortSummary").getS());
            coach.setProfilePicture(item.get("profilePicture").getS());
            coaches.add(coach);
        }

        return coaches;


    }

    @Override
    public CoachResponseDto getCoachById(String token, Long id) {
        Table coachDb = dynamoDB.getTable("tm2-coach-db");
        Item item = coachDb.getItem("id", id);
        CoachResponseDto coach = new CoachResponseDto();
        coach.setEmail(item.getString("email"));
        coach.setExpertiseDescription(item.getString("expertiseDescription"));
        coach.setName(item.getString("name"));
        coach.setRating(item.getNumber("rating").doubleValue());
        coach.setShortSummary(item.getString("shortSummary"));
        coach.setProfilePicture(item.getString("profilePicture"));
        return coach;
    }

}
