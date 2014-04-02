package org.teasdale.api;

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
    }

    /**
     * Enumeration of possible Parity values
     */
    public enum Parity { NONE, EVEN, ODD }

    /**
     * Enumeration of possible Stop Bits values - represents the intersection of the sets of
     * stop bits values supported by the Arduino UNO and the underlying serial library.
     */
    public enum Stopbits { ONE, TWO }

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
     * Set the baud rate of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_BAUD_RATE}
     *
     * @param newBaudrate The desired baud rate
     */
    public void setBaudrate(Baudrate newBaudrate);

    /**
     * Set the data bits of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_DATA_BITS}
     *
     * @param newDatabits The desired data bits
     */
    public void setDatabits(Databits newDatabits);

    /**
     * Set the parity of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_PARITY}
     *
     * @param newParity The desired parity
     */
    public void setParity(Parity newParity);

    /**
     * Set the stop bits of the serial connection
     * <br><br>
     * Defaults to {@link #DEFAULT_STOP_BITS}
     *
     * @param newStopbits The desired stop bits
     */
    public void setStopbits(Stopbits newStopbits);

    /**
     * Register an instance of {@link ArduinoSerialListener} to receive
     * serial responses from the Arduino.
     *
     * @param listener An implementation of the
     * {@link ArduinoSerialListener} interface.
     */
    public void registerListener(ArduinoSerialListener listener);

















































}
