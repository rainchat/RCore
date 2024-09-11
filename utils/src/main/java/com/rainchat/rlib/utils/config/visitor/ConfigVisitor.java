package com.rainchat.rlib.utils.config.visitor;

import com.rainchat.rlib.utils.config.visitor.annotation.ConfigComment;
import com.rainchat.rlib.utils.config.visitor.annotation.IgnoreField;
import com.rainchat.rlib.utils.config.visitor.annotation.Selection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigVisitor {

    private final JavaPlugin plugin;
    private final List<Object> loadedConfigs = new ArrayList<>();
    private Map<String, FileConfiguration> configs = new HashMap<>();

    public ConfigVisitor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(Object configObject) {
        Selection selection = configObject.getClass().getAnnotation(Selection.class);

        if (selection != null) {
            loadConfig(configObject, selection);
        }
    }

    private void loadConfig(Object configObject, Selection selection) {
        Class<?> clazz = configObject.getClass();
        String fileName = selection.fileName();
        String basePath = selection.path();

        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            try (InputStream inputStream = plugin.getResource(fileName)) {
                if (inputStream!= null) {
                    Files.copy(inputStream, configFile.toPath());
                } else {
                    if (!configFile.getParentFile().exists() &&!configFile.getParentFile().mkdirs()) {
                        throw new IOException("Failed to create directory for configuration file");
                    }
                    if (!configFile.createNewFile()) {
                        throw new IOException("Failed to create configuration file");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error creating configuration file: " + e.getMessage());
            }
        }

        FileConfiguration config;
        if (configs.containsKey(fileName)) {
            config = configs.get(fileName);
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
            configs.put(fileName, config);
        }

        List<Field> fieldsToSave = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(IgnoreField.class)) {
                continue;
            }
            field.setAccessible(true);
            String path = basePath.isEmpty()? field.getName() : basePath + "." + field.getName();

            try {
                ConfigComment commentAnnotation = field.getAnnotation(ConfigComment.class);
                if (commentAnnotation!= null) {
                    config.setComments(path, List.of(commentAnnotation.value()));
                }

                Object fieldValue = field.get(configObject);

                Selection fieldSelection = field.getAnnotation(Selection.class);
                if (fieldSelection!= null) {
                    if (fieldValue == null) {
                        fieldValue = field.getType().newInstance();
                    }
                    loadConfig(fieldValue, fieldSelection);
                    field.set(configObject, fieldValue);
                } else {
                    Object value = config.get(path);
                    if (value!= null) {
                        if (field.getType().isAssignableFrom(List.class)) {
                            field.set(configObject, config.getList(path));
                        } else if (field.getType().isAssignableFrom(Map.class)) {
                            field.set(configObject, config.getConfigurationSection(path).getValues(false));
                        } else {
                            field.set(configObject, value);
                        }
                    } else {
                        config.set(path, field.get(configObject));
                    }
                    fieldsToSave.add(field);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        // Сохраняем конфигурацию только для полей, у которых нет аннотации @Selection
        for (Field field : fieldsToSave) {
            String path = basePath.isEmpty()? field.getName() : basePath + "." + field.getName();
            try {
                config.set(path, field.get(configObject));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadedConfigs.add(configObject);
    }

    public void reloadConfigs() {
        List<Object> configsToReload = new ArrayList<>(loadedConfigs);
        for (Object configObject : configsToReload) {
            loadConfig(configObject);
        }
    }
}
