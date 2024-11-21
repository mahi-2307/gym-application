package com.epam.edp.demo.exception.subexceptions;

public class WorkoutNotFoundException extends RuntimeException{
    public WorkoutNotFoundException(String message) {
        super(message);
    }
}
