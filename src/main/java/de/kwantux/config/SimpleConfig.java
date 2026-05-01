package de.kwantux.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Simple configuration system with commented defaults
 */
public class SimpleConfig {
    
    private final JavaPlugin plugin;
    private final String fileName;
    private final Path filePath;
    private final Logger logger;
    private final Map<String, Object> defaultValues = new HashMap<>();
    private final Map<String, String> comments = new HashMap<>();
    private final Map<String, Object> activeValues = new HashMap<>();
    
    public SimpleConfig(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".conf") ? fileName : fileName + ".conf";
        this.filePath = plugin.getDataFolder().toPath().resolve(this.fileName);
        this.logger = plugin.getLogger();
        
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        loadConfig();
    }
    
    /**
     * Define a default value with a comment
     */
    public void defineDefault(String key, Object defaultValue, String comment) {
        defaultValues.put(key, defaultValue);
        comments.put(key, comment);
    }
    
    /**
     * Define a default value without a comment
     */
    public void defineDefault(String key, Object defaultValue) {
        defineDefault(key, defaultValue, null);
    }
    
    /**
     * Get a value from config, falling back to default if not set
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        if (activeValues.containsKey(key)) {
            return (T) activeValues.get(key);
        }
        if (defaultValues.containsKey(key)) {
            return (T) defaultValues.get(key);
        }
        throw new IllegalArgumentException("No default value defined for key: " + key);
    }
    
    /**
     * Get a string value
     */
    public String getString(String key) {
        return get(key, String.class);
    }
    
    /**
     * Get an integer value
     */
    public int getInt(String key) {
        return get(key, Integer.class);
    }
    
    /**
     * Get a boolean value
     */
    public boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }
    
    /**
     * Get a double value
     */
    public double getDouble(String key) {
        return get(key, Double.class);
    }

    /**
     * Get a string array
     */
    public String[] getStringArray(String key) {
        return get(key, String[].class);
    }

    /**
     * Get an integer array
     */
    public Integer[] getIntArray(String key) {
        return get(key, Integer[].class);
    }
    
    /**
     * Set a value in the active configuration
     */
    public void set(String key, Object value) {
        activeValues.put(key, value);
        saveConfig();
    }
    
    /**
     * Check if a key has an active value (not commented out)
     */
    public boolean isActive(String key) {
        return activeValues.containsKey(key);
    }
    
    /**
     * Load configuration from file
     */
    private void loadConfig() {
        if (!Files.exists(filePath)) {
            createDefaultConfig();
            return;
        }
        
        try {
            activeValues.clear();
            Files.lines(filePath).forEach(line -> {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    return; // Skip comments and empty lines
                }
                
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String valueStr = line.substring(equalIndex + 1).trim();
                    
                    // Parse the value
                    Object value = parseValue(valueStr);
                    activeValues.put(key, value);
                }
            });
        } catch (IOException e) {
            logger.warning("Failed to load config file " + fileName + ": " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    /**
     * Parse a string value to the appropriate type
     */
    private Object parseValue(String valueStr) {
        valueStr = valueStr.trim();
        
        // Handle arrays
        if (valueStr.startsWith("[") && valueStr.endsWith("]")) {
            String content = valueStr.substring(1, valueStr.length() - 1).trim();
            if (content.isEmpty()) {
                return new String[0];
            }
            String[] parts = content.split(",");
            String[] result = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = parts[i].trim().replaceAll("^\"|\"$", "");
            }
            return result;
        }
        
        // Handle quoted strings
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            return valueStr.substring(1, valueStr.length() - 1);
        }
        
        // Handle booleans
        if (valueStr.equalsIgnoreCase("true")) return true;
        if (valueStr.equalsIgnoreCase("false")) return false;
        
        // Handle integers
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException ignored) {}
        
        // Handle doubles
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException ignored) {}
        
        // Default to string
        return valueStr;
    }
    
    /**
     * Create the default configuration file with all options commented out
     */
    private void createDefaultConfig() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("# Configuration file for " + plugin.getName());
            writer.newLine();
            writer.write("# Uncomment lines to override default values");
            writer.newLine();
            writer.newLine();
            
            for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
                String key = entry.getKey();
                Object defaultValue = entry.getValue();
                String comment = comments.get(key);
                
                if (comment != null) {
                    writer.write("# " + comment);
                    writer.newLine();
                }
                writer.write("# " + key + " = " + formatValue(defaultValue));
                writer.newLine();
                writer.newLine();
            }
            
        } catch (IOException e) {
            logger.severe("Failed to create default config file " + fileName + ": " + e.getMessage());
        }
    }
    
    /**
     * Save the current configuration, preserving comments and resetting them to defaults
     */
    public void saveConfig() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("# Configuration file for " + plugin.getName());
            writer.newLine();
            writer.write("# Uncomment lines to override default values");
            writer.newLine();
            writer.newLine();
            
            // Write all default values as comments
            for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
                String key = entry.getKey();
                Object defaultValue = entry.getValue();
                String comment = comments.get(key);
                
                if (comment != null) {
                    writer.write("# " + comment);
                    writer.newLine();
                }
                
                // Write as active value if it's set, otherwise as comment
                if (activeValues.containsKey(key)) {
                    writer.write(key + " = " + formatValue(activeValues.get(key)));
                } else {
                    writer.write("# " + key + " = " + formatValue(defaultValue));
                }
                writer.newLine();
                writer.newLine();
            }
            
        } catch (IOException e) {
            logger.severe("Failed to save config file " + fileName + ": " + e.getMessage());
        }
    }
    
    /**
     * Format a value for writing to the config file
     */
    private String formatValue(Object value) {
        if (value instanceof String[]) {
            String[] array = (String[]) value;
            if (array.length == 0) return "[]";
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < array.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(array[i]).append("\"");
            }
            sb.append("]");
            return sb.toString();
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }
    
    /**
     * Reload the configuration from file
     */
    public void reload() {
        loadConfig();
    }
    
    /**
     * Get the file name
     */
    public String getFileName() {
        return fileName;
    }
}
