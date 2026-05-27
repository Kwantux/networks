package de.kwantux.config.util.exceptions;


/**
 * Raised when a non-existent node is requested
 */
public class InvalidNodeException extends Exception {

    /**
     * Raised when a non-existent node is requested
     * @param message The displayed message
     */
    public InvalidNodeException(String message) {
        super(message);
    }
}
