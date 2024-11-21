package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.response.CoachResponseDto;

import java.util.List;

public interface CoachService {
    List<CoachResponseDto> getCoaches(String token);
    CoachResponseDto getCoachById(String token, Long id);
}
