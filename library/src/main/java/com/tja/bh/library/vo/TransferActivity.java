package com.tja.bh.library.vo;

import com.tja.bh.library.enumeration.ActivityType;
import com.tja.bh.library.enumeration.TransferType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TransferActivity extends TripActivity {
    String location;
    DateTime time;
    TransferType transferType;
    Direction direction;
    String vehicleVoyageNumber;
    String seatNumber;

    public TransferActivity() {
        super(ActivityType.TRANSFER);
    }

    public enum Direction {
        ARRIVAL,
        DEPARTURE
    }
}
