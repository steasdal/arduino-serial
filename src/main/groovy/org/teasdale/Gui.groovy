package org.teasdale

import groovy.swing.SwingBuilder
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener

import javax.swing.JFrame
import javax.swing.JLabel
import java.awt.BorderLayout

class Gui {

    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"
    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    public void show() {
        def swingBuilder = new SwingBuilder()

        def statusLabel = {
            swingBuilder.label(text: "not open", horizontalAlignment: JLabel.CENTER, verticalAlignment: JLabel.TOP)
        }

        def controlPanel = {
            swingBuilder.panel(constraints: BorderLayout.NORTH){
                button(text:"Start", actionPerformed:{
                    start()
                } )
                button(text:"Stop", actionPerformed:{
                    stop()
                })
            }
        }

        swingBuilder.frame(title:"Arduino Serial GUI",
                defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE,
                size:[400,500],
                show:true) {

                controlPanel()
                statusLabel()

        }
    }

    /* ***************************************************************************************** */

    private static void start() {
        ArduinoSerialConfig config = getConfig()
        config.registerListener(new Listener())

        connection = factory.getArduinoSerialConnection(config)
        connection.open()

        println "Connection Open"
    }

    private static ArduinoSerialConfig getConfig() {
        try {
            return factory.getArduinoSerialConfig(EXTERNAL_CONFIG_FILE)
        } catch (Throwable throwable) {
            println throwable.getMessage()
        }
    }

    private static void stop() {
        connection.close()
        connection = null

        println "Connection Closed"
    }

    /* ***************************************************************************************** */

    /**
     * An instance of this class will be registered to receive incoming serial messages
     * via the {@link ArduinoSerialConfig#registerListener(org.teasdale.api.ArduinoSerialListener)} method.
     */
    static class Listener implements ArduinoSerialListener {
        @Override
        void stringReceived(String string) {
            // Using println here because the console tends to hold
            // onto output instead of displaying it immediately
            println string
        }
    }
}
