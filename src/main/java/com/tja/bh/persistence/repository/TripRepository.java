package com.tja.bh.persistence.repository;

import com.tja.bh.persistence.model.Trip;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    default List<Trip> findAllSameTrips(Trip trip) {
        return findAll(Example.of(trip,
                ExampleMatcher.matchingAll()
                        .withMatcher("destination", ExampleMatcher.GenericPropertyMatchers.exact())
                        .withMatcher("startDate", ExampleMatcher.GenericPropertyMatchers.exact())
                        .withMatcher("endDate", ExampleMatcher.GenericPropertyMatchers.exact())));
    }
}
