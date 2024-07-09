package com.kittynicky.app.api.config;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class OpenWeatherApiConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = OpenWeatherApiConfig.class.getClassLoader().getResourceAsStream("OpenWeatherMapApi.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getKey() {
        return PROPERTIES.get("api.key").toString();
    }

    public static String getUrl() {
        return PROPERTIES.get("api.url").toString();
    }

    public static String getWeatherSuffix() {
        return PROPERTIES.get("api.weather.suffix").toString();
    }

    public static String getForecastSuffix() {
        return PROPERTIES.get("api.forecast.suffix").toString();
    }

    public static String getGeoSuffix() {
        return PROPERTIES.get("api.geo.suffix").toString();
    }

    public static String getIconUrl() {
        return PROPERTIES.get("api.icon.url").toString();
    }
}
