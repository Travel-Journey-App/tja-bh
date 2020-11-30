package com.tja.bh.auth.persistence.model;

import com.tja.bh.auth.persistence.model.enumeration.ActivityType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trip_activity")
public abstract class TripActivity {
    @Id
    Long id;

    @NonNull
    ActivityType activityType;
}
