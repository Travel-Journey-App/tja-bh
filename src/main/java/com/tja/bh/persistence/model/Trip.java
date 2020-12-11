package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String destination;
    private Date startDate;
    private Date endDate;

    private String cover;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "trip")
    private List<TripDay> days;

    @JsonIgnore
    @ManyToOne
    private User user;
}
