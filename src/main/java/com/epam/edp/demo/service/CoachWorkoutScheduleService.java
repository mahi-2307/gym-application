package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;

import java.util.List;

public interface CoachWorkoutScheduleService {

    List<CoachBookDto> getCoachWorkoutSchedule(String authorizationHeader) throws UserNotFoundException;

}
