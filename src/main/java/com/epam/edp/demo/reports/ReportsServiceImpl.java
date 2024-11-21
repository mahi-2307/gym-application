package com.epam.edp.demo.reports;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.epam.edp.demo.config.RabbitMQConsumer;
import com.epam.edp.demo.config.SqsConfigMethods;
import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.dto.response.FeedbackResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.io.*;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {
    private final RabbitMQConsumer rabbitMQConsumer;
    private final SqsConfigMethods sqsConfig;
    private final ObjectMapper objectMapper;
    @Autowired
    private AmazonDynamoDB client;
    @Autowired
    private DynamoDB dynamoDB;
    @Autowired
    private AmazonS3 amazonS3;

    @RabbitListener(queues = "booking-queue")
    public void processBookingMessages(String message) throws JsonProcessingException {

        Table table = dynamoDB.getTable("reports_db");
        Table bookingsdb = dynamoDB.getTable("tm2-coach-book-db");

        // Deserialize the message body to CoachBookDto
        CoachBookDto coachBookDto = objectMapper.readValue(message, CoachBookDto.class);

        // Retrieve the item from DynamoDB for the coach using coachEmail
        Item item = table.getItem("email", coachBookDto.getCoachEmail());

        if (item == null) {
            // If no item exists for the coach, create a new entry
            item = new Item()
                    .withPrimaryKey("email", coachBookDto.getCoachEmail())
                    .withString("workoutType", coachBookDto.getWorkoutType())
                    .withNumber("averageDuration", coachBookDto.getDuration())
                    .withNumber("sessionCount", 1)
                    .withDouble("averageRating", 0)
                    .withNumber("feedbackCount", 0)
                    .withNumber("clientCount", 1);
        } else {
            // If the item exists, calculate the new average duration
            int sessionCount = item.getInt("sessionCount");
            double currentAverageDuration = item.getDouble("averageDuration");
            int clientCount = item.getInt("clientCount");
            sessionCount += 1;
            clientCount += 1;
            double newAverageDuration = (currentAverageDuration * (sessionCount - 1) + coachBookDto.getDuration()) / sessionCount;

            item.withNumber("averageDuration", newAverageDuration)
                    .withNumber("sessionCount", sessionCount);


            item.withNumber("clientCount", clientCount);
        }
        table.putItem(item);
    }

    @RabbitListener(queues = "feedback-queue")
    public void processFeedbackMessages(String message) throws JsonProcessingException {

        Table table = dynamoDB.getTable("reports_db");
        Table bookingsdb = dynamoDB.getTable("tm2-coach-book-db");
        FeedbackResponseDto feedbackDto = objectMapper.readValue(message, FeedbackResponseDto.class);
        System.out.println("Feedback message: " + feedbackDto);
        Item bookingsdbItem = bookingsdb.getItem("id", feedbackDto.getBooking_id());

        if (bookingsdbItem == null) {
            System.out.println("No booking found for booking ID: " + feedbackDto.getBooking_id());
            return;
        }

        String email = bookingsdbItem.getString("coachEmail");
        Item item = table.getItem("email", email);

        if (item == null) {
            System.out.println("No entry found for coach: " + email);
            return;
        }

        // Update the feedback stats
        int feedbackCount = item.getInt("feedbackCount");
        double currentAverageRating = item.getDouble("averageRating");

        feedbackCount += 1;
        double newAverageRating = (currentAverageRating * (feedbackCount - 1) + feedbackDto.getRating()) / feedbackCount;

        // Calculate delta rating (change from previous to current)
        double deltaRating = newAverageRating - currentAverageRating;
        StringBuilder stringBuilder = new StringBuilder();
        if (deltaRating > 0) {
            stringBuilder.append("+");
            stringBuilder.append(deltaRating);
        } else {
            stringBuilder.append("-");
            stringBuilder.append(deltaRating);
        }
        // Update the item with new average rating, feedback count, and delta rating
        item.withNumber("averageRating", newAverageRating)
                .withNumber("feedbackCount", feedbackCount)
                .withNumber("clientCount", item.getInt("clientCount"))
                .withString("deltaRating", stringBuilder.toString());

        // Save the updated item back to DynamoDB
        table.putItem(item);
    }



    @Scheduled(fixedRate = 49999)
    public void exportToExcel() throws IOException {
        Table reportsTable = dynamoDB.getTable("reports_db");
        ItemCollection<ScanOutcome> items = reportsTable.scan();

        // Create a workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Coach Reports");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Email");
        headerRow.createCell(1).setCellValue("Workout Type");
        headerRow.createCell(2).setCellValue("Average Duration");
        headerRow.createCell(3).setCellValue("Session Count");
        headerRow.createCell(4).setCellValue("Average Rating");
        headerRow.createCell(5).setCellValue("Feedback Count");
        headerRow.createCell(6).setCellValue("Client Count");
        headerRow.createCell(7).setCellValue("Delta Rating"); // New column for delta rating

        // Define styles for positive (green) and negative (red) delta ratings
        CellStyle positiveStyle = workbook.createCellStyle();
        positiveStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        positiveStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle negativeStyle = workbook.createCellStyle();
        negativeStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        negativeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Write each DynamoDB item to the Excel file
        int rowIndex = 1;
        for (Item item : items) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.getString("email"));
            row.createCell(1).setCellValue(item.getString("workoutType"));
            row.createCell(2).setCellValue(item.getDouble("averageDuration"));
            row.createCell(3).setCellValue(item.getInt("sessionCount"));
            row.createCell(4).setCellValue(item.getDouble("averageRating"));
            row.createCell(5).setCellValue(item.getInt("feedbackCount"));
            row.createCell(6).setCellValue(item.getInt("clientCount"));

            // Write the delta rating with conditional formatting
            Cell deltaRatingCell = row.createCell(7);
            if (item.hasAttribute("deltaRating")) {
                double deltaRating = Double.parseDouble(item.getString("deltaRating"));

                deltaRatingCell.setCellValue(deltaRating);

                // Apply style based on delta rating value
                if (deltaRating >= 0) {
                    deltaRatingCell.setCellStyle(positiveStyle);
                } else {
                    deltaRatingCell.setCellStyle(negativeStyle);
                }
            } else {
                deltaRatingCell.setCellValue(0); // Default delta rating to 0 if not present
            }
        }

        // Write the workbook data to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        // Convert the ByteArrayOutputStream to a byte array
        byte[] excelBytes = byteArrayOutputStream.toByteArray();

        // Upload the file to S3 using the byte array
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(excelBytes.length);
        metadata.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // Convert byte[] to InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);

        // Create a PutObjectRequest with the InputStream
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                "tm2-gym-application", // S3 bucket name
                "coach_reports.xlsx",   // S3 key (file name)
                inputStream,            // InputStream of the file
                metadata                // Metadata for the file
        );

        // Upload to S3
        PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);

        // Close streams
        inputStream.close();
        byteArrayOutputStream.close();

        System.out.println("Excel file uploaded to S3 successfully: " + putObjectResult);
    }



}
