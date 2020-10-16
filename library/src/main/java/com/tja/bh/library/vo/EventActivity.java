package com.tja.bh.library.vo;

import com.tja.bh.library.enumeration.ActivityType;
import com.tja.bh.library.enumeration.PriceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
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
