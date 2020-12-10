package com.tja.bh.persistence.model;

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
    private Long order;

    @ManyToOne
    private Trip trip;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tripDay")
    private List<TripActivity> activities;
}
