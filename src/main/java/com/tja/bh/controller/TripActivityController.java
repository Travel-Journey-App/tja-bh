package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.TripActivity;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.repository.TripActivityRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/activity", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class TripActivityController {

    private final TripActivityRepository repository;

    @Autowired
    public TripActivityController(TripActivityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{activityId}")
    public GenericResponse<TripActivity> getTripActivity(@PathVariable("activityId") Long activityId) {
        val activity = repository.findById(activityId);
        if (activity.isPresent()) {
            return GenericResponse.success(activity.get());
        }

        return GenericResponse.error("No activity with id=%s found", activityId);
    }

    @GetMapping("")
    public GenericResponse<List<TripActivity>> getTripActivitiesByCategory(
            @RequestParam("activityType") @NonNull ActivityType type
    ) {
        return GenericResponse.success(repository.findAllByActivityType(type));
    }

    @DeleteMapping("/{activityId}")
    public GenericResponse<Boolean> deleteTripActivity(@PathVariable("activityId") Long activityId) {
        val activity = repository.findById(activityId);
        if (activity.isPresent()) {
            repository.deleteById(activityId);
            return GenericResponse.success(true);
        }

        return GenericResponse.error("No activity with id=%s found", activityId);
    }

    @PostMapping("")
    public GenericResponse<TripActivity> createTripActivity(@RequestBody TripActivity activity) {
        return GenericResponse.success(repository.saveAndFlush(activity));
    }

    @PutMapping("")
    public GenericResponse<TripActivity> updateTripActivity(@RequestBody @NonNull TripActivity activity) {
        val activityId = activity.getId();
        if (repository.existsById(activity.getId())) {
            return GenericResponse.success(repository.saveAndFlush(activity));
        }

        return GenericResponse.error("No activity with id=%s found", activityId);
    }
}
