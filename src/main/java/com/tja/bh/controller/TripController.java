package com.tja.bh.controller;


import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.Trip;
import com.tja.bh.persistence.model.TripDay;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.repository.TripRepository;
import com.tja.bh.service.IPlaceEventService;
import com.tja.bh.service.IUserService;
import com.tja.bh.unsplash.api.UnsplashController;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
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

    private final IPlaceEventService placeEventService;

    @Autowired
    public TripController(TripRepository tripRepository, UnsplashController unsplashController,
                          IUserService userService, IPlaceEventService placeEventService) {
        this.tripRepository = tripRepository;
        this.unsplashController = unsplashController;
        this.userService = userService;
        this.placeEventService = placeEventService;
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
            tripRepository.deleteById(trip.getId());
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
            initializeDays(currentTrip);
            for (int i = 0; i < currentTrip.getDays().size() && i < trip.getDays().size(); i++) {
                currentTrip.getDays().get(i).getActivities().addAll(trip.getDays().get(i).getActivities());
            }

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
        return GenericResponse.success(updateTripWithMagicData(Trip.builder()
                .name("New Year in Tokyo")
                .destination("Tokyo")
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 1, 10))
                .build()));
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
        initializeDays(trip);
        linkWithUser(trip, user);

        return GenericResponse.success(tripRepository.saveAndFlush(trip));
    }

    private void linkWithUser(Trip trip, User user) {
        trip.setUser(user);
    }

    private void initializeDays(Trip trip) {
        val tripDuration = Duration.between(
                new Timestamp(trip.getStartDate().getTime()).toLocalDateTime(),
                new Timestamp(trip.getEndDate().getTime()).toLocalDateTime());
        val tripLength = tripDuration.toDays() + 1;

        val days = new ArrayList<TripDay>((int) tripLength);
        for (long i = 1; i <= tripLength; ++i) {
            days.add(TripDay.builder()
                    .orderInTrip(i)
                    .trip(trip)
                    .build());
        }

        trip.setDays(days);
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

    Trip getIfBelongsToUser(Long tripId) {
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
        return user.equals(trip.getUser());
    }

    private Trip updateTripWithMagicData(Trip trip) {
//        val activities = new Random().ints(30, 0, 29)
//                .mapToObj(id -> placeEventService.getEventById((long) id))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        val days = new ArrayList<TripDay>();
//        for (long i = 0; i < activities.size(); i += 5) {
//            val day = new TripDay();
//            day.setOrderInTrip(i / 5);
//            day.setTrip(trip);
//            val activitiesInDay = Lists.<TripActivity>newArrayList();
//            for (long j = i; j < i + 5 && j < activities.size(); j++) {
//                val activity = activities.get((int) j);
//                activity.setTripDay(day);
//                activitiesInDay.add(activity);
//            }
//            day.setActivities(activitiesInDay);
//            days.add(day);
//        }
//
//        trip.setDays(days);

        return trip;
    }
}
