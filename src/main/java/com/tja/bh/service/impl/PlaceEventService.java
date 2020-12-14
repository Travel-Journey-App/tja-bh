package com.tja.bh.service.impl;

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

@Profile("prod")
@Service
@Slf4j
public class PlaceEventService implements IPlaceEventService {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public PlaceEventService(@Value("${db.places.baseurl}") String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public EventActivity getEventById(Long id) {
        val request = new HttpEntity<>(null);
        val url = String.format("%s/place?id=%s", baseUrl, id);
        val response = restTemplate.exchange(url, HttpMethod.GET, request,
                PlaceEventResult.class);
        val items = requireNonNull(response.getBody()).getItems();
        if (isNull(items) || items.isEmpty()) {
            log.warn("PlaceEventService. Empty response from places db");
            return null;
        }

        val event = items.get(0);
        val eventActivity = new EventActivity();
        eventActivity.setName(event.getName());
        eventActivity.setDescription(event.getDescription());
        eventActivity.setLatitude(event.getLocation().getLat());
        eventActivity.setLongitude(event.getLocation().getLon());

        return eventActivity;
    }
}
