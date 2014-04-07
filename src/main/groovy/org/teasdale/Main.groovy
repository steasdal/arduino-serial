package org.teasdale

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener;

public class Main {

    public static Console console = null

    public static ArduinoSerialFactory factory = null
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

    private static void start() {
        factory = ArduinoSerialFactory.getInstance()

        ArduinoSerialConfig config = factory.getArduinoSerialConfig()
        config.registerListener(new Listener())
        config.setPortname("/dev/tty.usbmodemfd121")

        connection = factory.getArduinoSerialConnection(config)

        connection.open()

        consoleWriteLn()
        consoleWriteLn("Connection Open")
        consoleWriteLn()
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
