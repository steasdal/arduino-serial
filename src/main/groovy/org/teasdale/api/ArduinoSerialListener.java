package org.teasdale.api;

/**
 * A listener interface for receiving serial messages sent from the Arduino.
 * <br><br>
 * Register implementations of this interface with the
 * {@link ArduinoSerialConfig#registerListener(ArduinoSerialListener)} method.
 */
public interface ArduinoSerialListener {

    /**
     * This method will be called <i>only</i> once an
     * entire null terminated string has been received
     * from the Arduino.
     * @param string The full string returned from the
     *               Arduino.
     */
    void stringReceived(String string);
}
