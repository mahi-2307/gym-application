package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.epam.edp.demo.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateStorageServiceImpl implements CertificateStorageService {

    private final AmazonDynamoDB dynamoDB;

    private final JwtUtils jwtUtils;

    private final AmazonS3 s3Client;
    private String bucketName = "tm2-s3-coach-certificate";
    private String tableName = "tm2-coach-upload-info";
    private final Logger logger = LoggerFactory.getLogger(CertificateStorageServiceImpl.class);

    @Override
    public String uploadPdfToS3AndDynamoDB(MultipartFile file, String token) throws IOException {
        String email = jwtUtils.extractEmail(token);
        logger.info("User Email: {}", email);
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        // Generate a unique key for the file in S3
        String fileKey = UUID.randomUUID().toString() + ".pdf";

        // Upload file to S3
        s3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), objectMetadata));

        // Generate the full S3 URL
        String s3Url = s3Client.getUrl(bucketName, fileKey).toString();

        // Retrieve and increment the counter
        long id = getNextIdFromDynamoDB();

        // Prepare the item values for DynamoDB
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", new AttributeValue().withN(Long.toString(id)));
        itemValues.put("pdfUrl", new AttributeValue(s3Url)); // Store only the key

        // Store the item in DynamoDB
        dynamoDB.putItem(tableName, itemValues);

        // Return the URL of the uploaded file for confirmation, not storing it
        return s3Url;
    }

    // Method to retrieve and increment the counter in DynamoDB
    private long getNextIdFromDynamoDB() {
        HashMap<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue("pdfUploadCounter"));  // Ensure this key matches your table schema

        // Use if_not_exists to set countNumber to 0 if it does not exist
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("tm2-counters")
                .withKey(key)
                .withUpdateExpression("SET countNumber = if_not_exists(countNumber, :zero) + :incr")
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":incr", new AttributeValue().withN("1"));
                    put(":zero", new AttributeValue().withN("0"));
                }})
                .withReturnValues(ReturnValue.UPDATED_NEW);

        // Execute the update and get the updated counter value
        UpdateItemResult result = dynamoDB.updateItem(updateItemRequest);
        return Long.parseLong(result.getAttributes().get("countNumber").getN());
    }
    @Override
    public String getFileKeyById(long id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withN(String.valueOf(id)));

        GetItemRequest request = new GetItemRequest()
                .withTableName(tableName)
                .withKey(key);

        Map<String, AttributeValue> item = dynamoDB.getItem(request).getItem();
        if (item != null && item.containsKey("pdfUrl") && item.get("pdfUrl").getS() != null && !item.get("pdfUrl").getS().isEmpty()) {
            return item.get("pdfUrl").getS();
        } else {
            logger.error("No PDF URL found for ID: {}", id);
            throw new IllegalArgumentException("No PDF found for the given ID.");
        }
    }

}


