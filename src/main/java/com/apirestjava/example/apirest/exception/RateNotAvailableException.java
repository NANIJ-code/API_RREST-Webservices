package com.apirestjava.example.apirest.exception;

public class RateNotAvailableException extends RuntimeException {
    public RateNotAvailableException(String message) {
        super(message);
    }
}