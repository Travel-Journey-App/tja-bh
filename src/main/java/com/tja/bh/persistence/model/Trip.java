package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @EqualsAndHashCode.Include
    private String destination;

    @EqualsAndHashCode.Include
    private Date startDate;

    @EqualsAndHashCode.Include
    private Date endDate;

    private String cover;

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripDay> days;

    @JsonIgnore
    @ManyToOne
    private User user;

    @PreRemove
    private void tearDown() {
        if (isNull(user)) {
            return;
        }

        user.getTrips().remove(this);
    }

    @JsonIgnore
    public int getTripLength() {
        val tripDuration = Duration.between(
                new Timestamp(getStartDate().getTime()).toLocalDateTime(),
                new Timestamp(getEndDate().getTime()).toLocalDateTime());

        return (int) tripDuration.toDays() + 1;
    }
}
