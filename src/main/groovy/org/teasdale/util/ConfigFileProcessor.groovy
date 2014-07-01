package org.teasdale.util

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.impl.ArduinoSerialConfigImpl

class ConfigFileProcessor {

    public static ArduinoSerialConfig process(final InputStream inputStream) {
        return parseConfigString(inputStream.text)
    }

    public static ArduinoSerialConfig process(final String filename) {
        File configFile = new File(filename)

        if( !configFile.exists() ) {
            throw new FileNotFoundException("${filename} does not exist");
        }

        return parseConfigString(configFile.text)
    }

    private static ArduinoSerialConfig parseConfigString(final String fileContent) {

        ArduinoSerialConfigImpl arduinoSerialConfigImpl = new ArduinoSerialConfigImpl()

        def externalConfig = new ConfigSlurper().parse( fileContent )

        if(externalConfig != null) {
            def portname = externalConfig.serial.portname
            def baudrate = externalConfig.serial.baudrate
            def databits = externalConfig.serial.databits
            def stopbits = externalConfig.serial.stopbits
            def parity = externalConfig.serial.parity
            def updatefrequency = externalConfig.serial.updatefrequency
            def maxmissedupdates = externalConfig.serial.maxmissedupdates
            def commands = externalConfig.commands


            if( portname ) { arduinoSerialConfigImpl.setPortname( portname ) }

            if( baudrate ) {
                arduinoSerialConfigImpl.setBaudrate( ArduinoSerialConfig.Baudrate.parse( baudrate ) )
            }

            if( databits ) {
                arduinoSerialConfigImpl.setDatabits( ArduinoSerialConfig.Databits.parse( databits ) )
            }

            if( stopbits ) {
                arduinoSerialConfigImpl.setStopbits( ArduinoSerialConfig.Stopbits.parse( stopbits ) )
            }

            if( parity ) {
                arduinoSerialConfigImpl.setParity( ArduinoSerialConfig.Parity.parse( parity ) )
            }

            if( updatefrequency ) {
                arduinoSerialConfigImpl.setUpdateFrequency( updatefrequency )
            }

            if( maxmissedupdates ) {
                arduinoSerialConfigImpl.setMissedUpdatesAllowed( maxmissedupdates )
            }

            if( commands ) {
                commands.values().each { command ->
                    arduinoSerialConfigImpl.registerCommand( command.name, command.value )
                }
            }
        }

        return arduinoSerialConfigImpl
    }
}
