package com.tja.bh.service.impl.local;

import com.tja.bh.persistence.model.EventActivity;
import com.tja.bh.persistence.model.enumeration.EventType;
import com.tja.bh.service.IPlaceEventService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Profile("default")
@Service
public class PlaceEventService implements IPlaceEventService {

    @Override
    public List<EventActivity> getEventByTypeAndCity(EventType eventType, String city) {
        return Collections.singletonList(
                EventActivity.builder()
                        .build());
    }
}
