package dev.qilletni.api.lib.persistence;

import java.util.Map;
import java.util.Optional;

/**
 * A persistent configuration file for each package.
 */
public interface PackageConfig {

    /**
     * Load the configuration from the file system if it has not been read in yet.
     */
    void loadConfig();

    /**
     * Load the configuration from the file system, regardless of if it has been read in yet. This replaces all
     * previously read in values.
     */
    void reloadConfig();
    
    /**
     * Save the configuration to the file system.
     */
    void saveConfig();

    /**
     * Get a value from the configuration.
     * 
     * @param key The key to get the value for
     * @return The value for the key, if it exists
     */
    Optional<String> get(String key);
    
    /**
     * Get a value from the configuration, or throw an exception if it does not exist.
     * 
     * @param key The key to get the value for
     * @return The value for the key
     */
    String getOrThrow(String key);
    
    /**
     * Set a value in the configuration.
     * 
     * @param key The key to set the value for
     * @param value The value to set
     */
    void set(String key, String value);
    
    /**
     * Remove a value from the configuration.
     * 
     * @param key The key to remove the value for
     */
    void remove(String key);
    
    /**
     * Get all the values in the configuration.
     * 
     * @return A map of all the values in the configuration
     */
    Map<String, String> getAll();
    
}
