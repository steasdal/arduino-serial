package org.teasdale.api;

import java.util.Collection;

public interface ArduinoSerialConnection {
    public Collection<String> ports();
    public void open();
    public void writeBytes(byte[] bytes);
    public void close();
}
