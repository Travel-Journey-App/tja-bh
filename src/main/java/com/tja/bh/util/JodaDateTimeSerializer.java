package com.tja.bh.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class JodaDateTimeSerializer extends JsonSerializer<DateTime> {

    private final static DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DATETIME_FORMAT.print(value));
    }
}
