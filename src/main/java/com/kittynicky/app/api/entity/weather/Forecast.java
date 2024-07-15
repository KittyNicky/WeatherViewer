package com.kittynicky.app.api.entity.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {
    @JsonProperty("dt")
    private long dateTime;
    @JsonProperty("main")
    private Main main;
    @JsonProperty("weather")
    private List<Weather> weather;
}
