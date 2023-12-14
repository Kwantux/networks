package dev.nanoflux.config.util.exceptions;

/**
 * Raised when a config, that has previously been registered, is being registered again in the ConfigurationManager
 */
public class ConfigAlreadyRegisteredException extends QuillConfigException{

    /**
     * Raised when a config, that has previously been registered, is being registered again in the ConfigurationManager
     * @param message The displayed message
     */
    public ConfigAlreadyRegisteredException(String message) {
        super(message);
    }
}
