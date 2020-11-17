package com.tja.bh.auth.persistence.model;

import com.tja.bh.auth.persistence.model.enumeration.ActivityType;
import lombok.*;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class TripActivity {
    Long id;
    @NonNull
    ActivityType activityType;
}
