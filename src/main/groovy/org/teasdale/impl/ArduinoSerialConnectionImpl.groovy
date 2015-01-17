package org.teasdale.impl

import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortList
import org.apache.commons.lang3.Validate
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialListener
import org.teasdale.throwable.ArduinoSerialMethodOrderException
import org.teasdale.throwable.ArduinoSerialUnknownCommandException
import org.teasdale.util.CommandBuilder
import org.teasdale.util.DataUpdater

import java.util.concurrent.ConcurrentHashMap

class ArduinoSerialConnectionImpl implements ArduinoSerialConnection {

    ArduinoSerialConfigImpl arduinoSerialConfigImpl;
    SerialPort serialPort = null;
    DataUpdater dataUpdater = null;

    enum SerialState {UNOPENED, OPENED, CLOSED}
    SerialState serialState = SerialState.UNOPENED

    public ArduinoSerialConnectionImpl(ArduinoSerialConfigImpl arduinoSerialConfigImpl) {
        this.arduinoSerialConfigImpl = arduinoSerialConfigImpl
    }

    @Override
    Collection<String> ports() {
        LinkedHashSet<String> ports = new LinkedHashSet<String>()

        SerialPortList.getPortNames().each {
            ports.add(it);
        }

        return ports;
    }

    @Override
    void open() {
        verifyOpenState()
        constructSerialPort()
        configureAndOpenSerialPort()
        waitTwoSeconds()
        sendInitializationStrings()
        configureAndStartDataUpdater()
        setStateOpened()
    }

    @Override
    public void updateCommand(String commandName, int value) {
        validateCommandName(commandName)
        verifyWriteState()
        updateCommand( arduinoSerialConfigImpl.getCommands(), commandName, value )
    }

    @Override
    public void send(String string) {
        verifyWriteState()
        sendString(string);
    }

    @Override
    void close() {
        verifyCloseState()
        stopDataUpdater()
        closeSerialPort()
        setStateClosed()
    }

    /* ************************************************************************************************************* */

    void verifyOpenState() {
        if( serialState != SerialState.UNOPENED ) {
            throw new ArduinoSerialMethodOrderException("The open() method can only be called once")
        }
    }

    void constructSerialPort() {
        serialPort = new SerialPort(getPortname())
    }

    void configureAndOpenSerialPort() {
        serialPort.openPort()
        serialPort.setParams(getBaudrate(), getDatabits(), getStopbits(), getParity())
        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        serialPort.addEventListener(new SerialPortListener(serialPort))
    }

    /**
     * The serialPort.setParams() method causes the Arduino Uno to reset.
     * Delay for a moment to give the Arduino time to reset before writing.
     */
    static void waitTwoSeconds() {
        Thread.sleep(2000)
    }

    void sendInitializationStrings() {
        String updateFrequencyInitString = CommandBuilder.buildUpdateFrequencyInitString(arduinoSerialConfigImpl)
        String missedUpdatesAllowedInitString = CommandBuilder.buildMissedUpdatesAllowedInitString(arduinoSerialConfigImpl)
        Collection<String> commandInitStrings = CommandBuilder.buildCommandInitStrings(arduinoSerialConfigImpl)

        synchronizedWriteBytes(updateFrequencyInitString.getBytes())
        synchronizedWriteBytes(missedUpdatesAllowedInitString.getBytes())
        commandInitStrings.each { String command ->
            synchronizedWriteBytes(command.getBytes())
        }
    }

    void configureAndStartDataUpdater() {
        dataUpdater = new DataUpdater(this)
        dataUpdater.start()
    }

    void setStateOpened() {
        serialState = SerialState.OPENED
    }

    void verifyWriteState() {
        if( serialState == SerialState.UNOPENED ) {
            throw new ArduinoSerialMethodOrderException("The open() method must be called before writing serial data")
        } else if( serialState == SerialState.CLOSED ) {
            throw new ArduinoSerialMethodOrderException("The close() method has been called - unable to write data")
        }
    }

    synchronized void synchronizedWriteBytes(byte[] bytes) {
        serialPort.writeBytes( bytes )
    }

    static void validateByteArray(byte[] bytes) {
        Validate.notEmpty( (byte[]) bytes )
    }

    static void validateCommandName(String commandName) {
        Validate.notEmpty( (String) commandName )
    }

    static void updateCommand(ConcurrentHashMap<String, ArduinoSerialCommand> commands, String commandName, int value) {
        ArduinoSerialCommand oldCommand = commands.get(commandName)

        if( oldCommand == null ) {
            throw new ArduinoSerialUnknownCommandException("Unknown command: ${commandName}")
        } else {
            ArduinoSerialCommand newCommand = new ArduinoSerialCommand(oldCommand.name, oldCommand.currentValue);
            commands.replace(commandName, newCommand)
        }
    }

    void sendString(String string) {
        synchronizedWriteBytes( (string + '\n').getBytes() )
    }

    void verifyCloseState() {
        if( serialState != SerialState.OPENED ) {
            throw new ArduinoSerialMethodOrderException("The open() method must be called before close()")
        }
    }

    void stopDataUpdater() {
        dataUpdater.stop()
        dataUpdater = null
    }

    void closeSerialPort() {
        serialPort.closePort()
    }

    void setStateClosed() {
        serialState = SerialState.CLOSED
    }

    String getPortname() {
        arduinoSerialConfigImpl.portname
    }

    int getBaudrate() {
        arduinoSerialConfigImpl.getBaudrate().value()
    }

    int getDatabits() {
        arduinoSerialConfigImpl.getDatabits().value()
    }

    int getStopbits() {
        int result;

        switch (arduinoSerialConfigImpl.getStopbits()) {
            case ArduinoSerialConfig.Stopbits.ONE:
                result = SerialPort.STOPBITS_1
                break
            case ArduinoSerialConfig.Stopbits.TWO:
                result = SerialPort.STOPBITS_2
                break
        }

        return result
    }

    int getParity() {
        int result;

        switch (arduinoSerialConfigImpl.getParity()) {
            case ArduinoSerialConfig.Parity.NONE:
                result = SerialPort.PARITY_NONE
                break
            case ArduinoSerialConfig.Parity.EVEN:
                result = SerialPort.PARITY_EVEN
                break
            case ArduinoSerialConfig.Parity.ODD:
                result = SerialPort.PARITY_ODD
                break
        }

        return result
    }

    void notifyListeners(String newString) {
        Collection<ArduinoSerialListener> listeners = arduinoSerialConfigImpl.getListeners()

        synchronized(listeners) {
            for(ArduinoSerialListener listener in listeners) {
                listener.stringReceived(newString);
            }
        }

    }

    /* ************************************************************************************************************* */

    /**
     * Queue up received serial data and send it to all listeners
     * only once we've received an entire null terminated string.
     */
    class SerialPortListener implements SerialPortEventListener {

        private SerialPort serialPort;
        private StringBuffer stringBuffer = new StringBuffer();

        public SerialPortListener(SerialPort serialPort) {
            this.serialPort = serialPort
        }

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                try {
                    byte[] buffer = serialPort.readBytes(event.getEventValue())
                    String string = new String(buffer);

                    for( char theChar in string.toCharArray() ) {
                        if( theChar == '\n' ) {
                            notifyListeners(stringBuffer.toString())
                            stringBuffer.delete(0, stringBuffer.length())
                        } else {
                            stringBuffer.append(theChar)
                        }
                    }
                } catch (Exception ex) {
                    System.err.println "Exception: ${ex}"
                }
            }
        }
    }
}
