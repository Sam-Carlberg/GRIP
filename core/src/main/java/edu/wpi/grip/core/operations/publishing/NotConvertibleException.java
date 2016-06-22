package edu.wpi.grip.core.operations.publishing;

/**
 * An exception thrown when a type is not convertible. This shouldn't be thrown when using GRIP; all
 * converters must be responsible for ensuring that the data they convert is fully convertible.
 */
public class NotConvertibleException extends RuntimeException {

    public NotConvertibleException(String message) {
        super(message);
    }

    public NotConvertibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
