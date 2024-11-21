package com.epam.edp.demo.service;

import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;

public interface CancelSessionService {

    void cancelSession(int id, String authorizationHeader) throws WorkoutNotFoundException;

}
