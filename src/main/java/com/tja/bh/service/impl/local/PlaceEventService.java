package com.tja.bh.service.impl.local;

import com.tja.bh.dto.PlaceEventResult;
import com.tja.bh.persistence.model.EventActivity;
import com.tja.bh.service.IPlaceEventService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Profile("default")
@Service
public class PlaceEventService implements IPlaceEventService {

    @Override
    public EventActivity getEventById(Long id) {
        return EventActivity.builder()
                .build();
    }
}
