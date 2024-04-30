package com.kenpb.app.serviceImplementation;

import com.kenpb.app.constants.GeneralResponseEnum;
import com.kenpb.app.dtos.ModulePropertiesDto;
import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.ModuleStateResponse;
import com.kenpb.app.exceptions.*;
import com.kenpb.app.service.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    @Autowired(required = false)
    private PluginManager pluginManager;

    @Override
    public ApiResponse uninstall(String pluginId) {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            throw new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + pluginId + " does not exist");
        }

        try {
            // Validate plugin state
            if (pluginWrapper.getPluginState() != PluginState.STARTED) {
                throw new ModuleExceptionHandler.ModuleNotLoadedException("Module is not loaded");
            }

            // Check for dependencies or potential issues with uninstalling the plugin
            // if (isUninstallingPluginSafe(pluginId)) { return ResponseEntity.badRequest().body("Uninstalling the plugin may cause issues"); }
            // Unload the plugin
            pluginManager.unloadPlugin(pluginId);

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .data(pluginWrapper.getPluginState())
                    .build();

        } catch (Exception e) {
            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .data("Error unloading plugin: " + e.getMessage())
                    .build();
        }
    }

    public ApiResponse uploadModule(MultipartFile file) {
        try {

            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".jar")) {
                throw new ModuleExceptionHandler.InvalidFileTypeException("Invalid file type,File must be of type JAR");
            }
            // Ensure the 'plugins' directory exists
            Path pluginsDir = Paths.get("plugins");
            Files.createDirectories(pluginsDir);

            // Resolve the file name in the 'plugins' directory
            Path destinationFile = pluginsDir.resolve(file.getOriginalFilename());
            log.info("Destination file: {}", destinationFile);

            if (Files.exists(destinationFile)) {
                throw new ModuleExceptionHandler.JarAlreadyExitException("JAR file with the same name already exists");
            }
            long maxFileSize = 10000L * 1024 * 1024; // 10000MB in bytes
            if (file.getSize() > maxFileSize) {
                return ApiResponse.builder()
                        .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .message("File size too small. File must be above 10000MB")
                        .data(null)
                        .build();
            }

            // Copy the file to the 'plugins' directory
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Validate the content of the JAR file
            if (!containsNecessaryFiles(file)) {
                throw new ModuleExceptionHandler.InvalidModuleException("The uploaded JAR file is missing necessary files");
            }

            // Load and start the plugins
            pluginManager.loadPlugins();
            pluginManager.startPlugins();

            // Get the properties of the uploaded plugin
            List<ModulePropertiesDto> pluginProperties = getPluginProperties(pluginManager.getResolvedPlugins());
            log.info("Plugin properties: {}", pluginProperties);

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .data(pluginProperties)
                    .build();

        } catch (IOException e) {

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse updateModule(String pluginId, MultipartFile file) {
        // Validate plugin existence
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            throw new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + pluginId + " does not exist");
        }

        try {
            // Validate file type (JAR)
            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".jar")) {
                throw new ModuleExceptionHandler.InvalidFileTypeException("Invalid file type. File must be a JAR file");
            }

             //Validate file size
            long maxFileSize = 10000L * 1024 * 1024; // 10000MB in bytes
            if (file.getSize() > maxFileSize) {
                throw new ModuleExceptionHandler.FileSizeExceededModuleException("File size exceeds the maximum limit of 10000MB");
            }
            
            // Delete the existing plugin
            pluginManager.deletePlugin(pluginId);

            // Upload the new version of the plugin
            Path tempDir = Files.createTempDirectory("plugins");
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Load the new plugin
            pluginManager.loadPlugins();
            // Start the new plugin
            pluginManager.startPlugins();

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .build();

        } catch (IOException e) {

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .build();
        }
    }


    @Override
    public ApiResponse deleteModule(String moduleId) {
        // Validate plugin existence
        PluginWrapper pluginWrapper = pluginManager.getPlugin(moduleId);
        if (pluginWrapper == null) {
            throw  new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + moduleId + " does not exist");
        }

        try {
            // Check for dependencies or usage of the plugin
            if (isModuleInUse(moduleId)) {
                throw new ModuleExceptionHandler.ModuleInUseException("Module is in use and cannot be deleted");
            }
            // Delete the plugin
            pluginManager.deletePlugin(moduleId);

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .build();
        } catch (Exception e) {

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse stopModule(String moduleId) {

        PluginWrapper pluginWrapper = pluginManager.getPlugin(moduleId);
        if (pluginWrapper == null) {
            throw new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + moduleId + " does not exist");
        }

        try {
            // Validate plugin state
            if (pluginWrapper.getPluginState() != PluginState.STARTED) {
                throw new ModuleExceptionHandler.ModuleNotRunningException("Module is not running and cannot be stopped");
            }
            // Check for dependencies or potential issues with stopping the plugin
            // Example: if (isStoppingPluginSafe(moduleId)) { return "Stopping the plugin may cause issues"; }

            // Stop the plugin
            PluginState pluginState = pluginManager.stopPlugin(moduleId);
            log.info("Plugin stopped: {}", moduleId);

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .data(pluginState)
                    .build();

        } catch (Exception e) {

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .data("Error stopping plugin: " + e.getMessage())
                    .build();
        }

    }

    @Override
    public ApiResponse startModule(String pluginId) {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            throw new ModuleExceptionHandler.ModuleNotFoundException("module with ID " + pluginId + " does not exist");
        }

        try {
            // Validate plugin state
            if (pluginWrapper.getPluginState() != PluginState.STOPPED) {
                throw new ModuleExceptionHandler.ModuleAlreadyRunningModuleException("module is already running and cannot be started");
            }
            // Start the plugin
            PluginState pluginState = pluginManager.startPlugin(pluginId);
            log.info("Plugin started: {}", pluginId);

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.OK.value()))
                    .message(GeneralResponseEnum.SUCCESS.getMessage())
                    .data(pluginState)
                    .build();

        } catch (Exception e) {

            return ApiResponse.builder()
                    .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(GeneralResponseEnum.FAILED.getMessage())
                    .data("Error starting module: " + e.getMessage())
                    .build();
        }
    }


    private boolean containsNecessaryFiles(MultipartFile jarFile) {
        try (JarInputStream jarInputStream = new JarInputStream(jarFile.getInputStream())) {
            JarEntry entry;
            boolean hasPluginProperties = false;
            boolean hasJavaClasses = false;

            // Iterate over the entries in the JAR file
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                // Check if the entry is a file
                if (!entry.isDirectory()) {
                    // Check for plugin.properties file
                    if (entry.getName().equals("plugin.properties")) {
                        hasPluginProperties = true;
                    }
                    // Check for Java class files
                    if (entry.getName().endsWith(".class")) {
                        hasJavaClasses = true;
                    }
                }
            }

            // Check if both plugin.properties and Java classes are present
            return hasPluginProperties && hasJavaClasses;
        } catch (IOException e) {
            // Handle IO exception
            e.printStackTrace(); // or log the exception
        }
        return false;
    }


    private boolean isModuleInUse(String pluginId) {
        // Check if the plugin is in use by iterating over all loaded plugins
        List<PluginWrapper> loadedPlugins = pluginManager.getPlugins();
        for (PluginWrapper loadedPlugin : loadedPlugins) {

            // Skip the plugin being deleted
            if (loadedPlugin.getPluginId().equals(pluginId)) {
                continue;
            }
            // Check if the loaded plugin depends on the plugin being deleted
            PluginDescriptor pluginDescriptor = loadedPlugin.getDescriptor();
            if (pluginDescriptor.getDependencies().contains(pluginId)) {
                return true; // Plugin is in use
            }
        }
        return false; // Plugin is not in use
    }


    @Override
    public ApiResponse getListOfModules() {
        List<PluginWrapper> resolvedPlugins = pluginManager.getResolvedPlugins();
        log.info("Resolved plugins: {}", resolvedPlugins);

        // Check if there are no resolved plugins
        if (resolvedPlugins.isEmpty()) {
            throw new ModuleExceptionHandler.NoPluginsFoundException("No modules found");
        }

        // Extract plugin properties
        List<ModulePropertiesDto> pluginProperties = getPluginProperties(resolvedPlugins);
        log.info("Plugin properties: {}", pluginProperties);

        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(pluginProperties)
                .build();
    }

    @Override
    public ModuleStateResponse getModuleState(String pluginId) {
        // Retrieve the plugin wrapper using the plugin ID
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);

        // Check if the plugin exists
        if (pluginWrapper == null) {
            throw new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + pluginId + " does not exist");
        }

        // Get the state of the plugin from the plugin wrapper
        PluginState pluginState = pluginWrapper.getPluginState();
        log.info("Plugin state: {}", pluginState);

        // Get the description of the plugin state as a string
        String stateDescription = pluginState.toString();

        return ModuleStateResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .pluginId(pluginId)
                .state(stateDescription)
                .build();
    }


    @Override
    public ApiResponse getModule(String pluginId) {
        // Retrieve the plugin wrapper using the plugin ID
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        // Check if the plugin exists
        if (pluginWrapper == null) {
            // Throw an exception if the plugin is not found
            throw new ModuleExceptionHandler.ModuleNotFoundException("Module with ID " + pluginId + " does not exist");
        }

        // Extract relevant information about the plugin

        String pluginName = pluginWrapper.getDescriptor().getPluginId();
        PluginState pluginState = pluginWrapper.getPluginState();
        String stateDescription = pluginState.toString();
        String version = pluginWrapper.getDescriptor().getVersion();

        // Create a ModulePropertiesDto object with the plugin information
        ModulePropertiesDto modulePropertiesDto = ModulePropertiesDto.builder()
                .moduleId(pluginId)
                .moduleName(pluginName)
                .state(stateDescription)
                .version(version)
                .build();

        // Create and return a ModuleResponse object with the ModulePropertiesDto object
        return ApiResponse.builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .data(modulePropertiesDto)
                .build();
    }


    private List<ModulePropertiesDto> getPluginProperties(List<PluginWrapper> resolvedPlugins) {
        return resolvedPlugins.stream()
                .map(pluginWrapper -> {
                    String pluginId = pluginWrapper.getPluginId();
                    String pluginName = pluginWrapper.getDescriptor().getPluginId();
                    PluginState pluginState = pluginWrapper.getPluginState();
                    String stateDescription = pluginState.toString();
                    String version = pluginWrapper.getDescriptor().getVersion();

                    return ModulePropertiesDto.builder()
                            .moduleId(pluginId)
                            .moduleName(pluginName)
                            .state(stateDescription)
                            .version(version)
                            .build();
                })
                .collect(Collectors.toList());
    }




}
