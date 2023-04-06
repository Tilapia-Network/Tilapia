package me.fan87.plugindevkit.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager<T> {

    private final Class<T> configClass;
    @Getter
    private final File configFile;
    private final Gson gson;
    private final T defaultConfig;

    private final Map<String, Object> defaultValues = new HashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<Field, String> reversedFieldMap = new HashMap<>();

    @Getter
    private T config;

    public ConfigManager(Class<T> configClass, T defaultConfig, File configFile, Gson gson) {
        this.configClass = configClass;
        this.configFile = configFile;
        this.gson = gson;
        this.defaultConfig = defaultConfig;
        loadConfig();
    }

    @SneakyThrows
    public void loadConfig() {
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();

        this.config = gson.fromJson(new FileReader(configFile), configClass);
        if (this.config == null) {
            this.config = defaultConfig;
            saveConfig();
        }
        saveConfig();
    }

    @SneakyThrows
    public void saveConfig() {
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(configFile);
        outputStream.write(gson.toJson(this.config).getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }


    @SneakyThrows
    private void visitClass(Class<?> clazz, String prefix) {
        prefix = (prefix.equals("") ? "" : prefix + ".");
        for (Field declaredField : clazz.getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers)) {
                String name = prefix + declaredField.getName();
                defaultValues.put(name, declaredField.get(null));
                fields.put(name, declaredField);
                reversedFieldMap.put(declaredField, name);
            }
        }
        for (Class<?> aClass : clazz.getClasses()) {
            int modifiers = aClass.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                visitClass(aClass, prefix + aClass.getSimpleName());
            }
        }
    }

}
