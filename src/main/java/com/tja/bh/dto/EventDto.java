package com.tja.bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tja.bh.persistence.model.enumeration.EventType;
import com.tja.bh.persistence.model.enumeration.PriceType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventDto {

    private Long id;

    private String name;

    private String description;

    private String photo;

    private double rating;

    @JsonProperty("extra_name")
    private String extraName;

    private Location location;

    private PriceType price;

    @JsonProperty("working_hours")
    private String workingHours;

    private EventType category;

}
