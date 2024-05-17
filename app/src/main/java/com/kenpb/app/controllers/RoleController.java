package com.kenpb.app.controllers;

import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.AuthenticationRequest;
import com.kenpb.app.dtos.AuthenticationResponse;
import com.kenpb.app.dtos.RoleDTO;
import com.kenpb.app.service.AuthenticationService;
import com.kenpb.app.service.RoleService;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;
    private final AuthenticationService authenticationService;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getByRoleId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        ApiResponse response = roleService.createRole(roleDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }







}
