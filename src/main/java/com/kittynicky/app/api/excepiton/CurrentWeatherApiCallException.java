package com.kittynicky.app.api.excepiton;

public class CurrentWeatherApiCallException extends RuntimeException{
    public CurrentWeatherApiCallException(String message) {
        super(message);
    }
}
