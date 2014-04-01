package org.teasdale.api;

public interface ArduinoSerialConfig {

    public static final String DEFAULT_PORTNAME = "COM3";
    public static final Baudrate DEFAULT_BAUD_RATE = Baudrate.BAUDRATE_9600;
    public static final Databits DEFAULT_DATA_BITS = Databits.DATABITS_8;
    public static final Parity DEFAULT_PARITY = Parity.NONE;
    public static final Stopbits DEFAULT_STOP_BITS = Stopbits.ONE;

    /**
     * Baud Rate Enum - represents the intersection of the sets of
     * baud rates supported by the Arduino UNO and the serial library.
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

        public int value() {
            return value;
        }
    }

    /**
     * Data Bits Enum
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

        public int value() {
            return value;
        }
    }

    /**
     * Parity Enum
     */
    public enum Parity { NONE, EVEN, ODD }

    /**
     * Stop Bits Enum
     */
    public enum Stopbits { ONE, TWO }

    /* ************************************************************************************************************* */

    public void setPortname(String newPortname);
    public void setBaudrate(Baudrate newBaudrate);
    public void setDatabits(Databits newDatabits);
    public void setParity(Parity newParity);
    public void setStopbits(Stopbits newStopbits);
    public void registerListener(ArduinoSerialListener listener);
}
