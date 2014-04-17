package org.teasdale

import groovy.swing.SwingBuilder
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.BorderLayout

class Gui {
    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"
    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    def statusLabel

    public void show() {
        def swingBuilder = new SwingBuilder()

        def makeControlPanel = {
            swingBuilder.panel(constraints: BorderLayout.NORTH){
                button(text:"Start", actionPerformed:{
                    start()
                    statusLabel.text = "connection open"
                })
                button(text:"Stop", actionPerformed:{
                    stop()
                    statusLabel.text = "connection closed"
                })
            }
        }

        def makeLabelPanel = {
            swingBuilder.panel(constraints: BorderLayout.CENTER){
                statusLabel = swingBuilder.label(
                        text: "not open",
                        horizontalAlignment: JLabel.CENTER,
                        verticalAlignment: JLabel.TOP
                )
            }
        }

        def makeSliderPanel = {
            swingBuilder.panel(constraints: BorderLayout.SOUTH){
                swingBuilder.label(
                        text: "Blink Interval",
                        horizontalAlignment: JLabel.CENTER,
                        verticalAlignment: JLabel.TOP
                )
                swingBuilder.slider(
                        minimum: 25,
                        maximum: 2500,
                        minorTickSpacing: 25
                ).addChangeListener(new SliderListener())
            }
        }

        swingBuilder.frame(
                title:"Arduino Serial GUI",
                defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE,
                size:[400,300],
                show:true){
            makeControlPanel()
            makeLabelPanel()
            makeSliderPanel()
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

    static class SliderListener implements ChangeListener {
        @Override
        void stateChanged(ChangeEvent changeEvent) {
            if( connection && changeEvent.getSource() instanceof JSlider) {
                int sliderValue = ((JSlider) changeEvent.getSource()).value
                connection.updateCommand("BLINK", sliderValue);
            }
        }
    }
}
