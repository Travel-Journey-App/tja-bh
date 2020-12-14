package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import com.tja.bh.util.JodaDateTimeDeserializer;
import com.tja.bh.util.JodaDateTimeSerializer;
import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "activityType")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = EventActivity.class, name = "event"),
                @JsonSubTypes.Type(value = TransferActivity.class, name = "transfer"),
                @JsonSubTypes.Type(value = AccommodationActivity.class, name = "accommodation")
        })
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TripActivity {
    @NonNull
    @EqualsAndHashCode.Include
    ActivityType activityType;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tripday_id")
    private TripDay tripDay;

    private String name;

    private String description;

    private Date startTime;

    private Date endTime;

    private String note;

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;
}
