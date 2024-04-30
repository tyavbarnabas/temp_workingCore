package com.kenpb.app.dtos;

import com.kenpb.app.models.Menu;
import com.kenpb.app.models.Permission;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    @NotBlank(message = "name is mandatory")
    private String name;
    private String code;
    private String archived;
    private String description;
    private Set<Permission> permission;
    private Set<Menu> menu;
}
