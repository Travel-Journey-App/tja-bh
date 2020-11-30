package com.tja.bh.auth.persistence.repository;

import com.tja.bh.auth.persistence.model.TripActivity;
import com.tja.bh.auth.persistence.model.enumeration.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripActivityRepository extends JpaRepository<TripActivity, Long> {
    List<TripActivity> findAllByActivityType(ActivityType activityType);
}
