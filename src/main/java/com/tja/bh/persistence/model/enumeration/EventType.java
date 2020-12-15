package com.tja.bh.persistence.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public enum EventType {
    @JsonProperty("breakfast")
    BREAKFAST,
    @JsonProperty("lunch")
    LUNCH,
    @JsonProperty("dinner")
    DINNER,
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
    FUN;

    public static Set<EventType> FOOD_TYPES = newHashSet(BREAKFAST, LUNCH, DINNER);
}
