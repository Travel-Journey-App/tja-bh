package com.tja.bh.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(Long id) {
        this(id, null);
    }

    public Role(Long id, String name) {
        this(id, name, null);
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
