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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
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
        val user = userService.getUser();
        val userTrips = tripRepository.findAll().stream()
                .filter(trip -> isBelongsToUser(trip, user))
                .collect(Collectors.toList());

        return GenericResponse.success(userTrips);
    }

    @GetMapping("/{tripId}")
    public GenericResponse<Trip> getTrip(@PathVariable("tripId") long tripId) {
        val trip = tripRepository.findById(tripId);
        if (trip.isPresent()) {
            return GenericResponse.success(trip.get());
        }

        return GenericResponse.error("No trip with id=%s found", tripId);
    }

    @DeleteMapping("/{tripId}")
    public GenericResponse<Boolean> deleteTrip(@PathVariable("tripId") long tripId) {
        try {
            val trip = getIfBelongsToUser(tripId);
            tripRepository.delete(trip);
            return GenericResponse.success(true);
        } catch (Exception e) {
            return GenericResponse.error("Exception occurred while deleting trip with id=%s: %s", tripId,
                    e.getMessage());
        }
    }

    @PutMapping("")
    public GenericResponse<Trip> editTrip(@RequestBody Trip trip) {
        val tripId = trip.getId();
        try {
            val currentTrip = getIfBelongsToUser(tripId);
            currentTrip.setName(trip.getName());
            currentTrip.setDestination(trip.getDestination());
            currentTrip.setStartDate(trip.getStartDate());
            currentTrip.setEndDate(trip.getEndDate());
            currentTrip.setDays(trip.getDays());

            if (!currentTrip.getDestination().equals(trip.getDestination())) {
                val photoResponse = unsplashController.getPhoto(trip.getDestination());
                if (photoResponse.getStatus() == GenericResponse.Status.ERROR) {
                    return GenericResponse.error("Could not extract photo for the trip: %s", photoResponse.getError());
                }
                trip.setCover(photoResponse.getBody().getLinks().getDownload());
            }

            return GenericResponse.success(tripRepository.saveAndFlush(currentTrip));
        } catch (Exception e) {
            return GenericResponse.error("Exception occurred while editing trip with id=%s: %s", tripId,
                    e.getMessage());
        }
    }

    @PostMapping("")
    public GenericResponse<Trip> createTrip(@RequestBody Trip trip) {
        if (isBlank(trip.getDestination())) {
            return GenericResponse.error("No destination is passed");
        }

        return createTripResponse(trip);
    }

    @GetMapping("/magic")
    public GenericResponse<Trip> getMagicTrip() {
        return GenericResponse.success(updateTripWithMagicData(new Trip()));
    }

    private GenericResponse<Trip> createTripResponse(Trip trip) {
        val user = userService.getUser();
        if (isAlreadyExists(trip, user)) {
            log.warn("TripController. User already created this trip. Editing existing one");
            return editTrip(trip);
        }

        val photoResponse = unsplashController.getPhoto(trip.getDestination());
        if (photoResponse.getStatus() == GenericResponse.Status.ERROR) {
            return GenericResponse.error("Could not extract photo for the trip: %s", photoResponse.getError());
        }

        trip.setCover(photoResponse.getBody().getLinks().getDownload());
        trip.setUser(user);

        return GenericResponse.success(tripRepository.saveAndFlush(trip));
    }

    private boolean isAlreadyExists(Trip trip, User user) {
        val sameTrips = tripRepository.findAllSameTrips(trip);
        if (isNull(sameTrips) || sameTrips.isEmpty()) {
            return false;
        }

        val existingTrip = sameTrips.stream()
                .filter(sameTrip -> isBelongsToUser(sameTrip, user))
                .findFirst();

        val isAlreadyExists = existingTrip.isPresent();
        if (isAlreadyExists) {
            val existingId = existingTrip.get().getId();
            log.debug("TripController. Trip is already exists with id={}", existingId);
            trip.setId(existingId);
        }

        return isAlreadyExists;
    }

    private Trip getIfBelongsToUser(Long tripId) {
        return getIfBelongsToUser(tripId, userService.getUser());
    }

    private Trip getIfBelongsToUser(Long tripId, User user) {
        val optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new RuntimeException("Trip does not exist");
        }

        val existingTrip = optionalTrip.get();
        if (!isBelongsToUser(existingTrip, user)) {
            throw new RuntimeException("Trip does not belong to user");
        }

        return existingTrip;
    }

    private boolean isBelongsToUser(Trip trip, User user) {
        return user.getId().equals(trip.getUser().getId());
    }

    private Trip updateTripWithMagicData(Trip trip) {
        //TODO Add mock data
        trip.setName("Моя волшебная поездка");
        trip.setDestination("Хуево-кукуево");
        trip.setStartDate(new Date(2020, 12, 31));
        trip.setEndDate(new Date(2021, 1, 10));

        return trip;
    }
}
