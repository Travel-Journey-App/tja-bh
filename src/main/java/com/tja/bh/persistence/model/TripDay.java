package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trip_days")
public class TripDay {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    private Long orderInTrip;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Trip trip;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tripDay", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Include
    private List<TripActivity> activities;
}
