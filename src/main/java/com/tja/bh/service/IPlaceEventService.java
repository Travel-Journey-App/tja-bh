package com.tja.bh.service;

import com.tja.bh.persistence.model.EventActivity;
import com.tja.bh.persistence.model.enumeration.EventType;

import java.util.List;

public interface IPlaceEventService {
    List<EventActivity> getEventByTypeAndCity(EventType eventType, String city);
}
