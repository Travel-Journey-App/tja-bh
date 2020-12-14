package com.tja.bh.service;

import com.tja.bh.persistence.model.EventActivity;

public interface IPlaceEventService {
    EventActivity getEventById(Long id);
}
