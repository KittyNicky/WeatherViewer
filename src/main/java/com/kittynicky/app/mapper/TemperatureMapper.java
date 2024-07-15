package com.kittynicky.app.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemperatureMapper implements Mapper<BigDecimal, BigDecimal> {
    private static final BigDecimal ZERO = BigDecimal.valueOf(-273.15);
    public static final TemperatureMapper INSTANCE = new TemperatureMapper();

    public static TemperatureMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal mapFrom(BigDecimal object) {
        return object.add(ZERO);
    }
}
