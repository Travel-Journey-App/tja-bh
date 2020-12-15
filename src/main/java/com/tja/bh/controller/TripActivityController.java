package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.Trip;
import com.tja.bh.persistence.model.TripActivity;
import com.tja.bh.persistence.model.TripDay;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.repository.TripActivityRepository;
import com.tja.bh.persistence.repository.TripDayRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.tja.bh.controller.TripController.MILLISECONDS_IN_DAY;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/activities", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class TripActivityController {

    private final TripController tripController;

    private final TripDayRepository tripDayRepository;

    private final TripActivityRepository activityRepository;

    @Autowired
    public TripActivityController(TripController tripController,
                                  TripDayRepository tripDayRepository,
                                  TripActivityRepository activityRepository) {
        this.tripController = tripController;
        this.tripDayRepository = tripDayRepository;
        this.activityRepository = activityRepository;
    }

    @GetMapping("/{activityId}")
    public GenericResponse<TripActivity> getTripActivity(@PathVariable("activityId") Long activityId) {
        val activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            return GenericResponse.success(activity.get());
        }

        return GenericResponse.error("No activity with id=%s found", activityId);
    }

    @GetMapping("")
    public GenericResponse<List<TripActivity>> getTripActivitiesByCategory(
            @RequestParam("activityType") @NonNull ActivityType type
    ) {
        return GenericResponse.success(activityRepository.findAllByActivityType(type));
    }

    @DeleteMapping("/{tripId}/{dayId}")
    public GenericResponse<Boolean> deleteTripActivity(@PathVariable("tripId") Long tripId,
                                                       @PathVariable("dayId") Long dayId,
                                                       @RequestBody @NonNull TripActivity activity) {
        try {
            val trip = tripController.getIfBelongsToUser(tripId);
            val optionalTripDay = trip.getDays().stream()
                    .filter(day -> dayId.equals(day.getId()))
                    .findFirst();
            if (optionalTripDay.isEmpty()) {
                return GenericResponse.error("Trip with id=%s do not have day with id=%s", tripId, dayId);
            }

            val activityId = activity.getId();
            val optionalActivity = activityRepository.findById(activityId);
            if (optionalActivity.isEmpty()) {
                return GenericResponse.error("No activity found with id=%s", activityId);
            }

            val existingActivity = optionalActivity.get();
            if (!tripId.equals(existingActivity.getTripDay().getTrip().getId())) {
                return GenericResponse.error("No activity with id=%s belongs to trip with id=%s",
                        activityId, tripId);
            }

            if (!dayId.equals(existingActivity.getTripDay().getId())) {
                return GenericResponse.error("No activity with id=%s belongs to day with id=%s",
                        activityId, dayId);
            }

            val dayInTrip = optionalTripDay.get();
            dayInTrip.getActivities().removeIf(activityInDay -> activityId.equals(activityInDay.getId()));
            tripDayRepository.saveAndFlush(dayInTrip);

            activityRepository.deleteById(activityId);

            return GenericResponse.success(true);
        } catch (Exception e) {
            return GenericResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{tripId}/{dayId}")
    public GenericResponse<TripActivity> createTripActivity(@PathVariable("tripId") Long tripId,
                                                            @PathVariable("dayId") Long dayId,
                                                            @RequestBody @NonNull TripActivity activity) {
        try {
            val trip = tripController.getIfBelongsToUser(tripId);
            val day = trip.getDays().stream()
                    .filter(dayInTrip -> dayId.equals(dayInTrip.getId()))
                    .findFirst();
            if (day.isEmpty()) {
                throw new RuntimeException("Can not obtain day with requested id");
            }

            activity.setTripDay(day.get());

            return GenericResponse.success(activityRepository.saveAndFlush(activity));
        } catch (Exception e) {
            return GenericResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{tripId}/{dayId}")
    public GenericResponse<TripActivity> updateTripActivity(@PathVariable("tripId") Long tripId,
                                                            @PathVariable("dayId") Long dayId,
                                                            @RequestBody @NonNull TripActivity activityToUpdate) {
        val activityId = activityToUpdate.getId();

        Trip trip;
        TripDay day;
        try {
            trip = tripController.getIfBelongsToUser(tripId);
            day = trip.getDays().stream()
                    .filter(dayInTrip -> dayId.equals(dayInTrip.getId()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);

        } catch (Exception e) {
            return GenericResponse.error("Error while updating activity=%s", activityId);
        }

        val optionalExistingActivity = activityRepository.findById(activityId);
        if (optionalExistingActivity.isEmpty()) {
            return GenericResponse.error("No activity with id=%s found", activityId);
        }

        val existingActivity = optionalExistingActivity.get();
        if (!day.getId().equals(existingActivity.getTripDay().getId())) {
            val oldDay = existingActivity.getTripDay();
            val oldDayActivities = oldDay.getActivities();
            oldDayActivities.removeIf(oldDayActivity -> activityId.equals(oldDayActivity.getId()));
            tripDayRepository.saveAndFlush(oldDay);
        }

        val targetDayActivities = day.getActivities();
        targetDayActivities.removeIf(targetDayActivity -> activityId.equals(targetDayActivity.getId()));
        targetDayActivities.add(activityToUpdate);
        activityToUpdate.setTripDay(day);
        val updatedDay = tripDayRepository.saveAndFlush(day);

        val savedActivity = updatedDay.getActivities().stream()
                .filter(activity -> activityId.equals(activity.getId()))
                .findFirst();
        return savedActivity.isPresent()
                ? GenericResponse.success(savedActivity.get())
                : GenericResponse.error("Something fucked up");
    }
}
