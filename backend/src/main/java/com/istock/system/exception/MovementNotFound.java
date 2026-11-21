package com.istock.system.exception;

public class MovementNotFound extends RuntimeException {
    public MovementNotFound(String message) {
        super(message);
    }
}
