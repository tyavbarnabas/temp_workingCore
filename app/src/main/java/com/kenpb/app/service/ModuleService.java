package com.kenpb.app.service;

import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.ModuleStateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ModuleService {

    ApiResponse uninstall(String pluginId);

    ApiResponse uploadModule(MultipartFile file);

    ApiResponse updateModule(String pluginId, MultipartFile file);

    ApiResponse deleteModule(String pluginId);

    ApiResponse stopModule(String pluginId);

    ApiResponse startModule(String pluginId);

    ApiResponse getListOfModules();

    ModuleStateResponse getModuleState(String pluginId);

    ApiResponse getModule(String pluginId);
}
