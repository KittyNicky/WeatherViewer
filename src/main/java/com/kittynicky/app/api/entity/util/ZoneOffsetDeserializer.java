package com.kittynicky.app.api.entity.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class ZoneOffsetDeserializer extends JsonDeserializer<ZoneId> {
    @Override
    public ZoneOffset deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return ZoneOffset.ofTotalSeconds(p.getIntValue());
    }
}
