package com.kittynicky.app.service;

import com.kittynicky.app.api.entity.response.CurrentWeatherResponse;
import com.kittynicky.app.api.entity.response.ForecastWeatherResponse;
import com.kittynicky.app.api.entity.response.LocationWeatherResponse;
import com.kittynicky.app.api.service.OpenWeatherApiService;
import com.kittynicky.app.api.entity.dto.DailyForecastWeatherDto;
import com.kittynicky.app.api.entity.dto.HourlyForecastWeatherDto;
import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.api.entity.dto.WeatherDto;
import com.kittynicky.app.entity.Location;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.mapper.CurrentWeatherMapper;
import com.kittynicky.app.mapper.DailyForecastWeatherMapper;
import com.kittynicky.app.mapper.HourlyForecastWeatherMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherService {
    private static final WeatherService INSTANCE = new WeatherService();
    private final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();
    private final LocationService locationService = LocationService.getInstance();
    private final CurrentWeatherMapper currentWeatherMapper = CurrentWeatherMapper.getInstance();
    private final HourlyForecastWeatherMapper hourlyForecastWeatherMapper = HourlyForecastWeatherMapper.getInstance();
    private final DailyForecastWeatherMapper dailyForecastWeatherMapper = DailyForecastWeatherMapper.getInstance();

    public static WeatherService getInstance() {
        return INSTANCE;
    }

    public List<WeatherDto> getCurrentWeather(UserDto userDto) {
        User user = User.builder().id(userDto.getId()).build();
        List<WeatherDto> locations = new ArrayList<>();
        locationService.get(user).forEach(
                location -> locations.add(getCurrentWeather(location))
        );
        return locations;
    }

    public List<WeatherDto> getGeocodingWeather(String city) {
        List<WeatherDto> locations = new ArrayList<>();
        List<LocationWeatherResponse> locationWeatherResponse = openWeatherApiService.getGeocodingLocations(city);
        locationWeatherResponse.forEach(
                response -> {
                    Location location = Location.builder()
                            .name(response.getName())
                            .longitude(response.getLongitude())
                            .latitude(response.getLatitude())
                            .build();
                    locations.add(getCurrentWeather(location));
                }
        );
        return locations;
    }

    public List<HourlyForecastWeatherDto> getHourlyForecast(Location location) {
        ForecastWeatherResponse forecastWeatherResponse = openWeatherApiService.getForecastWeather(location.getLongitude(), location.getLatitude());
        return hourlyForecastWeatherMapper.mapFrom(forecastWeatherResponse);
    }

    public WeatherDto getCurrentWeather(Location location) {
        CurrentWeatherResponse currentWeatherResponse = openWeatherApiService.getCurrentWeather(location.getLongitude(), location.getLatitude());
        currentWeatherResponse.setCityName(location.getName());
        return currentWeatherMapper.mapFrom(currentWeatherResponse);
    }
}
