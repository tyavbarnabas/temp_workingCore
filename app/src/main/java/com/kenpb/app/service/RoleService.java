package com.kenpb.app.service;

import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.RoleDTO;

public interface RoleService {

    ApiResponse getByRoleId(Long id);

    ApiResponse getAllRoles();

    ApiResponse createRole(RoleDTO roleDTO);

    ApiResponse updateRole(RoleDTO role, Long id);

    ApiResponse deleteRole(Long id);


}
