package com.kittynicky.app.exception;

import com.kittynicky.app.validator.Error;

import java.util.List;

public class ValidationException extends RuntimeException {

    public ValidationException(List<Error> errors) {
        super(Error.createMessage(errors));
    }
}
