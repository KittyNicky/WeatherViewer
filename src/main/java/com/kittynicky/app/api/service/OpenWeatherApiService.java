package com.kittynicky.app.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kittynicky.app.api.config.OpenWeatherApiConfig;
import com.kittynicky.app.api.entity.response.CurrentWeatherResponse;
import com.kittynicky.app.api.entity.response.ForecastWeatherResponse;
import com.kittynicky.app.api.entity.response.LocationWeatherResponse;
import com.kittynicky.app.api.excepiton.CurrentWeatherApiCallException;
import com.kittynicky.app.api.excepiton.ForecastWeatherApiCallException;
import com.kittynicky.app.api.excepiton.GeocodingLocationsApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenWeatherApiService {
    private static final OpenWeatherApiService INSTANCE = new OpenWeatherApiService();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);
    private final String ERROR_MESSAGE = "The API method call ended with an error: ";

    public static OpenWeatherApiService getInstance() {
        return INSTANCE;
    }

    public List<LocationWeatherResponse> getGeocodingLocations(String cityName) {
        try {
            URI uri = createUriForGeocodingLocationRequest(cityName);
            HttpRequest request = buildRequest(uri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new GeocodingLocationsApiException(ERROR_MESSAGE + e.getMessage());
        }
    }

    public CurrentWeatherResponse getCurrentWeather(BigDecimal longitude, BigDecimal latitude) {
        try {
            URI uri = createUriForWeatherRequest(longitude, latitude);
            HttpRequest request = buildRequest(uri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), CurrentWeatherResponse.class);
        } catch (Exception e) {
            throw new CurrentWeatherApiCallException(ERROR_MESSAGE + e.getMessage());
        }
    }

    public ForecastWeatherResponse getForecastWeather(BigDecimal longitude, BigDecimal latitude) {
        try {
            URI uri = createUriForForecastRequest(longitude, latitude);
            HttpRequest request = buildRequest(uri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), ForecastWeatherResponse.class);
        } catch (Exception e) {
            throw new ForecastWeatherApiCallException(ERROR_MESSAGE + e.getMessage());
        }
    }

    public URI createUriForWeatherIcon(String code) {
        return URI.create(String.format(OpenWeatherApiConfig.getIconUrl(), code));
    }


    private URI createUriForGeocodingLocationRequest(String cityName) {
        return URI.create(OpenWeatherApiConfig.getUrl()
                          + OpenWeatherApiConfig.getGeoSuffix()
                          + "?q=" + cityName.replace(" ", "%20")
                          + "&limit=5"
                          + "&appid=" + OpenWeatherApiConfig.getKey());
    }

    private URI createUriForForecastRequest(BigDecimal longitude, BigDecimal latitude) {
        return URI.create(OpenWeatherApiConfig.getUrl()
                          + OpenWeatherApiConfig.getForecastSuffix()
                          + "?lat=" + latitude
                          + "&lon=" + longitude
                          + "&appid=" + OpenWeatherApiConfig.getKey());
    }

    private URI createUriForWeatherRequest(BigDecimal longitude, BigDecimal latitude) {
        return URI.create(OpenWeatherApiConfig.getUrl()
                          + OpenWeatherApiConfig.getWeatherSuffix()
                          + "?lat=" + latitude
                          + "&lon=" + longitude
                          + "&appid=" + OpenWeatherApiConfig.getKey());
    }


    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(TIMEOUT)
                .GET()
                .build();
    }
}
