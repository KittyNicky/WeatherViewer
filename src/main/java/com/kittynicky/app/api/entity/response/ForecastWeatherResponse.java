package com.kittynicky.app.api.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kittynicky.app.api.entity.weather.City;
import com.kittynicky.app.api.entity.weather.Forecast;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastWeatherResponse {
    @JsonProperty("list")
    List<Forecast> forecastList;
    @JsonProperty("city")
    private City city;
}
