package com.tja.bh.persistence.repository;

import com.tja.bh.persistence.model.TripActivity;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripActivityRepository extends JpaRepository<TripActivity, Long> {
    List<TripActivity> findAllByActivityType(ActivityType activityType);
}
