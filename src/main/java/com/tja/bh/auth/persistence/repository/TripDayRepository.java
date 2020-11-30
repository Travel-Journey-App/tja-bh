package com.tja.bh.auth.persistence.repository;

import com.tja.bh.auth.persistence.model.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
}
