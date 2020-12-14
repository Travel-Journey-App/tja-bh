package com.tja.bh.persistence.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActivityType {
    @JsonProperty("transfer")
    TRANSFER,

    @JsonProperty("accommodation")
    ACCOMMODATION,

    @JsonProperty("event")
    EVENT
}
