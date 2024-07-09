package com.kittynicky.app.mapper;

import com.kittynicky.app.api.entity.response.ForecastWeatherResponse;
import com.kittynicky.app.api.entity.weather.Forecast;
import com.kittynicky.app.api.entity.weather.WeatherCondition;
import com.kittynicky.app.api.service.OpenWeatherApiService;
import com.kittynicky.app.api.entity.dto.HourlyForecastWeatherDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HourlyForecastWeatherMapper implements Mapper<ForecastWeatherResponse, List<HourlyForecastWeatherDto>> {
    private static final HourlyForecastWeatherMapper INSTANCE = new HourlyForecastWeatherMapper();
    private final TemperatureMapper temperatureMapper = TemperatureMapper.getInstance();
    private final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    public static HourlyForecastWeatherMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public List<HourlyForecastWeatherDto> mapFrom(ForecastWeatherResponse response) {
        List<HourlyForecastWeatherDto> weather = new ArrayList<>();
        for (Forecast forecast : response.getForecastList()) {
            HourlyForecastWeatherDto weatherDto = HourlyForecastWeatherDto.builder()
                    .date(getDate(forecast.getDateTime(), response.getCity().getZoneOffset()))
                    .temperature(temperatureMapper.mapFrom(forecast.getMain().getTemperature()))
                    .temperatureMin(temperatureMapper.mapFrom(forecast.getMain().getTemperatureMin()))
                    .temperatureMax(temperatureMapper.mapFrom(forecast.getMain().getTemperatureMax()))
                    .weatherCondition(WeatherCondition.getWeatherCondition(forecast.getWeather().get(0).getId()))
                    .description(forecast.getWeather().get(0).getDescription())
                    .icon(openWeatherApiService.createUriForWeatherIcon(forecast.getWeather().get(0).getIcon()))
                    .zoneOffset(response.getCity().getZoneOffset())
                    .build();
            weather.add(weatherDto);
        }
        return weather;
    }

    private Date getDate(long seconds, ZoneOffset zoneOffset) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(seconds),
                zoneOffset
        );
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
