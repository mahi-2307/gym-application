package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.exception.subexceptions.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CoachBookService {

    CoachBookDto bookCoach(CoachBookDto coachBookDto, String authorizationHeader) throws UserNotFoundException, JsonProcessingException;

}