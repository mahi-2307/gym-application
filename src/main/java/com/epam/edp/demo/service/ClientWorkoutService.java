package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;

import java.util.List;

public interface ClientWorkoutService {

    List<CoachBookDto> getClientWorkouts(String authorizationHeader) throws UserNotFoundException;

}
