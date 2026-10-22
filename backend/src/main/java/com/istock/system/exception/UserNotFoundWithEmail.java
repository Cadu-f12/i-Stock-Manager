package com.istock.system.exception;

import com.istock.system.entities.User;

public class UserNotFoundWithEmail extends RuntimeException{

    public UserNotFoundWithEmail(String email) {
        super("User not found with email: " + email);
    }
}
