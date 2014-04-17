package org.teasdale

import groovy.swing.SwingBuilder
import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Gui {
    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"
    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    def statusLabel

    public void show() {
        def swingBuilder = new SwingBuilder()

        def makeControlPanel = {
            swingBuilder.panel(){
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
            swingBuilder.panel(){
                statusLabel = swingBuilder.label(
                        text: "not open",
                        horizontalAlignment: JLabel.CENTER,
                        verticalAlignment: JLabel.TOP
                )
            }
        }

        def makeSliderPanel = {
            swingBuilder.panel(){
                boxLayout(axis: BoxLayout.PAGE_AXIS)

                swingBuilder.panel(){
                    swingBuilder.label(
                            text: "Blink Interval",
                            horizontalAlignment: JLabel.CENTER,
                            verticalAlignment: JLabel.TOP
                    )
                    swingBuilder.slider(
                            minimum: 25,
                            maximum: 2500,
                            value: ((2500-25)/2),
                            minorTickSpacing: 25
                    ).addChangeListener(new BlinkSliderListener())
                }

                swingBuilder.panel(){
                    swingBuilder.label(
                            text: "Servo 01",
                            horizontalAlignment: JLabel.CENTER,
                            verticalAlignment: JLabel.TOP
                    )
                    swingBuilder.slider(
                            minimum: 0,
                            maximum: 180,
                            value: 90,
                            minorTickSpacing: 1
                    ).addChangeListener(new Servo01SliderListener())
                }

                swingBuilder.panel(){
                    swingBuilder.label(
                            text: "Servo 02",
                            horizontalAlignment: JLabel.CENTER,
                            verticalAlignment: JLabel.TOP
                    )
                    swingBuilder.slider(
                            minimum: 0,
                            maximum: 180,
                            value: 90,
                            minorTickSpacing: 1
                    ).addChangeListener(new Servo02SliderListener())
                }
            }
        }

        swingBuilder.frame(
                title:"Arduino Serial GUI",
                defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE,
                size:[400,300],
                show:true){
            boxLayout(axis: BoxLayout.PAGE_AXIS)
            makeControlPanel()
            makeLabelPanel()
            vglue()
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
            println string
        }
    }

    static abstract class SliderListener implements ChangeListener {
        @Override
        void stateChanged(ChangeEvent changeEvent) {
            if( connection && changeEvent.getSource() instanceof JSlider) {
                int sliderValue = ((JSlider) changeEvent.getSource()).value
                update(sliderValue);
            }
        }

        abstract void update(int value);
    }

    static class BlinkSliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("BLINK", value)
        }
    }

    static class Servo01SliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("SERVO_01", value)
        }
    }

    static class Servo02SliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("SERVO_02", value)
        }
    }


}
