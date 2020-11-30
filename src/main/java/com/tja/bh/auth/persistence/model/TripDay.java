package com.tja.bh.auth.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Getter
@NoArgsConstructor
@Table(name = "trip_day")
public class TripDay {
    @Id
    Long id;

    Long order;

    @OneToMany
    List<TripActivity> activities;
}
