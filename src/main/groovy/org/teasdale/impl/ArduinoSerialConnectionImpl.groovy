package org.teasdale.impl

import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortEventListener
import jssc.SerialPortList
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialListener

class ArduinoSerialConnectionImpl implements ArduinoSerialConnection {

    ArduinoSerialConfigImpl arduinoSerialConfigImpl;

    SerialPort serialPort = null;

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
        serialPort = new SerialPort(getPortname())
        serialPort.openPort()
        serialPort.setParams(getBaudrate(), getDatabits(), getStopbits(), getParity())
        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        serialPort.addEventListener(new SerialPortListener(serialPort))

        // setParams causes the Arduino to reset.  Delay for a moment
        // to give the arduino time to reset before writing any bytes.
        Thread.sleep(2000)
    }

    @Override
    void writeBytes(byte[] bytes) {
        serialPort.writeBytes(bytes)
    }

    @Override
    void close() {
        serialPort.closePort()
    }

    /* ************************************************************************************************************* */

    private String getPortname() {
        arduinoSerialConfigImpl.portname
    }

    private int getBaudrate() {
        arduinoSerialConfigImpl.getBaudrate().value()
    }

    private int getDatabits() {
        arduinoSerialConfigImpl.getDatabits().value()
    }

    private int getStopbits() {
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

    private int getParity() {
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

    private void notifyListeners(String newString) {
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
