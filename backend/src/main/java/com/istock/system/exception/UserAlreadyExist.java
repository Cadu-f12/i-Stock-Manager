package com.istock.system.exception;

import com.istock.system.entities.User;

public class UserAlreadyExist extends RuntimeException{
    public UserAlreadyExist(User user) {
        super("User with email already exist: " + user.getEmail());
    }
}
