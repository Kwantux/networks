package de.kwantux.config.util.exceptions;


/**
 * Raised when a file has an invalid file format
 */

public class InvalidFileFormatExecption extends QuillConfigException {
    /**
     * Raised when a file has an invalid file format
     * @param message The displayed message
     */
    public InvalidFileFormatExecption(String message) {
        super(message);
    }
}
