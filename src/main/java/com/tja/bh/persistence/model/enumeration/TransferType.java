package com.tja.bh.persistence.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransferType {
    @JsonProperty("plane")
    PLANE,

    @JsonProperty("train")
    TRAIN,

    @JsonProperty("ship")
    SHIP,

    @JsonProperty("bus")
    BUS,

    @JsonProperty("car")
    CAR
}
