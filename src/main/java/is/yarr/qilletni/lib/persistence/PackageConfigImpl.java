package is.yarr.qilletni.lib.persistence;

import is.yarr.qilletni.api.lib.persistence.PackageConfig;
import is.yarr.qilletni.lang.exceptions.config.ConfigLoadException;
import is.yarr.qilletni.lang.exceptions.config.ConfigValueDoesNotExistException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PackageConfigImpl implements PackageConfig {
    
    private final String comment;
    private final String packageName;
    private final Path configPath;
    private final Properties properties;
    
    private boolean loaded = false;

    public PackageConfigImpl(String packageName, String comment, Path configPath) {
        this.packageName = packageName;
        this.configPath = configPath;
        this.properties = new Properties();
        this.comment = comment;
    }
    
    /**
     * Gets the directory where all package configs are stored.
     * 
     * @return The directory path
     */
    private static Path getPersistenceDirectory() {
        try {
            var userHome = System.getProperty("user.home");
            return Files.createDirectories(Paths.get(userHome, ".qilletni", "persistence"));
        } catch (IOException e) {
            throw new ConfigLoadException("Unable to create persistence directory", e);
        }
    }

    /**
     * Creates a new package config for the given package name.
     * 
     * @param packageName The name of the package
     * @return The created {@link PackageConfig}
     */
    public static PackageConfig createPackageConfig(String packageName) {
        var propertiesFile = getPersistenceDirectory().resolve("%s.properties".formatted(packageName));
        return new PackageConfigImpl(packageName, "Properties for the package '%s'".formatted(packageName), propertiesFile);
    }

    /**
     * Creates the config storing internal data.
     * 
     * @return The created {@link PackageConfig}
     */
    public static PackageConfig createInternalConfig() {
        var propertiesFile = getPersistenceDirectory().resolve(".internal.properties");
        return new PackageConfigImpl(".internal", "Properties for the Qilletni language", propertiesFile);
    }

    @Override
    public void loadConfig() {
        if (loaded) {
            return;
        }
        
        loaded = true;
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        properties.clear();

        if (Files.notExists(configPath)) {
            return;
        }

        try (var input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (Exception e) {
            throw new ConfigLoadException("Unable to load package config file: %s".formatted(configPath), e);
        }
    }

    @Override
    public void saveConfig() {
        try (var output = Files.newOutputStream(configPath)) {
            properties.store(output, comment);
        } catch (Exception e) {
            throw new ConfigLoadException("Unable to save package config file: %s".formatted(configPath), e);
        }
    }

    @Override
    public Optional<String> get(String key) {
        loadConfig();

        return Optional.ofNullable(properties.getProperty(key));
    }

    @Override
    public String getOrThrow(String key) {
        loadConfig();

        return get(key).orElseThrow(() -> new ConfigValueDoesNotExistException("Config value does not exist: %s  for package: %s".formatted(key, packageName)));
    }

    @Override
    public void set(String key, String value) {
        loadConfig();

        properties.setProperty(key, value);
    }

    @Override
    public void remove(String key) {
        loadConfig();

        properties.remove(key);
    }

    @Override
    public Map<String, String> getAll() {
        loadConfig();

        return properties.entrySet().stream()
                .collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
    }

    @Override
    public String toString() {
        return "PackageConfigImpl{configPath=%s, properties=%s}".formatted(configPath, properties);
    }
}
