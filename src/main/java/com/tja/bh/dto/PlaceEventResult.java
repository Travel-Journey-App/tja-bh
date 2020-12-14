package com.tja.bh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PlaceEventResult {

    private List<EventDto> items;

}
