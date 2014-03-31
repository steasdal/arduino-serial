package org.teasdale

import jssc.SerialPort
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {
    public static void main(String[] args) {
        def portNames = SerialPortList.getPortNames();

        portNames.each {
            System.out.println it
        }

        SerialPort serialPort = new SerialPort("COM3")

        try {
            serialPort.openPort()
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            )

            serialPort.writeBytes(args[0].getBytes())
            serialPort.closePort()
        } catch (SerialPortException serialPortException) {
            System.out.println serialPortException
        }





    }
}
