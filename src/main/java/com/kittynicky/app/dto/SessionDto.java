package com.kittynicky.app.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class SessionDto {
    UUID id;
    UserDto user;
    LocalDateTime expiresAt;
}
