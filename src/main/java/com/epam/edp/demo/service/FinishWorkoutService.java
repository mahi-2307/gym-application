package com.epam.edp.demo.service;

import com.epam.edp.demo.exception.subexceptions.WorkoutNotFoundException;

public interface FinishWorkoutService {

    void finishWorkout(int id, String authorizationHeader) throws WorkoutNotFoundException;

}

