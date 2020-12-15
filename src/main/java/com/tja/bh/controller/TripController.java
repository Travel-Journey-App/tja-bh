package com.tja.bh.controller;


import com.google.common.collect.Lists;
import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.Trip;
import com.tja.bh.persistence.model.TripActivity;
import com.tja.bh.persistence.model.TripDay;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.model.enumeration.EventType;
import com.tja.bh.persistence.repository.TripRepository;
import com.tja.bh.service.IPlaceEventService;
import com.tja.bh.service.IUserService;
import com.tja.bh.unsplash.api.UnsplashController;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/trips", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class TripController {
    private static final long SECONDS_IN_DAY = 60 * 60 * 24;

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
            currentTrip.setStartDate(trip.getStartDate());
            currentTrip.setEndDate(trip.getEndDate());

            if (!currentTrip.getDestination().equals(trip.getDestination())) {
                val photoResponse = unsplashController.getPhoto(trip.getDestination());
                if (photoResponse.getStatus() == GenericResponse.Status.ERROR) {
                    return GenericResponse.error("Could not extract photo for the trip: %s", photoResponse.getError());
                }
                currentTrip.setDestination(trip.getDestination());
                currentTrip.setCover(photoResponse.getBody().getLinks().getDownload());
                initializeDays(currentTrip);
            } else {
                for (int i = 0; i < currentTrip.getDays().size() && i < trip.getDays().size(); i++) {
                    val currentDay = currentTrip.getDays().get(i);
                    var currentDayActivities = currentDay.getActivities();
                    if (isNull(currentDayActivities) || currentDayActivities.isEmpty()) {
                        currentDayActivities = newArrayList();
                    }

                    val newActivities = trip.getDays().get(i).getActivities();
                    newActivities.forEach(newActivity -> newActivity.setTripDay(currentDay));
                    currentDayActivities.addAll(newActivities);
                    currentDay.setActivities(currentDayActivities);
                }
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

    @PostMapping("/magic")
    public GenericResponse<Trip> getMagicTrip(@RequestBody Trip trip) {
        return GenericResponse.success(updateTripWithMagicData(trip));
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
        val tripLength = trip.getTripLength();

        val days = new ArrayList<TripDay>(tripLength);
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
        val activities = Arrays.stream(EventType.values()).parallel()
                .map(eventType -> placeEventService.getEventByTypeAndCity(eventType, trip.getDestination()))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Collections.shuffle(activities);

        val tripLength = trip.getTripLength();
        val maxActivitiesPerDay = Math.min(activities.size() / tripLength + 1, 5);
        val days = new ArrayList<TripDay>();
        for (long i = 0; i < tripLength; ++i) {
            val day = new TripDay();
            day.setOrderInTrip(i + 1);
            day.setTrip(trip);

            val activitiesInDay = Lists.<TripActivity>newArrayList();

            // Merge existing activities with new ones
            val existingActivities = trip.getDays().get((int) i).getActivities();
            if (nonNull(existingActivities) && !existingActivities.isEmpty()) {
                activitiesInDay.addAll(existingActivities);
            }

            for (long j = i * maxActivitiesPerDay; j < (i + 1) * maxActivitiesPerDay && j < activities.size(); j++) {
                val activity = activities.get((int) j);

                val tripStartDateInMilliseconds = trip.getStartDate().getTime();
                val startTime = activity.getStartTime();
                if (nonNull(startTime)) {
                    activity.setStartTime(new Date(tripStartDateInMilliseconds + i * SECONDS_IN_DAY + startTime.getTime()));
                } else {
                    activity.setStartTime(new Date(tripStartDateInMilliseconds + i * SECONDS_IN_DAY));
                }

                val endTime = activity.getEndTime();
                if (nonNull(endTime)) {
                    val endDay = endTime.compareTo(startTime) < 0
                            ? i + 1
                            : i;
                    activity.setEndTime(new Date(tripStartDateInMilliseconds + endDay * SECONDS_IN_DAY + endTime.getTime()));
                } else {
                    activity.setEndTime(new Date(tripStartDateInMilliseconds + i * SECONDS_IN_DAY - 1));
                }

                activity.setTripDay(day);
                activitiesInDay.add(activity);
            }
            activitiesInDay.sort(Comparator.comparing(TripActivity::getStartTime)
                    .thenComparing(TripActivity::getEndTime));
            day.setActivities(activitiesInDay);
            days.add(day);
        }

        trip.setDays(days);

        return trip;
    }
}
