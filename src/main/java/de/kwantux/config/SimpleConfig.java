package de.kwantux.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static de.kwantux.networks.utils.DevelopmentUtils.devlog;

/**
 * Simple configuration system with commented defaults
 */
public class SimpleConfig {

    private final Map<String, Class> listValueTypes = new HashMap<>();

    private final JavaPlugin plugin;
    private final String fileName;
    private final Path filePath;
    private final Logger logger;
    private final Map<String, Object> defaultValues = new HashMap<>();
    private final Map<String, String> comments = new HashMap<>();
    private final Map<String, Object> activeValues = new HashMap<>();
    private final HoconConfigurationLoader loader;

    public SimpleConfig(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".conf") ? fileName : fileName + ".conf";
        this.filePath = plugin.getDataFolder().toPath().resolve(this.fileName);
        this.logger = plugin.getLogger();
        
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.loader = HoconConfigurationLoader.builder()
                .path(plugin.getDataFolder().toPath().resolve(fileName))
                .build();
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
        devlog("GET " + key + " AS " + type.getSimpleName() + " -> " + activeValues.get(key));
        try {
            if (activeValues.containsKey(key)) {
                return (T) activeValues.get(key);
            }
            if (defaultValues.containsKey(key)) {
                return (T) defaultValues.get(key);
            }
        } catch (Exception e) {
            logger.severe("Error getting value for key: " + key);
            logger.severe(e.getMessage());
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
     * Get an integer array
     */
    public Integer[] getIntArray(String key) {
        return get(key, Integer[].class);
    }

    /**
     * Get a string array
     */
    public String[] getStringArray(String key) {
        return get(key, String[].class);
    }
    
    /**
     * Set a value in the active configuration
     */
    public void set(String key, Object value) {
        activeValues.put(key, value);
        save();
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
    public void load_old() {
        if (!Files.exists(filePath)) {
            save();
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
            save();
        } catch (IOException e) {
            logger.warning("Failed to load config file " + fileName + ": " + e.getMessage());
            save();
        }
    }

    /**
     * Load configuration from file
     */
    public void load() {
        if (!Files.exists(filePath)) {
            save();
            return;
        }
        try {
            activeValues.clear();
            CommentedConfigurationNode root = loader.load();
            loadNode(root, "");
            save();
        } catch (IOException e) {
            logger.warning("Failed to load config file " + fileName + ": " + e.getMessage());
            save();
        }
    }

    private void loadNode(CommentedConfigurationNode root, String keyPrefix) {
        System.out.println("-> ROOT: " + keyPrefix);
        root.childrenMap().forEach((relativeKey, node) -> {
            String fullKey = keyPrefix + relativeKey;
            if (node.isMap()) {
                loadNode(node, fullKey + ".");
            }
            else if (defaultValues.containsKey(fullKey)) {
                System.out.println("LOADING: " + relativeKey);
                if (node.isList()) {
                    System.out.println("> IS LIST");
                    try {
                        Class<?> clazz = defaultValues.get(fullKey).getClass().getComponentType();
                        List<?> list = node.getList(clazz);
                        activeValues.put(fullKey, toArray(list, clazz));
                    } catch (SerializationException e) {
                        logger.warning("Failed to load list value for key " + fullKey + ". Using default value instead.");
                    }
                }
                else activeValues.put(fullKey, node.raw());
            }
            else {
                System.out.println("SKIPPING: " + fullKey);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<?> list, Class<T> clazz) {
        T[] arr = (T[]) Array.newInstance(clazz, list.size());
        return list.toArray(arr); // also may throw ClassCastException for incompatible elements
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
            
            // Try to parse as Integer array
            try {
                Integer[] intResult = new Integer[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    String trimmed = parts[i].trim();
                    intResult[i] = Integer.parseInt(trimmed);
                }
                return intResult;
            } catch (NumberFormatException e) {
                // Fall back to String array
                String[] stringResult = new String[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    stringResult[i] = parts[i].trim().replaceAll("^\"|\"$", "");
                }
                return stringResult;
            }
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
     * Save the current configuration, preserving comments and resetting them to defaults
     */
    public void save() {
//        if (true) return; //TODO: REMOVE THIS
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("# Configuration file for " + plugin.getName());
            writer.newLine();
            writer.write("# Uncomment lines to override default values");
            writer.newLine();
            writer.newLine();
            
            // Write all default values as comments
            for (Map.Entry<String, Object> entry : defaultValues.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
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
        if (value instanceof String[] array) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < array.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(array[i]).append("\"");
            }
            sb.append("]");
            return sb.toString();
        } else if (value instanceof Integer[] array) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < array.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(array[i]);
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
     * Get the file name
     */
    public String getFileName() {
        return fileName;
    }
}
