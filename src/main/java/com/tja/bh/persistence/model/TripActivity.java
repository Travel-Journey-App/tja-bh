package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import lombok.*;

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
    @EqualsAndHashCode.Include
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tripday_id")
    private TripDay tripDay;

    @EqualsAndHashCode.Include
    private String name;

    @Column(columnDefinition = "varchar(2000)")
    private String description;

    @EqualsAndHashCode.Include
    private Date startTime;

    @EqualsAndHashCode.Include
    private Date endTime;

    private String note;

    @EqualsAndHashCode.Include
    @JsonProperty("lat")
    private Double latitude;

    @EqualsAndHashCode.Include
    @JsonProperty("lon")
    private Double longitude;
}
