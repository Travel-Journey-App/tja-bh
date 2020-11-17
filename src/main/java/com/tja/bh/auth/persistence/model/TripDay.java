package com.tja.bh.auth.persistence.model;

import lombok.Data;

import java.util.List;

@Data
public class TripDay {
    Long id;
    Long order;
    List<TripActivity> activities;
}
