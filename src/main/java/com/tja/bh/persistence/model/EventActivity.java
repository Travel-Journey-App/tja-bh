package com.tja.bh.persistence.model;


import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.persistence.model.enumeration.EventType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@JsonTypeName("EVENT")
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_activity")
public class EventActivity extends TripActivity {

    private EventType eventType;

    public EventActivity() {
        super(ActivityType.EVENT);
    }
}
