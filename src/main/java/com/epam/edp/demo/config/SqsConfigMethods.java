package com.epam.edp.demo.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Map;

@Component
public class SqsConfigMethods {
    @Value("${aws.sqsUrl}")
    private String sqsUrl;  // Now configurable from application.properties
    private final Logger log = LogManager.getLogger(SqsConfigMethods.class);
    @Autowired
    private SqsClient amazonSQS;

    // Send a message to the SQS queue
    public void sendMessage(String message, String entityType) {
        log.info("Sending message of type: {}", entityType);

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(sqsUrl)
                .messageBody(message)
                .messageAttributes(Map.of(
                        "type", MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue(entityType)
                                .build()
                ))
                .build();

        SendMessageResponse sendMessageResult = amazonSQS.sendMessage(sendMessageRequest);
        log.info("Message sent successfully: {} with message: {}", sendMessageResult, message);
    }

    // Receive messages from the SQS queue
    public List<Message> receiveMessage() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(sqsUrl)
                .maxNumberOfMessages(10)
                .messageAttributeNames("All")
                .build();

        ReceiveMessageResponse receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);
        List<Message> messages = receiveMessageResult.messages();

        // Process the received messages
        for (Message message : messages) {
            log.info("Received message: {}", message.body());

            // Delete the message after processing
            deleteMessage(message);
        }
        return messages;
    }

    // Delete the message from the SQS queue
    public void deleteMessage(Message message) {
        String receiptHandle = message.receiptHandle();

        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(sqsUrl)
                .receiptHandle(receiptHandle)
                .build();

        DeleteMessageResponse deleteMessageResponse = amazonSQS.deleteMessage(deleteMessageRequest);
        log.info("Message deleted successfully: {}", deleteMessageResponse);
    }
}
