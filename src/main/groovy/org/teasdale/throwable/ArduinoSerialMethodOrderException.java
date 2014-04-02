package org.teasdale.throwable;

/**
 * Thrown if order-dependent methods are thrown out of sequence.
 */
public class ArduinoSerialMethodOrderException extends ArduinoSerialException {
    public ArduinoSerialMethodOrderException( String message ) {
        super( message );
    }
}