package org.teasdale.impl

import org.apache.commons.lang3.Validate
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConfig.Baudrate
import org.teasdale.api.ArduinoSerialConfig.Databits
import org.teasdale.api.ArduinoSerialConfig.Parity
import org.teasdale.api.ArduinoSerialConfig.Stopbits
import org.teasdale.api.ArduinoSerialListener

class ArduinoSerialConfigImpl implements ArduinoSerialConfig {

    String portname = DEFAULT_PORTNAME;
    Baudrate baudrate = DEFAULT_BAUD_RATE
    Databits databits = DEFAULT_DATA_BITS
    Parity parity = DEFAULT_PARITY
    Stopbits stopbits = DEFAULT_STOP_BITS
    int updateFrequency = DEFAULT_UPDATE_FREQUENCY

    Map<String, ArduinoSerialCommand> commands = Collections.synchronizedMap(new HashMap<String, ArduinoSerialCommand>())
    Collection<ArduinoSerialListener> listeners = Collections.synchronizedSet(new HashSet<ArduinoSerialListener>())

    @Override
    void setPortname(String newPortname) {
        portname = newPortname
    }

    @Override
    void setBaudrate(Baudrate newBaudrate) {
        baudrate = newBaudrate
    }

    public Baudrate getBaudrate() { return baudrate }

    @Override
    void setDatabits(Databits newDatabits) {
        databits = newDatabits
    }

    public Databits getDatabits() { return databits }

    @Override
    void setParity(Parity newParity) {
        parity = newParity
    }

    public Parity getParity() { return parity }

    @Override
    void setStopbits(Stopbits newStopbits) {
        stopbits = newStopbits
    }

    public Stopbits getStopbits() { return stopbits }

    @Override
    public void setUpdateFrequency(int updateFrequency) {
        Validate.inclusiveBetween(MINIMUM_UPDATE_FREQUENCY, MAXIMUM_UPDATE_FREQUENCY, updateFrequency)
        this.updateFrequency = updateFrequency
    }

    public int getUpdateFrequency() {
        return updateFrequency
    }

    @Override
    public void registerCommand(String commandName, int initialValue) {
        Validate.notEmpty(commandName)
        ArduinoSerialCommand command = new ArduinoSerialCommand(commandName, initialValue)
        commands.put( command.name, command )
    }

    public Map<String, ArduinoSerialCommand> getCommands() { return commands }

    @Override
    void registerListener(ArduinoSerialListener listener) {
        listeners.add(listener);
    }

    public Collection<ArduinoSerialListener> getListeners() { return listeners }
}
