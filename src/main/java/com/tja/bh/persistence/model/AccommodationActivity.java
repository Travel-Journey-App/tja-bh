package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@JsonTypeName("ACCOMMODATION")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accommodation_activity")
public class AccommodationActivity extends TripActivity {
    @Builder.Default
    Direction direction = Direction.CHECK_IN;

    public AccommodationActivity() {
        super(ActivityType.ACCOMMODATION);
    }

    public enum Direction {
        @JsonProperty("check-in")
        CHECK_IN,

        @JsonProperty("check-out")
        CHECK_OUT
    }
}
