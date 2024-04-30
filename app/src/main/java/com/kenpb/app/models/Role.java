package com.kenpb.app.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "base-role")
public class Role {
    @SequenceGenerator(name = "role_id", sequenceName = "role_id", allocationSize = 1)
    @GeneratedValue(generator = "role_id", strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    private String name;
    private String code;
    private LocalDateTime dateModified;
    private LocalDateTime dateCreated;
    private String archived;
    private String description;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Permission> permission;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Menu> menu;

}
