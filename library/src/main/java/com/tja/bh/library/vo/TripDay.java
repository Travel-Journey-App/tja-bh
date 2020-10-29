package com.tja.bh.library.vo;

import lombok.Data;

import java.util.List;

@Data
public class TripDay {
    Long id;
    Long order;
    List<TripActivity> activities;
}
