package com.tja.bh.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tja.bh.dto.PlaceEventResult;
import com.tja.bh.persistence.model.EventActivity;
import com.tja.bh.persistence.model.enumeration.EventType;
import com.tja.bh.service.IPlaceEventService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tja.bh.persistence.model.enumeration.EventType.FOOD;
import static com.tja.bh.persistence.model.enumeration.EventType.FOOD_TYPES;
import static java.util.Objects.*;

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
    public List<EventActivity> getEventByTypeAndCity(EventType eventType, String city) {
        val request = new HttpEntity<>(null);
        val url = String.format("%s/place?category=%s&city=%s",
                baseUrl,
                eventType.name().substring(0, 1).toUpperCase() + eventType.name().substring(1).toLowerCase(),
                city.toLowerCase());

        PlaceEventResult placeEvents;
        try {
            val response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            log.debug("On request={} got response={}", url, response.getBody());
            placeEvents = new ObjectMapper().readValue(response.getBody(), PlaceEventResult.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        val items = requireNonNull(placeEvents).getItems();
        if (isNull(items) || items.isEmpty()) {
            log.warn("PlaceEventService. Empty response from places db");
            return null;
        }

        return items.stream()
                .map(event -> {
                    val eventActivity = new EventActivity();
                    eventActivity.setName(event.getName());
                    eventActivity.setDescription(event.getDescription());
                    eventActivity.setLatitude(event.getLocation().getLat());
                    eventActivity.setLongitude(event.getLocation().getLon());
                    eventActivity.setDescription(event.getDescription());
                    eventActivity.setEventType(FOOD_TYPES.contains(eventType) ? FOOD : eventType);
                    val workingHours = event.getWorkingHours();
                    if (nonNull(workingHours)) {
                        eventActivity.setStartTime(new Date(workingHours.getOpen().getTime() * 1000));
                        eventActivity.setEndTime(new Date(workingHours.getClose().getTime() * 1000));
                    }
                    return eventActivity;
                })
                .collect(Collectors.toList());
    }
}
