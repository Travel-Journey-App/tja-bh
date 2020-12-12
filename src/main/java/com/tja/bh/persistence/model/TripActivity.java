package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tja.bh.persistence.model.enumeration.ActivityType;
import lombok.*;

import javax.persistence.*;

import static java.util.Objects.isNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "activityType")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = EventActivity.class, name = "EVENT"),
                @JsonSubTypes.Type(value = TransferActivity.class, name = "TRANSFER"),
                @JsonSubTypes.Type(value = AccommodationActivity.class, name = "ACCOMMODATION")
        })
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TripActivity {
    @NonNull
    @JsonIgnore
    @EqualsAndHashCode.Include
    ActivityType activityType;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tripday_id")
    private TripDay tripDay;
}
