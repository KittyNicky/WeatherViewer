package com.kittynicky.app.api.entity.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {
    @JsonProperty("temp")
    private BigDecimal temperature;
    @JsonProperty("feels_like")
    private BigDecimal feelsLike;
    @JsonProperty("temp_min")
    private BigDecimal temperatureMin;
    @JsonProperty("temp_max")
    private BigDecimal temperatureMax;
}
