package com.tja.bh.persistence.model.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

public enum PriceType {
    @JsonProperty("cheap")
    CHEAP,
    @JsonProperty("medium")
    MEDIUM,
    @JsonProperty("expensive")
    EXPENSIVE;
}
