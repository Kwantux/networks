package de.kwantux.networks.utils;

import de.kwantux.networks.Main;

/**
 * Utility class for detecting development environment and enabling development-only features.
 */
public final class DevelopmentUtils {
    
    private static final boolean IS_DEVELOPMENT = Boolean.parseBoolean(
        System.getProperty("networks.development", "false")
    );
    
    private DevelopmentUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Returns true if running in development environment (runServer task)
     * Returns false if running in production (build task)
     */
    public static boolean isDevelopment() {
        return IS_DEVELOPMENT;
    }
    
    /**
     * Executes the given runnable only in development environment
     */
    public static void runInDevelopment(Runnable runnable) {
        if (isDevelopment()) {
            runnable.run();
        }
    }
    
    /**
     * Returns the development value if in development, otherwise returns the production value
     */
    public static <T> T ifDevelopment(T developmentValue, T productionValue) {
        return isDevelopment() ? developmentValue : productionValue;
    }


    /**
     * Prints the given message to the server log only in development environment
     */
    public static void devlog(String message) {
        if (isDevelopment()) {
            Main.logger.info("[DEV] " + message);
        }
    }
}
