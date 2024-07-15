package com.kittynicky.app.api.entity.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZoneOffset;
import java.util.Date;

@Value
@Builder
public class WeatherDto {
    String city;
    String country;
    Date date;
    BigDecimal temperature;
    BigDecimal feelsLike;
    BigDecimal temperatureMin;
    BigDecimal temperatureMax;
    String description;
    BigDecimal longitude;
    BigDecimal latitude;
    Date sunrise;
    Date sunset;
    URI icon;
    ZoneOffset zoneOffset;
}
