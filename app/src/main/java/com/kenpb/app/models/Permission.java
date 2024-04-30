package com.kenpb.app.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "base_permission")
public class Permission {
    @SequenceGenerator(name = "permission_id", sequenceName = "permission_id", allocationSize = 1)
    @GeneratedValue(generator = "permission_id", strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @NotNull
    private String name;

    private String description;

    @JsonIgnore
    private int archived=0;

    @Column(name = "module_name")
    private String moduleName;

}
