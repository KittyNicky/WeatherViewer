package com.kittynicky.app.api.entity.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinates {
    @JsonProperty("lon")
    private BigDecimal longitude;
    @JsonProperty("lat")
    private BigDecimal latitude;
}
