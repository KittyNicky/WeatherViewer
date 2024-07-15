package com.kittynicky.app.api.excepiton;

public class ForecastWeatherApiCallException extends RuntimeException {
    public ForecastWeatherApiCallException(String message) {
        super(message);
    }
}
