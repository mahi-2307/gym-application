package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.FeedbackRequestDto;
import com.epam.edp.demo.dto.response.FeedbackResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ClientFeedbackService {
    FeedbackResponseDto saveClientFeedback(FeedbackRequestDto feedback, String token) throws JsonProcessingException;
}
