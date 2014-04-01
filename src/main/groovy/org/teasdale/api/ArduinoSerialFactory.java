package org.teasdale.api;

import org.teasdale.impl.ArduinoSerialConfigImpl;
import org.teasdale.impl.ArduinoSerialConnectionImpl;

public class ArduinoSerialFactory {

    private static final ArduinoSerialFactory instance = new ArduinoSerialFactory();
    private ArduinoSerialFactory() {}
    public static ArduinoSerialFactory getInstance() { return instance; }

    public ArduinoSerialConfig getArduinoSerialConfig() {
        return new ArduinoSerialConfigImpl();
    }

    public ArduinoSerialConnection getArduinoSerialConnection() {
        ArduinoSerialConfigImpl defaultConfig = new ArduinoSerialConfigImpl();
        return new ArduinoSerialConnectionImpl(defaultConfig);
    }

    public ArduinoSerialConnection getArduinoSerialConnection(ArduinoSerialConfig arduinoSerialConfig) {
        return new ArduinoSerialConnectionImpl((ArduinoSerialConfigImpl)arduinoSerialConfig);
    }
}
