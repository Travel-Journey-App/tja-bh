package com.tja.bh.auth.persistence.model;

import com.tja.bh.auth.persistence.model.enumeration.ActivityType;
import lombok.*;
import org.joda.time.DateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class AccommodationActivity extends TripActivity {
    String location;
    String address;
    DateTime dateTime;
    @Builder.Default
    Direction accommodationDirection = Direction.CHECK_IN;

    public AccommodationActivity() {
        super(ActivityType.ACCOMMODATION);
    }

    public enum Direction {
        CHECK_IN,
        CHECK_OUT
    }
}
