package com.tja.bh.persistence.repository;

import com.tja.bh.persistence.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
