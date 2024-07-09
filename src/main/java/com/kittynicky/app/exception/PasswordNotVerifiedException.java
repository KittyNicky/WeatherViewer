package com.kittynicky.app.exception;

public class PasswordNotVerifiedException extends RuntimeException {
    public PasswordNotVerifiedException(String message) {
        super(message);
    }
}
