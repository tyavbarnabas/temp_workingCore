package com.kenpb.app.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApiResponse {

    private String statusCode;
    private String message;
    private Object data;
}
