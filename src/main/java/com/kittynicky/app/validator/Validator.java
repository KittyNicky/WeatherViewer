package com.kittynicky.app.validator;

public interface Validator<T> {
    ValidationResult isValid(T object);
}
