package com.kenpb.app.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthenticationResponse {
    private String token;
}
