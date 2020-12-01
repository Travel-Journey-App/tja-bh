package com.tja.bh.persistence.model;

import com.tja.bh.persistence.model.enumeration.ActivityType;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TripActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    Long id;

    @NonNull
    ActivityType activityType;

    @ManyToOne
    @JoinColumn(name = "tripday_id")
    private TripDay tripDay;
}
