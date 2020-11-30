package com.tja.bh.auth.persistence.repository;

import com.tja.bh.auth.persistence.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
}