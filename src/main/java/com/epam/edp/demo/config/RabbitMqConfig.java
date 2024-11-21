package com.epam.edp.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Common exchange
    public static final String EXCHANGE = "app-exchange";

    // Booking queue, routing key
    public static final String BOOKING_QUEUE = "booking-queue";
    public static final String BOOKING_ROUTING_KEY = "booking";

    // Feedback queue, routing key
    public static final String FEEDBACK_QUEUE = "feedback-queue";
    public static final String FEEDBACK_ROUTING_KEY = "feedback";

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE, true);
    }

    @Bean
    public Queue feedbackQueue() {
        return new Queue(FEEDBACK_QUEUE, true);
    }

    @Bean
    public Binding bookingBinding(Queue bookingQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(bookingQueue)
                .to(appExchange)
                .with(BOOKING_ROUTING_KEY);
    }

    @Bean
    public Binding feedbackBinding(Queue feedbackQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(feedbackQueue)
                .to(appExchange)
                .with(FEEDBACK_ROUTING_KEY);
    }
}
