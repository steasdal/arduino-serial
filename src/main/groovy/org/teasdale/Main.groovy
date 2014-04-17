package org.teasdale

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialConnection
import org.teasdale.api.ArduinoSerialFactory
import org.teasdale.api.ArduinoSerialListener
import org.teasdale.throwable.ArduinoSerialUnknownCommandException

public class Main {

    public static Console console = null
    public static final String EXTERNAL_CONFIG_FILE = "external_config.txt"

    public static ArduinoSerialFactory factory = ArduinoSerialFactory.getInstance()
    public static ArduinoSerialConnection connection = null

    public static final String START = /start/
    public static final String STOP = /stop/
    public static final String SEND = /^/ + /send/ + /\s+/ + /(\w+)/ + /$/    /* "send", one or more whitespace characters, one or more word characters */
    public static final String UPDATE = /^/ + /update/ + /\s+/ + /(\w+)/ + /\s+/ + /(\d+)/ + /$/  /* "update", whitespace, command name, whitespace, command value */
    public static final String STATUS = /status/
    public static final String GUI = /gui/
    public static final String HELP = /help/
    public static final String QUIT = /quit/

    public static final String NEWLINE = "\n\r"

    public static void main(String[] args) {
        getNewConsoleExitIfNull()

        consoleWriteLn('Please enter a command (type "help" for available commands)')
        consoleWriteLn()

        for(;;) {

            String input = console.readLine().trim()

            switch( input ) {
                case ~START:
                    start()
                    break
                case ~SEND:
                    transmit(input)
                    break
                case ~UPDATE:
                    update(input)
                    break
                case ~STATUS:
                    status()
                    break
                case ~STOP:
                    stop()
                    break
                case ~GUI:
                    gui()
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
        console = System.console()

        if( console == null ) {
            System.out.println("Console object is null.  Start failed.")
            System.exit(1)
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
        try {
            return factory.getArduinoSerialConfig(EXTERNAL_CONFIG_FILE)
        } catch (Throwable throwable) {
            consoleWriteLn(throwable.getMessage())
        }
    }

    private static void transmit(String input) {
        ( input =~ SEND ).each { match, command ->
            String message = command + '\n'
            connection.writeBytes( message.getBytes() )
        }
    }

    private static void update(String input) {
        ( input =~ UPDATE ).each { match, command, value ->
            try {
                connection.updateCommand( command, Integer.parseInt(value) )
            } catch ( NumberFormatException exception ) {
                consoleWriteLn("Unable to parse command value ${value} to an integer: ${exception.getMessage()}")
            } catch ( ArduinoSerialUnknownCommandException exception ) {
                consoleWriteLn(exception.getMessage())
            }
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

    private static void gui() {
        Gui gui = new Gui()
        gui.show()
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
        console.printf(string + NEWLINE)
    }

    private static void consoleWriteLn() {
        consoleWriteLn("")
    }

    private static void printAvailableCommands() {
        consoleWriteLn("Available Commands:")
        consoleWriteLn("help - Display this list of available commands")
        consoleWriteLn("status - Get the current status of the listener")
        consoleWriteLn("start - Attempt to start the listener")
        consoleWriteLn("stop - Attempt to stop the listener")
        consoleWriteLn("send <command> - Send a command to the Arduino")
        consoleWriteLn("update <command> <value> - Update a command value")
        consoleWriteLn("gui - Open the GUI")
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
            // Using println here because the console tends to hold
            // onto output instead of displaying it immediately
            println string
        }
    }
}
