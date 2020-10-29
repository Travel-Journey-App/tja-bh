package com.tja.bh.library.vo;

import com.tja.bh.library.enumeration.ActivityType;
import lombok.*;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class TripActivity {
    Long id;
    @NonNull
    ActivityType activityType;
}
