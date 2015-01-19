package org.teasdale.impl

import org.apache.commons.lang3.Validate
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConfig.Baudrate
import org.teasdale.api.ArduinoSerialConfig.Databits
import org.teasdale.api.ArduinoSerialConfig.Parity
import org.teasdale.api.ArduinoSerialConfig.Stopbits
import org.teasdale.api.ArduinoSerialListener

import java.util.concurrent.ConcurrentHashMap

class ArduinoSerialConfigImpl implements ArduinoSerialConfig {

    String portname = DEFAULT_PORTNAME;
    Baudrate baudrate = DEFAULT_BAUD_RATE
    Databits databits = DEFAULT_DATA_BITS
    Parity parity = DEFAULT_PARITY
    Stopbits stopbits = DEFAULT_STOP_BITS
    int updateFrequency = DEFAULT_UPDATE_FREQUENCY
    int missedUpdatesAllowed = DEFAULT_MISSED_UPDATES_ALLOWED

    ConcurrentHashMap<String, Integer> updateValues = new ConcurrentHashMap<String, Integer>()
    ConcurrentHashMap<String, Boolean> updateFlags = new ConcurrentHashMap<String, Boolean>()

    Collection<ArduinoSerialListener> listeners = Collections.synchronizedSet(new HashSet<ArduinoSerialListener>())

    @Override
    void setPortname(String newPortname) {
        Validate.notEmpty(newPortname)
        portname = newPortname
    }

    @Override
    void setBaudrate(Baudrate newBaudrate) {
        baudrate = newBaudrate
    }

    @Override
    public Baudrate getBaudrate() { return baudrate }

    @Override
    void setDatabits(Databits newDatabits) {
        databits = newDatabits
    }

    @Override
    public Databits getDatabits() { return databits }

    @Override
    void setParity(Parity newParity) {
        parity = newParity
    }

    @Override
    public Parity getParity() { return parity }

    @Override
    void setStopbits(Stopbits newStopbits) {
        stopbits = newStopbits
    }

    @Override
    public Stopbits getStopbits() { return stopbits }

    @Override
    public void setUpdateFrequency(int updateFrequency) {
        Validate.inclusiveBetween(MINIMUM_UPDATE_FREQUENCY, MAXIMUM_UPDATE_FREQUENCY, updateFrequency)
        this.updateFrequency = updateFrequency
    }

    @Override
    public int getUpdateFrequency() {
        return updateFrequency
    }

    @Override
    public void setMissedUpdatesAllowed(int missedUpdatesAllowed) {
        Validate.inclusiveBetween(MINIMUM_MISSED_UPDATES_ALLOWED, MAXIMUM_MISSED_UPDATES_ALLOWED, missedUpdatesAllowed);
        this.missedUpdatesAllowed = missedUpdatesAllowed;
    }

    @Override
    public int getMissedUpdatesAllowed() {
        return missedUpdatesAllowed
    }

    @Override
    public void registerCommand(String commandName, int initialValue) {
        Validate.notEmpty(commandName)

        updateValues.putIfAbsent(commandName, new Integer(initialValue))
        updateFlags.putIfAbsent(commandName, Boolean.FALSE)
    }

    public ConcurrentHashMap<String, Integer> getUpdateValues() { return updateValues }
    public ConcurrentHashMap<String, Boolean> getUpdateFlags() { return updateFlags }

    @Override
    public String[] getRegisteredCommands() {
        def commandArray = []

        for(Map.Entry<String, Integer> updateValue: updateValues.entrySet()) {
            commandArray << updateValue.getKey()
        }

        return commandArray as String[]
    }

    @Override
    void registerListener(ArduinoSerialListener listener) {
        listeners.add(listener);
    }

    public Collection<ArduinoSerialListener> getListeners() { return listeners }
}
