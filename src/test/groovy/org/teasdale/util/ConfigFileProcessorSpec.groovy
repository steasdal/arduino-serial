package org.teasdale.util

import org.teasdale.api.ArduinoSerialConfig
import spock.lang.Specification

class ConfigFileProcessorSpec extends Specification {

    def "Verify that the ConfigFileProcessor will properly parse a config file"() {
        setup: 'a config file string to parse'

        String configString =
                '''
                serial.portname = "COM1"
                serial.baudrate = 19200
                serial.databits = 8
                serial.parity = "none"
                serial.stopbits = 1
                serial.updatefrequency = 10
                serial.maxmissedupdates = 5

                commands {
                    blink {
                        name = "BLINK"
                        value = 3000
                    }

                    motor01 {
                        name = "MTR1"
                        value = 0
                    }

                    servo01 {
                        name = "SRV1"
                        value = 90
                    }

                    relay01 {
                        name = "RELAY1"
                        value = 0
                    }
                }
                '''

        InputStream configStream = new ByteArrayInputStream( configString.getBytes() )

        when: "the above configuration is parsed"
        ArduinoSerialConfig arduinoSerialConfig = ConfigFileProcessor.process(configStream)

        then: "the resulting ArduinoSerialConfig object is configured as expected"
        arduinoSerialConfig.getPortname() == "COM1"
        arduinoSerialConfig.getBaudrate() == ArduinoSerialConfig.Baudrate.BAUDRATE_19200
        arduinoSerialConfig.getDatabits() == ArduinoSerialConfig.Databits.DATABITS_8
        arduinoSerialConfig.getParity() == ArduinoSerialConfig.Parity.NONE
        arduinoSerialConfig.getStopbits() == ArduinoSerialConfig.Stopbits.ONE
        arduinoSerialConfig.getUpdateFrequency() == 10
        arduinoSerialConfig.getMissedUpdatesAllowed() == 5

        arduinoSerialConfig.getRegisteredCommands().each { it in ["BLINK", "MTR1", "SRV1", "RELAY1"] }
    }


    def "Verify that an empty config file results in an ArduinoSerialConfig object set to all defaults "() {
        setup: 'an empty config file'
        String emptyConfig = ""
        InputStream configStream = new ByteArrayInputStream( emptyConfig.getBytes() )

        when: 'the empty config stream is parsed'
        ArduinoSerialConfig arduinoSerialConfig = ConfigFileProcessor.process(configStream)

        then: 'the resulting ArduinoSerialConfig object is configured with all defaults'
        arduinoSerialConfig.getPortname() == ArduinoSerialConfig.DEFAULT_PORTNAME
        arduinoSerialConfig.getBaudrate() == ArduinoSerialConfig.DEFAULT_BAUD_RATE
        arduinoSerialConfig.getDatabits() == ArduinoSerialConfig.DEFAULT_DATA_BITS
        arduinoSerialConfig.getParity() == ArduinoSerialConfig.DEFAULT_PARITY
        arduinoSerialConfig.getStopbits() == ArduinoSerialConfig.DEFAULT_STOP_BITS
        arduinoSerialConfig.getUpdateFrequency() == ArduinoSerialConfig.DEFAULT_UPDATE_FREQUENCY
        arduinoSerialConfig.getMissedUpdatesAllowed() == ArduinoSerialConfig.DEFAULT_MISSED_UPDATES_ALLOWED

        arduinoSerialConfig.getRegisteredCommands().size() == 0
    }
}
