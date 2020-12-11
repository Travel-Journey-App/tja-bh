package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "trip_days")
public class TripDay {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderInTrip;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Trip trip;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tripDay")
    private List<TripActivity> activities;
}
