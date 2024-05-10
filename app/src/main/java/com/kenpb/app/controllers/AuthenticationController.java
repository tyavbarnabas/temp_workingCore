package com.kenpb.app.controllers;

import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.AuthenticationRequest;
import com.kenpb.app.dtos.AuthenticationResponse;
import com.kenpb.app.dtos.RegistrationRequest;
import com.kenpb.app.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegistrationRequest request) {
        log.info("Request received: {}", request);
        return ResponseEntity.ok(authenticationService.register(request));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody  @Valid AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));

    }



}
