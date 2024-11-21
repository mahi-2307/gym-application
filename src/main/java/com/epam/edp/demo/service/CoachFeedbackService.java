package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.CoachFeedbackDto;
import com.epam.edp.demo.dto.response.CoachFeedbackResponseDto;

public interface CoachFeedbackService {

    CoachFeedbackResponseDto saveCoachFeedback(CoachFeedbackDto feedback, String token);

}
