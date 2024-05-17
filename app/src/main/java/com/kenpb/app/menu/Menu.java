package com.kenpb.app.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "base_menu")
public class Menu {

    @SequenceGenerator(name = "menu_id", sequenceName = "menu_id", allocationSize = 1)
    @GeneratedValue(generator = "menu_id", strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @NotNull
    private String name;

    private String state;
}
