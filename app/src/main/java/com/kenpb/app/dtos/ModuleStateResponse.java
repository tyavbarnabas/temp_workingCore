package com.kenpb.app.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ModuleStateResponse {

    private String statusCode;
    private String message;
    private String pluginId;
    private String state;
}
