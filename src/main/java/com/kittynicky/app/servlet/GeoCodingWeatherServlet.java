package com.kittynicky.app.servlet;

import com.kittynicky.app.api.entity.dto.WeatherDto;
import com.kittynicky.app.service.WeatherService;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(WebServletPath.GEO_LOCATION)
public class GeoCodingWeatherServlet extends MainServlet {
    private final WeatherService weatherService = WeatherService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templateEngine.process(ViewPath.WEATHER, webContext, resp.getWriter());
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Searching location process has started");
        String city = req.getParameter("city");

        List<WeatherDto> geoCodingWeather = weatherService.getGeocodingWeather(city);
        webContext.setVariable("geoCodingWeather", geoCodingWeather);
        webContext.setVariable("city", city);

        log.info("Searching location process has finished successfully");
        doGet(req, resp);
    }
}
