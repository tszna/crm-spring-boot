package com.crm.crm.exceptions;

public class UserIdAlreadyExistException extends RuntimeException {
    public UserIdAlreadyExistException(String userIdIsAlreadyTaken) {
        super(userIdIsAlreadyTaken);
    }
}