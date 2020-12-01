package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.TripActivity;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.repository.TripActivityRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api/activity", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Controller
@Slf4j
public class TripActivityController {

    private final TripActivityRepository repository;

    @Autowired
    public TripActivityController(TripActivityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{activityId}")
    public GenericResponse<TripActivity> getTripActivity(@PathVariable("activityId") Long activityId) {
        if (repository.existsById(activityId)) {
            return GenericResponse.success(repository.getOne(activityId));
        }
        return GenericResponse.error();
    }

    @GetMapping
    public GenericResponse<List<TripActivity>> getTripActivitiesByCategory(
            @RequestParam("activityType") @NonNull ActivityType type
    ) {
        return GenericResponse.success(repository.findAllByActivityType(type));
    }

    @DeleteMapping("/{activityId}")
    public GenericResponse<TripActivity> deleteTripActivity(@PathVariable("activityId") Long activityId) {
        if (repository.existsById(activityId)) {
            final TripActivity tripActivity = repository.getOne(activityId);
            repository.deleteById(activityId);
            return GenericResponse.success(tripActivity);
        }
        return GenericResponse.error();
    }

    @PostMapping
    public GenericResponse<TripActivity> createTripActivity(@RequestBody TripActivity activity) {
        return GenericResponse.success(repository.saveAndFlush(activity));
    }

    @PutMapping
    public GenericResponse<TripActivity> updateTripActivity(@RequestBody @NonNull TripActivity activity) {
        if (repository.existsById(activity.getId())) {
            return GenericResponse.success(repository.saveAndFlush(activity));
        }
        return GenericResponse.error();
    }
}
