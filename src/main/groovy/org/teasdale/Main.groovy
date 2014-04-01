package org.teasdale

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener;

public class Main {
    public static void main(String[] args) {

        // setup connection with default configuration (9600,8,N,1), register listener.
        ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()

        ArduinoSerialConfig config = factory.getArduinoSerialConfig()
        config.registerListener(new Listener())

        ArduinoSerialConnection connection = factory.getArduinoSerialConnection(config)

        // Open connection, send some data.
        connection.open()

        String message = args[0] + '\n'
        connection.writeBytes(message.getBytes())

        // Delay just long enough to receive any
        // incoming messages then shut 'er down.
        Thread.sleep(500)
        connection.close()
    }

    static class Listener implements ArduinoSerialListener {
        @Override
        void stringReceived(String string) {
            System.out.println string
        }
    }
}
