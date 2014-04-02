package org.teasdale.throwable;

/**
 * Abstract base class for all Arduino Serial Exceptions
 */
public abstract class ArduinoSerialException extends RuntimeException {
    protected ArduinoSerialException( String message ) {
        super( message );
    }
}
