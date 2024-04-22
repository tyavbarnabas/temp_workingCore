package com.kenpb.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
public class AppController {

    @Autowired(required = false)
    private PluginManager pluginManager;

    @GetMapping
    public List<String> list() {
        return pluginManager.getResolvedPlugins().stream()
                .map(PluginWrapper::getPluginId).collect(Collectors.toList());
    }

    @GetMapping("/{pluginId}/stop")
    public Object stop(@PathVariable String pluginId) {
        PluginState pluginState = pluginManager.stopPlugin(pluginId);
        return Collections.singletonMap("state", pluginState);
    }

    @GetMapping("/{pluginId}/start")
    public Object start(@PathVariable String pluginId) {
        PluginState pluginState = pluginManager.startPlugin(pluginId);
        return Collections.singletonMap("state", pluginState);
    }


    @GetMapping("/{pluginId}/unload")
    public Object update(@PathVariable String pluginId) {
        pluginManager.unloadPlugin(pluginId);
        return Collections.singletonMap("state", pluginManager.getPlugin(pluginId).getPluginState());
    }

    @GetMapping("/{pluginId}/delete")
    public Object delete(@PathVariable String pluginId) {
        pluginManager.deletePlugin(pluginId);
        return Collections.singletonMap("state", pluginManager.getPlugin(pluginId).getPluginState());
    }

    @PostMapping(value = "${spring.sbp.controller.base-path:/sbp}/upload")
    public Object upload(@RequestParam("file") MultipartFile file) {
        try {
            // Ensure the 'plugins' directory exists
            Path pluginsDir = Paths.get("plugins");
            Files.createDirectories(pluginsDir);

            // Resolve the file name in the 'plugins' directory
            Path destinationFile = pluginsDir.resolve(file.getOriginalFilename());

            // Copy the file to the 'plugins' directory
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Load and start the plugins
            pluginManager.loadPlugins();
            pluginManager.startPlugins();

            return Collections.singletonMap("state", "success");
        } catch (IOException e) {
            return Collections.singletonMap("state", "error");
        }


    }
    @PostMapping("/{pluginId}/update")
    public Object update(@PathVariable String pluginId, @RequestParam("file") MultipartFile file) {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            return Collections.singletonMap("state", "Plugin does not exist");
        }

        try {
            // Unload the existing plugin
            pluginManager.unloadPlugin(pluginId);

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

            return Collections.singletonMap("state", "success");
        } catch (IOException e) {
            return Collections.singletonMap("state", "error");
        }
    }
}
