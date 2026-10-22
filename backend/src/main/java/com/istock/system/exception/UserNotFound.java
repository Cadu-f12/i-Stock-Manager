package com.istock.system.exception;

public class UserNotFound extends RuntimeException {

    public UserNotFound(Long id) {
        super("User not found with id: " + id);
    }
}