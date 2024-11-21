package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.epam.edp.demo.dto.request.CoachEditProfilePicDto;
import com.epam.edp.demo.dto.response.CoachResponseEditProfilePicDto;
import com.epam.edp.demo.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadProfilePictureServiceImpl implements UploadProfilePictureService {
    private final AmazonDynamoDB amazonDynamoDB;
    private final DynamoDB dynamoDB;
    private final JwtUtils jwtUtils;
    private final AmazonS3 amazonS3;
    private final String bucketName = "tm2-coach-update-profile-picture";

    private String uploadProfilePictureToS3(MultipartFile file) throws IOException {
        String fileName = "profile-pictures/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private String uploadPdfToS3(MultipartFile file) throws IOException {
        String fileName = "pdf-documents/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private UpdateItemSpec createCoachUpdateItemSpec(String email, CoachEditProfilePicDto requestData, String profilePictureUrl,String pdfUrl) {
//        return new UpdateItemSpec()
//                .withPrimaryKey("email", email)
//                .withUpdateExpression("set #nm = :n, title = :t, about = :a, specialization = :s, profilePictureUrl = :p, pdfUrl = :pdf")
//                .withNameMap(new NameMap().with("#nm", "name"))
//                .withValueMap(new ValueMap()
//                        .withString(":n", requestData.getName())
//                        .withString(":t", requestData.getTitle())
//                        .withString(":a", requestData.getAbout())
//                        .withList(":s", requestData.getSpecialization())
//                        .withString(":p", profilePictureUrl)
//                        .withString(":pdf", pdfUrl))
//                .withReturnValues(ReturnValue.UPDATED_NEW);
        StringBuilder updateExpression = new StringBuilder("set ");
        ValueMap valueMap = new ValueMap();
        NameMap nameMap = new NameMap();

        if (requestData.getName() != null) {
            updateExpression.append("#nm = :n, ");
            nameMap.with("#nm", "name");
            valueMap.withString(":n", requestData.getName());
        }
        if (requestData.getTitle() != null) {
            updateExpression.append("title = :t, ");
            valueMap.withString(":t", requestData.getTitle());
        }
        if (requestData.getAbout() != null) {
            updateExpression.append("about = :a, ");
            valueMap.withString(":a", requestData.getAbout());
        }
        if (requestData.getSpecialization() != null && !requestData.getSpecialization().isEmpty()) {
            updateExpression.append("specialization = :s, ");
            valueMap.withList(":s", requestData.getSpecialization());
        }
        if (profilePictureUrl != null) {
            updateExpression.append("profilePictureUrl = :p, ");
            valueMap.withString(":p", profilePictureUrl);
        }
        if (pdfUrl != null) {
            updateExpression.append("pdfUrl = :pdf, ");
            valueMap.withString(":pdf", pdfUrl);
        }

        // Remove the last comma and space
        if (updateExpression.length() > 4) {
            updateExpression.setLength(updateExpression.length() - 2);
        }

        return new UpdateItemSpec()
                .withPrimaryKey("email", email)
                .withUpdateExpression(updateExpression.toString())
                .withNameMap(nameMap)
                .withValueMap(valueMap)
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }

    private ResponseEntity<?> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(status).body(error);
    }

    @Override
    public ResponseEntity<?> updateProfile(String authorizationHeader, CoachEditProfilePicDto requestData) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        String role= jwtUtils.extractRole(token);
        Table table = dynamoDB.getTable("tm2-test-coach-update-info");


        if (!"coach".equals(role)) {
            return createErrorResponse(HttpStatus.FORBIDDEN, "Access denied: Only coaches are allowed to update profiles");
        }
        if (email == null || email.isEmpty()) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
        }

        try {
//            String profilePictureUrl = uploadProfilePictureToS3(requestData.getProfilePicture());
//            String pdfUrl=uploadPdfToS3(requestData.getCertificate());
            String profilePictureUrl = null;
            if (requestData.getProfilePicture() != null && !requestData.getProfilePicture().isEmpty()) {
                profilePictureUrl = uploadProfilePictureToS3(requestData.getProfilePicture());
            }

            String pdfUrl = null;
            if (requestData.getCertificate() != null && !requestData.getCertificate().isEmpty()) {
                pdfUrl = uploadPdfToS3(requestData.getCertificate());
            }
            UpdateItemSpec updateItemSpec = createCoachUpdateItemSpec(email, requestData, profilePictureUrl,pdfUrl);
            table.updateItem(updateItemSpec);           // Perform the update operation

            CoachResponseEditProfilePicDto response = new CoachResponseEditProfilePicDto(
                    requestData.getName(),
                    requestData.getTitle(),
                    requestData.getAbout(),
                    requestData.getSpecialization(),
                    profilePictureUrl,
                    pdfUrl
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof ConditionalCheckFailedException) {
                System.out.println("Conditional check failed: " + e.getMessage());
            } else {
                System.out.println("Update failed: " + e.getMessage());
            }
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update coach profile: " + e.getMessage());
        }
    }

}
