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

@RequestMapping(value = "/api/day", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Controller
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
        if (repository.existsById(dayId)) {
            return GenericResponse.success(repository.getOne(dayId));
        }

        return GenericResponse.error();
    }

    @DeleteMapping
    public GenericResponse<TripDay> deleteTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            val deletingTripDay = repository.getOne(tripDay.getId());
            repository.delete(tripDay);

            return GenericResponse.success(deletingTripDay);
        }

        return GenericResponse.error();
    }

    @PutMapping
    public GenericResponse<TripDay> updateTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error();
    }

    @PostMapping
    public GenericResponse<TripDay> createTripDay(@RequestBody TripDay tripDay) {
        if (repository.existsById(tripDay.getId())) {
            return GenericResponse.success(repository.saveAndFlush(tripDay));
        }

        return GenericResponse.error();
    }
}
