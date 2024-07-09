package com.kittynicky.app.api.entity.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kittynicky.app.api.entity.util.ZoneOffsetDeserializer;
import lombok.Data;

import java.time.ZoneOffset;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {
    @JsonProperty("timezone")
    @JsonDeserialize(using = ZoneOffsetDeserializer.class)
    private ZoneOffset zoneOffset;
}
