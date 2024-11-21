package com.epam.edp.demo.service;

import java.util.List;
import java.util.Map;

public interface CoachAllFeedbackService {

    public List<Map<String, Object>> getCoachAllFeedbacks(String authorizationHeader, Integer pageNumber, Integer pageSize);

}
