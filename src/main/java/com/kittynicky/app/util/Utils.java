package com.kittynicky.app.util;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Optional;

public class Utils {
    public static Optional<Cookie> getCookie(Cookie[] cookies, String value) {
        if (cookies == null || cookies.length == 0) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(value))
                .findFirst();
    }
}