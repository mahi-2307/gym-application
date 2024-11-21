package com.epam.edp.demo.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import com.epam.edp.demo.config.DynamoDbConfig;
import com.epam.edp.demo.config.RabbitMQProducer;
import com.epam.edp.demo.config.SqsConfigMethods;
import com.epam.edp.demo.dto.request.BookingEventDto;
import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.exception.subexceptions.CoachNotFoundException;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;
import com.epam.edp.demo.model.CoachBookEntity;
import com.epam.edp.demo.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoachBookServiceImpl implements CoachBookService {

    @Autowired
    private DynamoDB dynamoDB;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RabbitMQProducer rabbitMQProducer;
    private static final String COACH_DB_TABLE = "tm2-coaches-database";
    private static final String reportsDb = "reports_db";
    private final DynamoDbConfig dynamoDbConfig;

    public CoachBookDto bookCoach(CoachBookDto coachBookDto, String authorizationHeader) throws UserNotFoundException, JsonProcessingException {

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        int id = generateAutoIncrementedId();
        String status = "Scheduled";
        coachBookDto.setClientEmail(email);
        coachBookDto.setId(id);
        coachBookDto.setStatus(status);

        String coachEmail = coachBookDto.getCoachEmail();
        if (!isCoachEmailExists(coachEmail)) {
            throw new CoachNotFoundException("Coach with email " + coachEmail + " not found.");
        }
        String workoutType = fetchWorkoutTypeFromDb(coachEmail);
        coachBookDto.setWorkoutType(workoutType);

        DateTime time = DateTime.parse(coachBookDto.getDate(), ISODateTimeFormat.dateTimeParser());
        String isoFormattedDate = time.toString(ISODateTimeFormat.dateTime().withZoneUTC());

        CoachBookEntity coachBookEntity = new CoachBookEntity();
        coachBookEntity.setId(id);
        coachBookEntity.setClientEmail(email);
        coachBookEntity.setCoachEmail(coachEmail);
        coachBookEntity.setWorkoutType(workoutType);
        coachBookEntity.setDate(isoFormattedDate);
        coachBookEntity.setDuration(coachBookDto.getDuration());
        coachBookEntity.setStatus(coachBookDto.getStatus());

        String message = objectMapper.writeValueAsString(coachBookEntity);
        rabbitMQProducer.sendBookingMessage(message);
        dynamoDBMapper.save(coachBookEntity);

        return coachBookDto;
    }

    private boolean isCoachEmailExists(String coachEmail) {
        if (coachEmail == null || coachEmail.isEmpty()) {
            throw new IllegalArgumentException("Coach email cannot be null or empty");
        }

        Table coachDbTable = dynamoDB.getTable(COACH_DB_TABLE);

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("email = :email")
                .withValueMap(new ValueMap().withString(":email", coachEmail));

        ItemCollection<ScanOutcome> items = coachDbTable.scan(scanSpec);

        Iterator<Item> iterator = items.iterator();

        if (iterator.hasNext()) {
            log.info("Coach email {} found in database", coachEmail);
            return true;
        } else {
            log.warn("Coach email {} not found in database", coachEmail);
            return false;
        }
    }

    private String fetchWorkoutTypeFromDb(String coachEmail) {
        Table coachDbTable = dynamoDB.getTable(COACH_DB_TABLE);
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", coachEmail);
        Item outcome = coachDbTable.getItem(spec);
        if (outcome != null) {
            String workoutType = outcome.getString("shortSummary");
            log.info("Fetched workout type: {}", workoutType);
            return workoutType;
        } else {
            log.warn("No workout type found for coach email: {}", coachEmail);
            return null;
        }
    }

    private int generateAutoIncrementedId() {
        Table coachBookDb = dynamoDB.getTable("tm2-coach-book-db");
        ScanSpec scanSpec = new ScanSpec().withProjectionExpression("id");

        Iterator<Item> iterator = coachBookDb.scan(scanSpec).iterator();
        int maxId = 0;
        boolean itemsFound = false;

        while (iterator.hasNext()) {
            itemsFound = true;
            Item item = iterator.next();
            int id = item.getInt("id");
            if (id > maxId) {
                maxId = id;
            }
        }

        if (!itemsFound) {
            log.info("No items found in the table. Starting ID from 1.");
        } else {
            log.info("Max ID found: " + maxId);
        }

        return maxId + 1;
    }


}
