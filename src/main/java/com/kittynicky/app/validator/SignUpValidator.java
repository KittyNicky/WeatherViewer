package com.kittynicky.app.validator;

import com.kittynicky.app.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpValidator implements Validator<User> {
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 20;
    private static final int LOGIN_MIN_LENGTH = 6;
    private static final int LOGIN_MAX_LENGTH = 20;

    public static final SignUpValidator INSTANCE = new SignUpValidator();

    public static SignUpValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult isValid(User object) {
        ValidationResult validationResult = new ValidationResult();
        validateLogin(validationResult, object.getLogin());
        validatePassword(validationResult, object.getPassword());
        return validationResult;
    }

    private void validateLogin(ValidationResult validationResult, String login) {
        if (login.isBlank()) {
            validationResult.add(Error.of("invalid.login", "Login is whitespace, empty or null"));
        }
        if (login.length() < LOGIN_MIN_LENGTH) {
            validationResult.add(Error.of("invalid.login", "login must be at least " + LOGIN_MIN_LENGTH + " characters"));
        }
        if (login.length() > LOGIN_MAX_LENGTH) {
            validationResult.add(Error.of("invalid.login", "login must be less than " + LOGIN_MAX_LENGTH + " characters"));
        }
    }

    private void validatePassword(ValidationResult validationResult, String password) {
        if (password.length() < PASSWORD_MIN_LENGTH) {
            validationResult.add(Error.of("invalid.password", "password must be at least " + PASSWORD_MIN_LENGTH + " characters"));
        }
        if (password.length() > PASSWORD_MAX_LENGTH) {
            validationResult.add(Error.of("invalid.password", "password must be less than " + PASSWORD_MAX_LENGTH + " characters"));
        }
        if (!digit(password)) {
            validationResult.add(Error.of("invalid.password", "password must contain digit"));
        }
        if (!upperCase(password)) {
            validationResult.add(Error.of("invalid.password", "password must contain upper case letter"));
        }
        if (!lowerCase(password)) {
            validationResult.add(Error.of("invalid.password", "password must contain lower case letter"));
        }
    }

    private boolean upperCase(String password) {
        return password.matches(".*[A-Z].*");
    }

    private boolean lowerCase(String password) {
        return password.matches(".*[a-z].*");
    }

    private boolean digit(String password) {
        return password.matches(".*\\d.*");
    }
}