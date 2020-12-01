package com.tja.bh.persistence.model;

import com.tja.bh.persistence.model.enumeration.ActivityType;
import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accommodation_activity")
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
