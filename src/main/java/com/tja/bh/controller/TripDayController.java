package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.TripDay;
import com.tja.bh.persistence.repository.TripDayRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/day", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class TripDayController {

    private final TripDayRepository repository;

    @Autowired
    public TripDayController(TripDayRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public GenericResponse<List<TripDay>> getAllTripDays() {
        return GenericResponse.success(repository.findAll());
    }

    @GetMapping("/{dayId}")
    public GenericResponse<TripDay> getTripDay(@PathVariable("dayId") Long dayId) {
        val tripDay = repository.findById(dayId);
        if (tripDay.isPresent()) {
            return GenericResponse.success(tripDay.get());
        }

        return GenericResponse.error("No trip day with id=%s found", dayId);
    }

    @DeleteMapping("")
    public GenericResponse<Boolean> deleteTripDay(@RequestBody TripDay tripDay) {
        val dayId = tripDay.getId();
        if (repository.existsById(dayId)) {
            repository.deleteById(dayId);

            return GenericResponse.success(true);
        }

        return GenericResponse.error("No trip day with id=%s found", dayId);
    }

    @PutMapping("")
    public GenericResponse<TripDay> updateTripDay(@RequestBody TripDay tripDay) {
        val dayId = tripDay.getId();
        if (repository.existsById(dayId)) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error("No trip day with id=%s found", dayId);
    }

    @PostMapping("")
    public GenericResponse<TripDay> createTripDay(@RequestBody TripDay tripDay) {
        val dayId = tripDay.getId();
        if (repository.existsById(dayId)) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error("No trip day with id=%s found", dayId);
    }
}
