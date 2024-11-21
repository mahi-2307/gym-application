package com.epam.edp.demo.service;

import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;

public interface CancelCoachWorkoutService {

    void cancelCoachWorkout(int id, String authorizationHeader) throws WorkoutNotFoundException;

}
