package com.tja.bh.controller;


import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.Trip;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.repository.TripRepository;
import com.tja.bh.service.IUserService;
import com.tja.bh.unsplash.api.UnsplashController;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/trips", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class TripController {

    private final IUserService userService;

    private final TripRepository tripRepository;

    private final UnsplashController unsplashController;

    @Autowired
    public TripController(TripRepository tripRepository, UnsplashController unsplashController, IUserService userService) {
        this.tripRepository = tripRepository;
        this.unsplashController = unsplashController;
        this.userService = userService;
    }

    @GetMapping("")
    public GenericResponse<List<Trip>> getAllTrips() {
        return GenericResponse.success(tripRepository.findAll());
    }

    @GetMapping("/{tripId}")
    public GenericResponse<Trip> getTrip(@PathVariable("tripId") long tripId) {
        if (tripRepository.existsById(tripId)) {
            return GenericResponse.success(tripRepository.getOne(tripId));
        }

        return GenericResponse.error("No trip with id=%s found", tripId);
    }

    @DeleteMapping("/{tripId}")
    public GenericResponse<Trip> deleteTrip(@PathVariable("tripId") long tripId) {
        if (tripRepository.existsById(tripId)) {
            final Trip deletingTrip = tripRepository.getOne(tripId);
            tripRepository.deleteById(tripId);
            return GenericResponse.success(deletingTrip);
        }

        return GenericResponse.error("No trip with id=%s found", tripId);
    }

    @PutMapping("")
    public GenericResponse<Trip> editTrip(@RequestBody Trip trip) {
        val tripId = trip.getId();
        if (tripRepository.existsById(tripId)) {
            val currentTrip = tripRepository.getOne(trip.getId());
            if (!trip.getCover().equals(currentTrip.getCover())) {
                val photoResponse = unsplashController.getPhoto(trip.getDestination());
                if (photoResponse.getStatus() == GenericResponse.Status.ERROR) {
                    return GenericResponse.error("Could not extract photo for the trip: %s", photoResponse.getError());
                }
                trip.setCover(photoResponse.getBody().getLinks().getDownload());
            }

            return GenericResponse.success(tripRepository.saveAndFlush(trip));
        }

        return GenericResponse.error("No trip with id=%s found", tripId);
    }

    @PostMapping("")
    public GenericResponse<Trip> createTrip(@RequestBody Trip trip) {
        if (trip.getDestination().isBlank()) {
            return GenericResponse.error("No destination is passed");
        }

        val user = userService.getUser();
        if (checkIfTripAlreadyExists(trip, user)) {
            return GenericResponse.error("User already created this trip");
        }

        val photoResponse = unsplashController.getPhoto(trip.getDestination());
        if (photoResponse.getStatus() == GenericResponse.Status.ERROR) {
            return GenericResponse.error("Could not extract photo for the trip: %s", photoResponse.getError());
        }

        trip.setCover(photoResponse.getBody().getLinks().getDownload());
        trip.setUser(user);
        return GenericResponse.success(tripRepository.saveAndFlush(trip));
    }

    @GetMapping("/magic")
    public GenericResponse<Trip> createMagicTrip() {
        val trip = Trip.builder()
                //TODO add content
                .build();

        return GenericResponse.success(tripRepository.saveAndFlush(trip));
    }

    private boolean checkIfTripAlreadyExists(Trip trip, User user) {
        val sameTrips = tripRepository.findAllSameTrips(trip);
        return nonNull(sameTrips) && sameTrips.stream()
                .anyMatch(sameTrip -> user.equals(sameTrip.getUser()));
    }
}
