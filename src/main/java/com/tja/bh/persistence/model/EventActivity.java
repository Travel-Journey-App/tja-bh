package com.tja.bh.persistence.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.model.enumeration.PriceType;
import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Table;

@JsonTypeName("EVENT")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_activity")
public class EventActivity extends TripActivity {

    public EventActivity() {
        super(ActivityType.EVENT);
    }
}
