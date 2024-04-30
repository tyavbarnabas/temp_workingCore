package com.kenpb.app.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ModulePropertiesDto {

    private String moduleId;
    private String moduleName;
    private String state;
    private String version;
}
