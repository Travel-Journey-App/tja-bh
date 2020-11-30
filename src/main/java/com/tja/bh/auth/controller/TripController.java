package com.tja.bh.auth.controller;


import com.tja.bh.auth.GenericResponse;
import com.tja.bh.auth.persistence.model.Trip;
import com.tja.bh.auth.persistence.repository.TripRepository;
import com.tja.bh.unsplash.api.UnsplashController;
import com.tja.bh.unsplash.dto.Photo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api/trips", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Controller
@Slf4j
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UnsplashController unsplashController;

    @GetMapping
    public GenericResponse<List<Trip>> getAllTrips() {
        return GenericResponse.success(tripRepository.findAll());
    }

    @GetMapping("/{tripId}")
    public GenericResponse<Trip> getTrip(@PathVariable("tripId") long tripId) {
        if (tripRepository.existsById(tripId)) {
            return GenericResponse.success(tripRepository.getOne(tripId));
        }
        return GenericResponse.error();
    }

    @DeleteMapping("/{tripId}")
    public GenericResponse<Trip> deleteTrip(@PathVariable("tripId") long tripId) {
        if (tripRepository.existsById(tripId)) {
            final Trip deletingTrip = tripRepository.getOne(tripId);
            tripRepository.deleteById(tripId);
            return GenericResponse.success(deletingTrip);
        }
        return GenericResponse.error();
    }

    @PutMapping
    public GenericResponse<Trip> editTrip(@RequestBody Trip trip) {
        if (tripRepository.existsById(trip.getId())) {
            final Trip currentTrip = tripRepository.getOne(trip.getId());
            if (!trip.getCover().equals(currentTrip.getCover())) {
                final GenericResponse<Photo> photo = unsplashController.getPhoto(trip.getDestination());
                if (photo.getStatus() == GenericResponse.Status.ERROR) {
                    return GenericResponse.error();
                }
                trip.setCover(photo.getBody().getLinks().getDownload());
            }

            return GenericResponse.success(tripRepository.saveAndFlush(trip));
        }
        return GenericResponse.error();
    }

    @PostMapping
    public GenericResponse<Trip> createTrip(@RequestBody Trip trip) {
        if (trip.getDestination().isBlank()) {
            return GenericResponse.error();
        }
        final GenericResponse<Photo> photo = unsplashController.getPhoto(trip.getDestination());
        if (photo.getStatus() == GenericResponse.Status.ERROR) {
            return GenericResponse.error();
        }
        trip.setCover(photo.getBody().getLinks().getDownload());
        return GenericResponse.success(tripRepository.saveAndFlush(trip));
    }

    @PutMapping("/magic/{tripId}")
    public GenericResponse<Trip> createMagicTripPlan(@PathVariable("tripId") long tripId,
                                                     @RequestParam("lat") @NonNull double lat,
                                                     @RequestParam("lon") @NonNull double lon,
                                                     @RequestParam("days") @NonNull int days) {
        if (tripRepository.existsById(tripId)) {
            Trip trip = tripRepository.getOne(tripId);
            // todo add activities
            trip.setDays(List.of());
            return GenericResponse.success(tripRepository.saveAndFlush(trip));
        }

        return GenericResponse.error();
    }
}
