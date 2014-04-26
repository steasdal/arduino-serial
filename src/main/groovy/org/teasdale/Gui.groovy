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
import javax.swing.JTextArea
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.text.DefaultCaret
import java.awt.BorderLayout

class Gui {
    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"
    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    JTextArea statusText

    public void show() {
        def swingBuilder = new SwingBuilder()

        def makeControlPanel = {
            swingBuilder.panel(){
                button(text:"Start", actionPerformed:{
                    start(statusText)
                })
                button(text:"Stop", actionPerformed:{
                    stop(statusText)
                })
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

                swingBuilder.panel(){
                    swingBuilder.label(
                            text: "Motor 01",
                            horizontalAlignment: JLabel.CENTER,
                            verticalAlignment: JLabel.TOP
                    )
                    swingBuilder.slider(
                            minimum: 0,
                            maximum: 255,
                            value: 0,
                            minorTickSpacing: 5
                    ).addChangeListener(new Motor01SliderListener())
                }

                swingBuilder.panel(){
                    swingBuilder.label(
                            text: "Motor 02",
                            horizontalAlignment: JLabel.CENTER,
                            verticalAlignment: JLabel.TOP
                    )
                    swingBuilder.slider(
                            minimum: 0,
                            maximum: 255,
                            value: 0,
                            minorTickSpacing: 5
                    ).addChangeListener(new Motor02SliderListener())
                }
            }
        }

        def resultsPanel = {
            swingBuilder.scrollPane(constraints: BorderLayout.CENTER){
                statusText = textArea(
                        rows:10,
                        editable: false,
                        autoscrolls: true
                )
                DefaultCaret caret = (DefaultCaret)statusText.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            }
        }

        swingBuilder.frame(
                title:"Arduino Serial GUI",
                defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE,
                size:[400,500],
                show:true){
            boxLayout(axis: BoxLayout.PAGE_AXIS)
            makeControlPanel()
            makeSliderPanel()
            vglue()
            resultsPanel()
        }
    }

    /* ***************************************************************************************** */

    private static void start(JTextArea results) {
        ArduinoSerialConfig config = getConfig(results)
        config.registerListener(new Listener(results))

        connection = factory.getArduinoSerialConnection(config)
        connection.open()

        results.append("Connection Open \n")
    }

    private static ArduinoSerialConfig getConfig(JTextArea results) {
        try {
            return factory.getArduinoSerialConfig(EXTERNAL_CONFIG_FILE)
        } catch (Throwable throwable) {
            results.append(throwable.getMessage() + '\n')
        }
    }

    private static void stop(JTextArea results) {
        connection.close()
        connection = null

        results.append("Connection Closed \n")
    }

    /* ***************************************************************************************** */

    /**
     * An instance of this class will be registered to receive incoming serial messages
     * via the {@link ArduinoSerialConfig#registerListener(org.teasdale.api.ArduinoSerialListener)} method.
     */
    static class Listener implements ArduinoSerialListener {
        JTextArea results = null;

        public Listener(JTextArea results) {
            this.results = results
        }

        @Override
        void stringReceived(String string) {
            results.append(string + '\n')
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
            connection.updateCommand("SRV1", value)
        }
    }

    static class Servo02SliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("SRV2", value)
        }
    }

    static class Motor01SliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("MTR1", value)
        }
    }

    static class Motor02SliderListener extends SliderListener {
        @Override void update(int value) {
            connection.updateCommand("MTR2", value)
        }
    }
}
