package com.kittynicky.app.service;

import com.kittynicky.app.dao.UserDao;
import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.exception.PasswordNotVerifiedException;
import com.kittynicky.app.exception.UserNotFoundException;
import com.kittynicky.app.exception.ValidationException;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.util.BCryptUtils;
import com.kittynicky.app.validator.SignUpValidator;
import com.kittynicky.app.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserDao userDao = UserDao.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final SignUpValidator signUpValidator = SignUpValidator.getInstance();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public Optional<UserDto> login(String login, String password) throws UserNotFoundException, PasswordNotVerifiedException {
        Optional<User> user = userDao.findByLogin(login);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with login '" + login + "' not found.");
        }
        if (!BCryptUtils.isVerified(password, user.get().getPassword())) {
            throw new PasswordNotVerifiedException("Password isn't verified. Please, try again.");
        }
        return user.map(userMapper::mapFrom);
    }

    public Integer create(String login, String password) {
        User user = User.builder()
                .login(login)
                .password(password)
                .build();

        ValidationResult validationResult = signUpValidator.isValid(user);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        user.setPassword(BCryptUtils.getHash(user.getPassword()));
        return userDao.save(user);
    }

    public Optional<User> getById(Integer id) {
        return userDao.findById(id);
    }

    public Optional<UserDto> getByLogin(String login) {
        Optional<User> user = userDao.findByLogin(login);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with login '" + login + "' not found.");
        }
        return user.map(userMapper::mapFrom);
    }
}
