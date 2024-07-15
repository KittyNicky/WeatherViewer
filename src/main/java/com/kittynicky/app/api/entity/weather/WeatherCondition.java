package com.kittynicky.app.api.entity.weather;

import com.kittynicky.app.api.service.OpenWeatherApiService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WeatherCondition {
    THUNDERSTORM("11d"),
    DRIZZLE("09d"),
    RAIN("10d"),
    SNOW("13d"),
    ATMOSPHERE("50d"),
    CLEAR("01d"),
    CLOUDS("03d"),
    UNDEFINED("02d");

    private String defaultIcon;

    private static final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    public static WeatherCondition getWeatherCondition(int code) {
        String codeStr = String.valueOf(code);

        if (codeStr.startsWith("2")) {
            return THUNDERSTORM;
        }
        if (codeStr.startsWith("3")) {
            return DRIZZLE;
        }
        if (codeStr.startsWith("5")) {
            return RAIN;
        }
        if (codeStr.startsWith("6")) {
            return SNOW;
        }
        if (codeStr.startsWith("7")) {
            return ATMOSPHERE;
        }
        if (codeStr.equals("800")) {
            return CLEAR;
        }
        if (codeStr.startsWith("8")) {
            return CLOUDS;
        }
        return UNDEFINED;
    }
}
