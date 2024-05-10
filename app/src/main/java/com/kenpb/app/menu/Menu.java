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

//    @NotNull
//    private int archived=0;
//
//    private String type ="LINK";
//
//    @NotNull
//    private String level = "LEVEL_1";
//
//    @NotNull
//    private Integer position = 1;
//
//    private String icon;
//
//    @Column(name = "code")
//    private String code;
//
//    private String uuid;
//
//    private String tooltip;
//
//    private String breadcrumb;
//
//    private String url;
//
//    private Boolean disabled = false;

}
