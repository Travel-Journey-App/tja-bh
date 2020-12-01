package com.tja.bh.persistence.model;

import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.model.enumeration.TransferType;
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
@Table(name = "transfer_activity")
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
