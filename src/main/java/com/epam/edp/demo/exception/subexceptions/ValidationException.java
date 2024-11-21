package com.epam.edp.demo.exception.subexceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message){
        super(message);
    }
}
