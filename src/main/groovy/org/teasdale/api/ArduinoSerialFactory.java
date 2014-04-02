package org.teasdale.api;

import org.teasdale.impl.ArduinoSerialConfigImpl;
import org.teasdale.impl.ArduinoSerialConnectionImpl;

/**
 * A singleton factory for obtaining concrete instances of
 * {@link ArduinoSerialConfig} and {@link ArduinoSerialConnection}.
 */
public class ArduinoSerialFactory {

    private static final ArduinoSerialFactory instance = new ArduinoSerialFactory();
    private ArduinoSerialFactory() {}

    /**
     * @return the one and only instance of this factory.
     */
    public static ArduinoSerialFactory getInstance() { return instance; }

    /**
     * @return an instance of {@link ArduinoSerialConfig} with all config values
     * set to their defaults.
     */
    public ArduinoSerialConfig getArduinoSerialConfig() {
        return new ArduinoSerialConfigImpl();
    }

    /**
     * @return an instance of {@link ArduinoSerialConnection} configured
     * with all {@link ArduinoSerialConfig} defaults and no registered listeners.
     */
    public ArduinoSerialConnection getArduinoSerialConnection() {
        ArduinoSerialConfigImpl defaultConfig = new ArduinoSerialConfigImpl();
        return new ArduinoSerialConnectionImpl(defaultConfig);
    }

    /**
     * @param arduinoSerialConfig An instance of {@link ArduinoSerialConfig}
     *                            containing configuration settings that'll be
     *                            used to configure the instance of
     *                            {@link ArduinoSerialConnection} returned by
     *                            this method.
     * @return an instance of {@link ArduinoSerialConnection} configured
     * according to the settings in the instance of {@link ArduinoSerialConfig}
     * passed in via the arduinoSerialConfig parameter.
     */
    public ArduinoSerialConnection getArduinoSerialConnection(ArduinoSerialConfig arduinoSerialConfig) {
        return new ArduinoSerialConnectionImpl((ArduinoSerialConfigImpl)arduinoSerialConfig);
    }
}
