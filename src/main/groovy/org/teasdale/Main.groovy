package org.teasdale

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener

public class Main {

    public static Console console = null
    public static def externalConfig = null
    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"

    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    public static final String START = /start/
    public static final String STOP = /stop/
    public static final String SEND = /^/ + /send/ + /\s*/ + /(\w+)/ + /$/    /* "send", zero or more whitespace characters, one or more word characters */
    public static final String STATUS = /status/
    public static final String HELP = /help/
    public static final String QUIT = /quit/

    public static final String NEWLINE = "\n\r";

    public static void main(String[] args) {
        getNewConsoleExitIfNull()

        loadConfigFile()

        consoleWriteLn('Please enter a command (type "help" for available commands)')
        consoleWriteLn();

        for(;;) {

            String input = console.readLine().trim()

            switch( input ) {
                case ~START:
                    start()
                    break
                case ~SEND:
                    transmit(input)
                    break
                case ~STATUS:
                    status()
                    break
                case ~STOP:
                    stop()
                    break
                case ~HELP:
                    getHelp()
                    break
                case ~QUIT:
                    quit()
                    break
                default:
                    unrecognizedCommand(input)
            }
        }
    }

    private static void getNewConsoleExitIfNull() {
        console = System.console();

        if( console == null ) {
            System.out.println("Console object is null.  Start failed.");
            System.exit(1);
        }
    }

    private static void loadConfigFile() {
        File configFile = new File(EXTERNAL_CONFIG_FILE)

        if(configFile.exists()) {
            consoleWriteLn("Loading configuration file ${EXTERNAL_CONFIG_FILE}")
            externalConfig = new ConfigSlurper().parse( configFile.text )
        } else {
            consoleWriteLn("No external configuration file found")
        }
    }

    private static void start() {
        ArduinoSerialConfig config = getConfig()
        config.registerListener(new Listener())

        connection = factory.getArduinoSerialConnection(config)
        connection.open()

        consoleWriteLn()
        consoleWriteLn("Connection Open")
        consoleWriteLn()
    }

    private static ArduinoSerialConfig getConfig() {
        ArduinoSerialConfig config = factory.getArduinoSerialConfig()

        if(externalConfig != null) {
            def portname = externalConfig.serial.portname
            def baudrate = externalConfig.serial.baudrate
            def databits = externalConfig.serial.databits
            def stopbits = externalConfig.serial.stopbits
            def parity = externalConfig.serial.parity

            if( portname ) { config.setPortname( portname ) }

            if( baudrate ) {
                try {
                    config.setBaudrate( ArduinoSerialConfig.Baudrate.parse( baudrate ) )
                } catch( Throwable throwable ) {
                    consoleWriteLn("Unable to parse baudrate value from config file: ${throwable.getMessage()}")
                }
            }

            if( databits ) {
                try {
                    config.setDatabits( ArduinoSerialConfig.Databits.parse( databits ) )
                } catch( Throwable throwable ) {
                    consoleWriteLn("Unable to parse databits value from config file: ${throwable.getMessage()}")
                }
            }

            if( stopbits ) {
                try {
                    config.setStopbits( ArduinoSerialConfig.Stopbits.parse( stopbits ) )
                } catch( Throwable throwable ) {
                    consoleWriteLn("Unable to parse stopbits value from config file: ${throwable.getMessage()}")
                }
            }

            if( parity ) {
                try {
                    config.setParity( ArduinoSerialConfig.Parity.parse( parity ) )
                } catch( Throwable throwable ) {
                    consoleWriteLn("Unable to parse parity value from config file: ${throwable.getMessage()}")
                }
            }
        }

        return config
    }

    private static void transmit(String input) {
        ( input =~ SEND ).each { match, command ->
            String message = command + '\n'
            connection.writeBytes( message.getBytes() )
        }
    }

    private static void status() {
        consoleWriteLn()

        if( connection != null ) {
            consoleWriteLn("connection open")
        } else {
            consoleWriteLn("connection closed")
        }

        consoleWriteLn()
    }

    private static void stop() {
        connection.close()
        connection = null

        consoleWriteLn()
        consoleWriteLn("Connection Closed")
        consoleWriteLn()
    }

    private static void getHelp() {
        consoleWriteLn()
        printAvailableCommands()
        consoleWriteLn()
    }

    private static void quit() {
        if( connection != null ) {
            stop()
        }

        consoleWriteLn()
        consoleWriteLn("Quitting.  Good Bye!")
        System.exit(0)
    }

    private static void unrecognizedCommand(String input) {
        consoleWriteLn()
        consoleWriteLn("Unrecognized command: ${input}")
        consoleWriteLn()
        printAvailableCommands()
        consoleWriteLn()
    }

    private static void consoleWriteLn(final String string) {
        console.printf(string + NEWLINE);
    }

    private static void consoleWriteLn() {
        consoleWriteLn("");
    }

    private static void printAvailableCommands() {
        consoleWriteLn("Available Commands:")
        consoleWriteLn("help - Display this list of available commands")
        consoleWriteLn("status - Get the current status of the listener")
        consoleWriteLn("start - Attempt to start the listener")
        consoleWriteLn("stop - Attempt to stop the listener")
        consoleWriteLn("send - Send a command to the Arduino")
        consoleWriteLn("quit - Quit this interactive console")
    }

    /* ***************************************************************************************** */

    /**
     * An instance of this class will be registered to receive incoming serial messages
     * via the {@link ArduinoSerialConfig#registerListener(ArduinoSerialListener)} method.
     */
    static class Listener implements ArduinoSerialListener {
        @Override
        void stringReceived(String string) {
            consoleWriteLn(string)
        }
    }
}
