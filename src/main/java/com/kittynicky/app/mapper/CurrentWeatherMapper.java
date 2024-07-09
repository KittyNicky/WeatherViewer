package com.kittynicky.app.mapper;

import com.kittynicky.app.api.entity.response.CurrentWeatherResponse;
import com.kittynicky.app.api.service.OpenWeatherApiService;
import com.kittynicky.app.api.entity.dto.WeatherDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentWeatherMapper implements Mapper<CurrentWeatherResponse, WeatherDto> {
    private static final CurrentWeatherMapper INSTANCE = new CurrentWeatherMapper();
    private final TemperatureMapper temperatureMapper = TemperatureMapper.getInstance();
    private final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    public static CurrentWeatherMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public WeatherDto mapFrom(CurrentWeatherResponse object) {
        return WeatherDto.builder()
                .city(object.getCityName())
                .country(object.getSys().getCountry())
                .description(object.getWeather().get(0).getDescription())
                .feelsLike(temperatureMapper.mapFrom(object.getMain().getFeelsLike()))
                .temperature(temperatureMapper.mapFrom(object.getMain().getTemperature()))
                .temperatureMax(temperatureMapper.mapFrom(object.getMain().getTemperatureMax()))
                .temperatureMin(temperatureMapper.mapFrom(object.getMain().getTemperatureMin()))
                .latitude(object.getCoordinates().getLatitude())
                .longitude(object.getCoordinates().getLongitude())
                .sunrise(getDate(object.getSys().getSunrise(), object.getZoneOffset()))
                .sunset(getDate(object.getSys().getSunset(), object.getZoneOffset()))
                .icon(openWeatherApiService.createUriForWeatherIcon(object.getWeather().get(0).getIcon()))
                .zoneOffset(object.getZoneOffset())
                .build();
    }

    private Date getDate(long seconds, ZoneOffset zoneOffset) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(seconds),
                zoneOffset
        );
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
