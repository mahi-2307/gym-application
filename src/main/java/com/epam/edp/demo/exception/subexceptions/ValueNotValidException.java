package com.epam.edp.demo.exception.subexceptions;

public class ValueNotValidException extends RuntimeException {
    public ValueNotValidException(String message){
        super(message);
    }
}
