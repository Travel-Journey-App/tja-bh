package com.tja.bh.persistence.repository;

import com.tja.bh.persistence.model.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
}
