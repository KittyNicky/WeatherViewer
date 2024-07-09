package com.kittynicky.app.validator;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class Error {
    String code;
    String message;

    public static String createMessage(List<Error> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        while (i < errors.size()) {
            stringBuilder.append(errors.get(i - 1).getMessage());
            stringBuilder.append(", ");
            i++;
        }
        stringBuilder.append(errors.get(i - 1).getMessage());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        return stringBuilder.toString();
    }
}