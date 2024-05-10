package com.kenpb.app.serviceImplementation;


import com.kenpb.app.constants.GeneralResponseEnum;
import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.RoleDTO;
import com.kenpb.app.role.Role;
import com.kenpb.app.repositories.RoleRepository;
import com.kenpb.app.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;
    @Override
    public ApiResponse getByRoleId(Long id) {

        // Retrieve the role from the database
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role with id " + id + " not found"));

        // Convert Role entity to RoleDTO
        RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);

        // Return ApiResponse
        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(roleDTO)
                .build();

    }

    @Override
    public ApiResponse getAllRoles() {
        // Retrieve all roles from the database
        List<Role> roles = roleRepository.findAll();

        // Check if there are no roles
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("No roles found");
        }

        // Convert each Role entity to RoleDTO
        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toList());

        // Return ApiResponse
        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(roleDTOs)
                .build();
    }

    @Override
    public ApiResponse createRole(RoleDTO roleDTO) {

        Optional<Role> existingRole = roleRepository.findByName(roleDTO.getName());
        if (existingRole.isPresent()) {
            throw new EntityNotFoundException("Role already exists");
        }


        // Convert RoleDTO to Role entity
        Role role = modelMapper.map(roleDTO, Role.class);

        if(StringUtils.isBlank(role.getCode())) {
            role.setCode(UUID.randomUUID().toString());
        }


        Role savedRole = roleRepository.save(role);
        log.info("Role saved successfully {}", savedRole);

        // Convert saved Role entity back to RoleDTO
        RoleDTO savedRoleDTO = modelMapper.map(savedRole, RoleDTO.class);

        // Return ApiResponse
        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.CREATED.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(savedRoleDTO)
                .build();

    }

    @Override
    public ApiResponse updateRole(RoleDTO roleDTO, Long id) {

        // Retrieve the role from the database
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role with id " + id + " not found"));

        // Update the role with the data from the RoleDTO
        role.setName(roleDTO.getName());
        role.setCode(roleDTO.getCode());
        role.setDateCreated(LocalDateTime.now());
        role.setDateModified(LocalDateTime.now());

        // Save the updated role back to the database
        Role updatedRole = roleRepository.save(role);

        // Convert updated Role entity to RoleDTO
        RoleDTO updatedRoleDTO = modelMapper.map(updatedRole, RoleDTO.class);

        // Return ApiResponse
        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(updatedRoleDTO)
                .build();
    }

    @Override
    public ApiResponse deleteRole(Long id) {
        return null;
    }
}
