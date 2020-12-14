package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.model.enumeration.TransferType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Table;
@JsonTypeName("TRANSFER")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "transfer_activity")
public class TransferActivity extends TripActivity {
    TransferType transferType;
    Direction direction;
    String voyageNumber;
    String seatNumber;

    public TransferActivity() {
        super(ActivityType.TRANSFER);
    }

    public enum Direction {
        @JsonProperty("arrival")
        ARRIVAL,

        @JsonProperty("departure")
        DEPARTURE
    }
}
