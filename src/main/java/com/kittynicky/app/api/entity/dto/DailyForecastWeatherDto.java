package com.kittynicky.app.api.entity.dto;

import com.kittynicky.app.api.entity.weather.WeatherCondition;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZoneOffset;
import java.util.Date;

@Value
@Builder
public class DailyForecastWeatherDto {
    Date date;
    BigDecimal temperature;
    BigDecimal temperatureMin;
    BigDecimal temperatureMax;
    WeatherCondition weatherCondition;
    URI icon;
    ZoneOffset zoneOffset;
}