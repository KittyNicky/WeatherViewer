package com.kittynicky.app.api.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kittynicky.app.api.entity.util.ZoneOffsetDeserializer;
import com.kittynicky.app.api.entity.weather.Coordinates;
import com.kittynicky.app.api.entity.weather.Main;
import com.kittynicky.app.api.entity.weather.Sys;
import com.kittynicky.app.api.entity.weather.Weather;
import lombok.Data;

import java.time.ZoneOffset;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherResponse {
    @JsonProperty("id")
    private int id;
    @JsonProperty("coord")
    private Coordinates coordinates;
    @JsonProperty("weather")
    private List<Weather> weather;
    @JsonProperty("main")
    private Main main;
    @JsonProperty("sys")
    private Sys sys;
    @JsonProperty("name")
    private String cityName;
    @JsonProperty("timezone")
    @JsonDeserialize(using = ZoneOffsetDeserializer.class)
    private ZoneOffset zoneOffset;
}
