package com.epam.edp.demo.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    private String lastBookingMessage;
    private String lastFeedbackMessage;


    public void receiveBookingMessage(String message) {
        System.out.println("Received booking message: " + message);
        lastBookingMessage = message;
    }


    public void receiveFeedbackMessage(String message) {
        System.out.println("Received feedback message: " + message);
        lastFeedbackMessage = message;
    }

    public String getLastBookingMessage() {
        return lastBookingMessage;
    }

    public String getLastFeedbackMessage() {
        return lastFeedbackMessage;
    }
}
