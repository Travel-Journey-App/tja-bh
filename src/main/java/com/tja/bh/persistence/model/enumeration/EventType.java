package com.tja.bh.persistence.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventType {
    @JsonProperty("food")
    FOOD,
    @JsonProperty("bar")
    BAR,
    @JsonProperty("gallery")
    GALLERY,
    @JsonProperty("sightseeing")
    SIGHTSEEING,
    @JsonProperty("museum")
    MUSEUM,
    @JsonProperty("fun")
    FUN
}
