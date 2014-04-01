package org.teasdale.impl

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConfig.Baudrate
import org.teasdale.api.ArduinoSerialConfig.Databits
import org.teasdale.api.ArduinoSerialConfig.Parity
import org.teasdale.api.ArduinoSerialConfig.Stopbits
import org.teasdale.api.ArduinoSerialListener

class ArduinoSerialConfigImpl implements ArduinoSerialConfig {

    String portname = DEFAULT_PORTNAME;
    Baudrate _baudrate = DEFAULT_BAUD_RATE
    Databits _databits = DEFAULT_DATA_BITS
    Parity _parity = DEFAULT_PARITY
    Stopbits _stopbits = DEFAULT_STOP_BITS

    Collection<ArduinoSerialListener> listeners = Collections.synchronizedSet(new HashSet());

    @Override
    void setPortname(String newPortname) {
        portname = newPortname
    }

    @Override
    void setBaudrate(Baudrate newBaudrate) {
        _baudrate = newBaudrate
    }

    public Baudrate getBaudrate() { return _baudrate }

    @Override
    void setDatabits(Databits newDatabits) {
        _databits = newDatabits
    }

    public Databits getDatabits() { return _databits }

    @Override
    void setParity(Parity newParity) {
        _parity = newParity
    }

    public Parity getParity() { return _parity }

    @Override
    void setStopbits(Stopbits newStopbits) {
        _stopbits = newStopbits
    }

    public Stopbits getStopbits() { return _stopbits }

    @Override
    void registerListener(ArduinoSerialListener listener) {
        listeners.add(listener);
    }

    public Collection<ArduinoSerialListener> getListeners() { return listeners }
}
