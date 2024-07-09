package com.kittynicky.app.servlet;

import com.kittynicky.app.api.entity.dto.DailyForecastWeatherDto;
import com.kittynicky.app.api.entity.dto.HourlyForecastWeatherDto;
import com.kittynicky.app.entity.Location;
import com.kittynicky.app.mapper.DailyForecastWeatherMapper;
import com.kittynicky.app.service.WeatherService;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@WebServlet(WebServletPath.FORECAST)
public class ForecastWeatherServlet extends MainServlet {

    private final WeatherService weatherService = WeatherService.getInstance();
    private final DailyForecastWeatherMapper dailyForecastWeatherMapper = DailyForecastWeatherMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templateEngine.process(ViewPath.FORECAST, webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get forecast weather process has started");
        String city = req.getParameter("city");
        BigDecimal latitude = BigDecimal.valueOf(Double.parseDouble(req.getParameter("latitude")));
        BigDecimal longitude = BigDecimal.valueOf(Double.parseDouble(req.getParameter("longitude")));

        Location location = Location.builder()
                .name(city)
                .longitude(longitude)
                .latitude(latitude)
                .build();

        List<HourlyForecastWeatherDto> hourlyForecastWeather = weatherService.getHourlyForecast(location);
        List<HourlyForecastWeatherDto> hourlyForecastWeatherLimit = getHourlyForecastLimit(hourlyForecastWeather);

        List<DailyForecastWeatherDto> dailyForecastWeather = dailyForecastWeatherMapper.mapFrom(hourlyForecastWeather);
        List<DailyForecastWeatherDto> dailyForecastWeatherLimit = getDailyForecastLimit(dailyForecastWeather);

        req.setAttribute("hourlyForecast", hourlyForecastWeatherLimit);
        req.setAttribute("dailyForecast", dailyForecastWeatherLimit);

        log.info("Get forecast weather process has finished successfully");
        doGet(req, resp);
    }

    private List<DailyForecastWeatherDto> getDailyForecastLimit(List<DailyForecastWeatherDto> dailyForecastWeather) {
        return dailyForecastWeather.stream()
                .sorted(Comparator.comparing(DailyForecastWeatherDto::getDate))
                .limit(5)
                .toList();
    }

    private List<HourlyForecastWeatherDto> getHourlyForecastLimit(List<HourlyForecastWeatherDto> hourlyForecastWeather) {
        return hourlyForecastWeather.stream()
                .sorted(Comparator.comparing(HourlyForecastWeatherDto::getDate))
                .limit(5)
                .toList();
    }
}
