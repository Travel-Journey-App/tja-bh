package com.tja.bh.library.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Trip {
    String name;
    String destination;
    Date startDate;
    Date endDate;
    List<TripDay> days;
}