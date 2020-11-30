package com.tja.bh.auth.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    String name;

    @NonNull
    String destination;

    String cover;

    @NonNull
    Date startDate;

    @NonNull
    Date endDate;

    @OneToMany
    List<TripDay> days;

    public Trip() {
        super();
    }
}
