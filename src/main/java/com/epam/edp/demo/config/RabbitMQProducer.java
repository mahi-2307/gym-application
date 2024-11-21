package com.epam.edp.demo.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {
    Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE = "app-exchange";

    public void sendBookingMessage(String message) {
        logger.info("Sending booking message to RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(EXCHANGE, "booking", message);
    }

    public void sendFeedbackMessage(String message) {
        logger.info("Sending feedback message to RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(EXCHANGE, "feedback", message);
    }
}
