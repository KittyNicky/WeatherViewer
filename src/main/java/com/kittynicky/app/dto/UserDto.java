package com.kittynicky.app.dto;

import lombok.*;

@Value
@Builder
public class UserDto {
    Integer id;
    String login;
}
