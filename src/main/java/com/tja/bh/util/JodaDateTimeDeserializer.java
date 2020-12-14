package com.tja.bh.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class JodaDateTimeDeserializer extends JsonDeserializer<DateTime> {

    private final static DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        val date = jsonParser.getText();

        return DateTime.parse(date, DATETIME_FORMAT);
    }

}
