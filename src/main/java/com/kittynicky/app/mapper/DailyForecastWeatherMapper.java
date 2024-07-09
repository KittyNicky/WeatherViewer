package com.kittynicky.app.mapper;

import com.kittynicky.app.api.entity.weather.WeatherCondition;
import com.kittynicky.app.api.service.OpenWeatherApiService;
import com.kittynicky.app.api.entity.dto.DailyForecastWeatherDto;
import com.kittynicky.app.api.entity.dto.HourlyForecastWeatherDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyForecastWeatherMapper implements Mapper<List<HourlyForecastWeatherDto>, List<DailyForecastWeatherDto>> {
    private static final DailyForecastWeatherMapper INSTANCE = new DailyForecastWeatherMapper();
    private static final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    public static DailyForecastWeatherMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public List<DailyForecastWeatherDto> mapFrom(List<HourlyForecastWeatherDto> hourlyForecast) {
        Map<LocalDate, List<HourlyForecastWeatherDto>> collect = getCollectWeatherByDate(hourlyForecast);

        return collect.values().stream()
                .map(DailyForecastWeatherMapper::buildDailyWeatherDto)
                .toList();
    }

    private static DailyForecastWeatherDto buildDailyWeatherDto(List<HourlyForecastWeatherDto> hourlyForecast) {
        WeatherCondition avgWeatherCondition = getAverageWeatherCondition(hourlyForecast);
        return DailyForecastWeatherDto.builder()
                .date(hourlyForecast.get(0).getDate())
                .temperature(getAvgTemperature(hourlyForecast))
                .temperatureMin(getMinTemperature(hourlyForecast))
                .temperatureMax(getMaxTemperature(hourlyForecast))
                .weatherCondition(avgWeatherCondition)
                .icon(openWeatherApiService.createUriForWeatherIcon(avgWeatherCondition.getDefaultIcon()))
                .build();
    }

    private static BigDecimal getMaxTemperature(List<HourlyForecastWeatherDto> hourlyForecast) {
        return hourlyForecast.stream()
                .map(HourlyForecastWeatherDto::getTemperatureMax)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal getMinTemperature(List<HourlyForecastWeatherDto> hourlyForecast) {
        return hourlyForecast.stream()
                .map(HourlyForecastWeatherDto::getTemperatureMin)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal getAvgTemperature(List<HourlyForecastWeatherDto> hourlyForecast) {
        return hourlyForecast.stream()
                .map(HourlyForecastWeatherDto::getTemperature)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(hourlyForecast.size()), 2, RoundingMode.HALF_UP);
    }

    private static WeatherCondition getAverageWeatherCondition(List<HourlyForecastWeatherDto> hourlyForecast) {
        return hourlyForecast.stream()
                .collect(Collectors.groupingBy(HourlyForecastWeatherDto::getWeatherCondition, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(WeatherCondition.UNDEFINED);
    }

    private Map<LocalDate, List<HourlyForecastWeatherDto>> getCollectWeatherByDate(List<HourlyForecastWeatherDto> weatherAfterToday) {
        return weatherAfterToday.stream()
                .collect(Collectors.groupingBy(dto -> LocalDate.ofInstant(dto.getDate().toInstant(), ZoneId.systemDefault())));
    }
}
