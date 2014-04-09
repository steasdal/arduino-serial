package org.teasdale.throwable

/**
 * Thrown if an attempt is made to send an unknown command
 */
class ArduinoSerialUnknownCommandException extends ArduinoSerialException {
    public ArduinoSerialUnknownCommandException( String message ) { super( message ) }
}
