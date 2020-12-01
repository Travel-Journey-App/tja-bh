package com.tja.bh.persistence.model;


import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.model.enumeration.PriceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "event_activity")
public class EventActivity extends TripActivity {
    String location;
    String address;
    DateTime dateTime;
    Double longitude;
    Double latitude;
    PriceType price;
    Double rating;

    public EventActivity() {
        super(ActivityType.EVENT);
    }
}
