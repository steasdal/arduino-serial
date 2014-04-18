package org.teasdale.api;

import org.apache.commons.lang3.Validate;

/**
 * A container for all options required to configure an {@link ArduinoSerialConnection}
 */
public interface ArduinoSerialConfig {

    /**
     * Default Port Name ("COM3")
     */
    public static final String DEFAULT_PORTNAME = "COM3";

    /**
     * Default Baud Rate ({@link Baudrate#BAUDRATE_9600})
     */
    public static final Baudrate DEFAULT_BAUD_RATE = Baudrate.BAUDRATE_9600;

    /**
     * Default Data Bits ({@link Databits#DATABITS_8})
     */
    public static final Databits DEFAULT_DATA_BITS = Databits.DATABITS_8;

    /**
     * Default Parity ({@link Parity#NONE})
     */
    public static final Parity DEFAULT_PARITY = Parity.NONE;

    /**
     * Default Stop Bits ({@link Stopbits#ONE})
     */
    public static final Stopbits DEFAULT_STOP_BITS = Stopbits.ONE;

    /**
     * Default Update Frequency (16 updates per second)
     */
    public static final int DEFAULT_UPDATE_FREQUENCY = 16;

    /**
     * Minimum Update Frequency (5 updates per second)
     */
    public static final int MINIMUM_UPDATE_FREQUENCY = 5;

    /**
     * Maximum Update Frequency (20 updates per second)
     */
    public static final int MAXIMUM_UPDATE_FREQUENCY = 20;

    /**
     * Default Missed Updates Allowed (8 missed updates)
     */
    public static final int DEFAULT_MISSED_UPDATES_ALLOWED = 4;

    /**
     * Maximum Missed Updates Allowed (100 updates)
     */
    public static final int MAXIMUM_MISSED_UPDATES_ALLOWED = 100;

    /**
     * Minimum Missed Updates Allowed (3 updates)
     */
    public static final int MINIMUM_MISSED_UPDATES_ALLOWED = 3;

    /* ************************************************************************************************************* */

    /**
     * Enumeration of possible Baud Rate values - represents the intersection of the sets
     * of baud rates supported by the Arduino UNO and the underlying serial library.
     */
    public enum Baudrate {
        BAUDRATE_300 (300),
        BAUDRATE_600 (600),
        BAUDRATE_1200 (1200),
        BAUDRATE_4800 (4800),
        BAUDRATE_9600 (9600),
        BAUDRATE_14400 (14400),
        BAUDRATE_19200 (19200),
        BAUDRATE_38400 (38400),
        BAUDRATE_57600 (57600),
        BAUDRATE_115200 (115200);

        private final int value;

        Baudrate(int value) {
            this.value = value;
        }

        /**
         * @return the integer baud rate value represented by the enum constant
         */
        public int value() {
            return value;
        }

        public static Baudrate parse(String baudrate) {
            Validate.notNull(baudrate, "baudrate argument cannot be null");

            try {
                return parse( Integer.parseInt(baudrate) );
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("unable to parse baudrate string to integer: " + baudrate );
            }
        }

        public static Baudrate parse(Integer baudrate) {
            Validate.notNull(baudrate, "baudrate argument cannot be null");
            return parse( baudrate.intValue() );
        }

        public static Baudrate parse(int baudrate) {
            Validate.notNull(baudrate, "baudrate argument cannot be null");

            switch( baudrate ) {
                case 300: return BAUDRATE_300;
                case 600: return BAUDRATE_600;
                case 1200: return BAUDRATE_1200;
                case 4800: return BAUDRATE_4800;
                case 9600: return BAUDRATE_9600;
                case 14400: return BAUDRATE_14400;
                case 19200: return BAUDRATE_19200;
                case 38400: return BAUDRATE_38400;
                case 57600: return BAUDRATE_57600;
                case 115200: return BAUDRATE_115200;
                default: throw new IllegalArgumentException("unrecognized baudrate argument: " + Integer.toString(baudrate) );
            }
        }
    }

    /**
     * Enumeration of possible Data Bits values
     */
    public enum Databits {
        DATABITS_5 (5),
        DATABITS_6 (6),
        DATABITS_7 (7),
        DATABITS_8 (8);

        private final int value;

        Databits(int value) {
            this.value = value;
        }

        /**
         * @return the integer stop bits value represented by the enum constant
         */
        public int value() {
            return value;
        }

        public static Databits parse(String databits) {
            Validate.notNull(databits, "databits argument cannot be null");

            try {
                return parse( Integer.parseInt(databits) );
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("unable to parse databits string to integer: " + databits );
            }
        }

        public static Databits parse(Integer databits) {
            Validate.notNull(databits, "databits argument cannot be null");
            return parse( databits.intValue() );
        }

        public static Databits parse(int databits) {
            Validate.notNull(databits, "databits argument cannot be null");

            switch( databits ) {
                case 5: return DATABITS_5;
                case 6: return DATABITS_6;
                case 7: return DATABITS_7;
                case 8: return DATABITS_8;
                default: throw new IllegalArgumentException("unrecognized databits argument: " + Integer.toString(databits) );
            }
        }
    }

    /**
     * Enumeration of possible Parity values
     */
    public enum Parity {
        NONE,
        EVEN,
        ODD;

        public static Parity parse(String parity) {
            Validate.notEmpty(parity, "parity argument cannot be null");

            if( parity.trim().matches("[Nn][Oo][Nn][Ee]") ) {
                return NONE;
            } else if( parity.trim().matches("[Ee][Vv][Ee][Nn]") ) {
                return EVEN;
            } else if( parity.trim().matches("[Oo][Dd][Dd]") ) {
                return ODD;
            } else {
                throw new IllegalArgumentException("unrecognized parity argument: " + parity );
            }
        }
    }

    /**
     * Enumeration of possible Stop Bits values - represents the intersection of the sets of
     * stop bits values supported by the Arduino UNO and the underlying serial library.
     */
    public enum Stopbits {
        ONE,
        TWO;

        public static Stopbits parse( String stopbits ) {
            Validate.notEmpty(stopbits, "stopbits argument cannot be null");

            if( stopbits.trim().matches("[Oo][Nn][Ee]") | stopbits.trim().matches("1") ) {
                return ONE;
            } else if ( stopbits.trim().matches("[Tt][Ww][Oo]") | stopbits.trim().matches("2") ) {
                return TWO;
            } else {
                throw new IllegalArgumentException("unrecognized stopbits argument: " + stopbits );
            }
        }

        public static Stopbits parse( Integer stopbits ) {
            Validate.notNull(stopbits, "stopbits argument cannot be null");
            return parse( stopbits.intValue() );
        }

        public static Stopbits parse( int stopbits ) {
            Validate.notNull(stopbits, "stopbits argument cannot be null");

            if( stopbits == 1 ) {
                return ONE;
            } else if (stopbits == 2 ) {
                return TWO;
            } else {
                throw new IllegalArgumentException("unrecognized stopbits argument: " + Integer.toString(stopbits) );
            }
        }
    }

    /* ************************************************************************************************************* */

    /**
     * Set the name of the port to connect to (e.g. "COM3", "/dev/ttyS0", "/dev/ttyUSB0", etc.)
     * <br><br>
     * Defaults to {@link #DEFAULT_PORTNAME}
     *
     * @param newPortname The desired port name
     */
    public void setPortname(String newPortname);

    /**
     * @return the current port name value
     */
    public String getPortname();

    /**
     * Set the baud rate of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_BAUD_RATE}
     *
     * @param newBaudrate The desired baud rate
     */
    public void setBaudrate(Baudrate newBaudrate);

    /**
     * @return the current badu rate value
     */
    public Baudrate getBaudrate();

    /**
     * Set the data bits of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_DATA_BITS}
     *
     * @param newDatabits The desired data bits
     */
    public void setDatabits(Databits newDatabits);

    /**
     * @return the current data bits value
     */
    public Databits getDatabits();

    /**
     * Set the parity of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_PARITY}
     *
     * @param newParity The desired parity
     */
    public void setParity(Parity newParity);

    /**
     * @return the current parity value
     */
    public Parity getParity();

    /**
     * Set the stop bits of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_STOP_BITS}
     *
     * @param newStopbits The desired stop bits
     */
    public void setStopbits(Stopbits newStopbits);

    /**
     * @return the current stop bits value
     */
    public Stopbits getStopbits();

    /**
     * Set the update frequency (in updates per second) - must be between
     * {@link #MINIMUM_UPDATE_FREQUENCY} and {@link #MAXIMUM_UPDATE_FREQUENCY}
     * <br><br>
     * Defaults to {@link #DEFAULT_UPDATE_FREQUENCY}
     *
     * @param updateFrequency The desired update frequency
     */
    public void setUpdateFrequency(int updateFrequency);

    /**
     * @return the current update frequency value
     */
    public int getUpdateFrequency();

    /**
     * Set the maximum number of missed updates allowed before all
     * commands are reset to their initial values - must be between
     * {@link #MAXIMUM_MISSED_UPDATES_ALLOWED} and {@link #MINIMUM_MISSED_UPDATES_ALLOWED}
     *
     * @param missedUpdatesAllowed The desired number of missed updates allowed
     */
    public void setMissedUpdatesAllowed(int missedUpdatesAllowed);

    /**
     * @return the current maximum number of missed udpates allowed
     */
    public int getMissedUpdatesAllowed();

    /**
     * Register a command.
     *
     * @param commandName The name of the command to register
     * @param initialValue The initial value of the command.  This is
     *                     the value that the command will revert to
     *                     if the serial connection is lost.
     */
    public void registerCommand(String commandName, int initialValue);

    /**
     * @return a String array containing the names of all currently
     * registered commands.
     */
    public String[] getRegisteredCommands();

    /**
     * Register an instance of {@link ArduinoSerialListener} to receive
     * serial responses from the Arduino.
     *
     * @param listener An implementation of the
     * {@link ArduinoSerialListener} interface.
     */
    public void registerListener(ArduinoSerialListener listener);
}
