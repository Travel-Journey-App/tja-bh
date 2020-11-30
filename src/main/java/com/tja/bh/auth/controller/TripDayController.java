package com.tja.bh.auth.controller;

import com.tja.bh.auth.GenericResponse;
import com.tja.bh.auth.persistence.model.TripDay;
import com.tja.bh.auth.persistence.repository.TripDayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api/day", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Controller
@Slf4j
public class TripDayController {

    @Autowired
    private TripDayRepository repository;

    @GetMapping("/all")
    public GenericResponse<List<TripDay>> getAllTripDays() {
        return GenericResponse.success(repository.findAll());
    }

    @GetMapping("/{dayId}")
    public GenericResponse<TripDay> getTripDay(@PathVariable("dayId") Long dayId) {
        if (repository.existsById(dayId)) {
            return GenericResponse.success(repository.getOne(dayId));
        }

        return GenericResponse.error();
    }

    @DeleteMapping
    public GenericResponse deleteTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            final TripDay deletingTripDay = repository.getOne(tripDay.getId());
            repository.delete(tripDay);
            return GenericResponse.success(deletingTripDay);
        }

        return GenericResponse.error();
    }

    @PutMapping
    public GenericResponse updateTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error();
    }

    @PostMapping
    public GenericResponse createTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error();
    }
}
