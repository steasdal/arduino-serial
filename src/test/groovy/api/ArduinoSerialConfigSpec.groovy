package api

import org.teasdale.api.ArduinoSerialConfig
import org.teasdale.api.ArduinoSerialFactory
import spock.lang.Specification

class ArduinoSerialConfigSpec extends Specification {
    def "Verify that Baudrate.parse() methods return the expected enumeration values"() {
        expect: "The parsed input results and the expected output"
        ArduinoSerialConfig.Baudrate.parse(input) == output

        where:
        input               | output

        300                 | ArduinoSerialConfig.Baudrate.BAUDRATE_300
        new Integer(300)    | ArduinoSerialConfig.Baudrate.BAUDRATE_300
        "300"               | ArduinoSerialConfig.Baudrate.BAUDRATE_300

        600                 | ArduinoSerialConfig.Baudrate.BAUDRATE_600
        new Integer(600)    | ArduinoSerialConfig.Baudrate.BAUDRATE_600
        "600"               | ArduinoSerialConfig.Baudrate.BAUDRATE_600

        1200                | ArduinoSerialConfig.Baudrate.BAUDRATE_1200
        new Integer(1200)   | ArduinoSerialConfig.Baudrate.BAUDRATE_1200
        "1200"              | ArduinoSerialConfig.Baudrate.BAUDRATE_1200

        4800                | ArduinoSerialConfig.Baudrate.BAUDRATE_4800
        new Integer(4800)   | ArduinoSerialConfig.Baudrate.BAUDRATE_4800
        "4800"              | ArduinoSerialConfig.Baudrate.BAUDRATE_4800

        9600                | ArduinoSerialConfig.Baudrate.BAUDRATE_9600
        new Integer(9600)   | ArduinoSerialConfig.Baudrate.BAUDRATE_9600
        "9600"              | ArduinoSerialConfig.Baudrate.BAUDRATE_9600

        14400               | ArduinoSerialConfig.Baudrate.BAUDRATE_14400
        new Integer(14400)  | ArduinoSerialConfig.Baudrate.BAUDRATE_14400
        "14400"             | ArduinoSerialConfig.Baudrate.BAUDRATE_14400

        19200               | ArduinoSerialConfig.Baudrate.BAUDRATE_19200
        new Integer(19200)  | ArduinoSerialConfig.Baudrate.BAUDRATE_19200
        "19200"             | ArduinoSerialConfig.Baudrate.BAUDRATE_19200

        38400               | ArduinoSerialConfig.Baudrate.BAUDRATE_38400
        new Integer(38400)  | ArduinoSerialConfig.Baudrate.BAUDRATE_38400
        "38400"             | ArduinoSerialConfig.Baudrate.BAUDRATE_38400

        57600               | ArduinoSerialConfig.Baudrate.BAUDRATE_57600
        new Integer(57600)  | ArduinoSerialConfig.Baudrate.BAUDRATE_57600
        "57600"             | ArduinoSerialConfig.Baudrate.BAUDRATE_57600

        115200              | ArduinoSerialConfig.Baudrate.BAUDRATE_115200
        new Integer(115200) | ArduinoSerialConfig.Baudrate.BAUDRATE_115200
        "115200"            | ArduinoSerialConfig.Baudrate.BAUDRATE_115200
    }

    def "Verify that Baudrate.parse() methods throw the expected exceptions when fed bad arguments"() {
        when: "We feed bad input to the parse() methods"
        ArduinoSerialConfig.Baudrate.parse(input)

        then: "We get the expected exception"
        thrown(exception)

        where:
        input             | exception

        null              | NullPointerException
        ""                | IllegalArgumentException

        "not-an-int"      | IllegalArgumentException
        1.005             | MissingMethodException
        "9600.0"          | IllegalArgumentException

        0                 | IllegalArgumentException
        "0"               | IllegalArgumentException
        new Integer(0)    | IllegalArgumentException

        -1                | IllegalArgumentException
        "-1"              | IllegalArgumentException
        new Integer(-1)   | IllegalArgumentException

        100               | IllegalArgumentException
        "100"             | IllegalArgumentException
        new Integer(100)  | IllegalArgumentException

        9601              | IllegalArgumentException
        "9601"            | IllegalArgumentException
        new Integer(9601) | IllegalArgumentException
    }

    def "Verify that Databits.parse() methods return the expected enumerations"() {
        expect: "Good input results in good output"

        ArduinoSerialConfig.Databits.parse(input) == output

        where:
        input          | output

        5              | ArduinoSerialConfig.Databits.DATABITS_5
        new Integer(5) | ArduinoSerialConfig.Databits.DATABITS_5
        "5"            | ArduinoSerialConfig.Databits.DATABITS_5

        6              | ArduinoSerialConfig.Databits.DATABITS_6
        new Integer(6) | ArduinoSerialConfig.Databits.DATABITS_6
        "6"            | ArduinoSerialConfig.Databits.DATABITS_6

        7              | ArduinoSerialConfig.Databits.DATABITS_7
        new Integer(7) | ArduinoSerialConfig.Databits.DATABITS_7
        "7"            | ArduinoSerialConfig.Databits.DATABITS_7

        8              | ArduinoSerialConfig.Databits.DATABITS_8
        new Integer(8) | ArduinoSerialConfig.Databits.DATABITS_8
        "8"            | ArduinoSerialConfig.Databits.DATABITS_8
    }

    def "Verity that Databits.parse() methods throw the expected exceptions when fed bad arguments"() {
        when: "We feed bad input to the parse() methods"
        ArduinoSerialConfig.Databits.parse(input)

        then: "We get the expected exception"
        thrown(exception)

        where:
        input                 | exception

        null                  | NullPointerException
        ""                    | IllegalArgumentException

        "cannot-parse-to-int" | IllegalArgumentException
        8.0                   | MissingMethodException
        "8.0"                 | IllegalArgumentException

        -1                    | IllegalArgumentException
        "-1"                  | IllegalArgumentException
        new Integer(-1)       | IllegalArgumentException

        4                     | IllegalArgumentException
        "4"                   | IllegalArgumentException
        new Integer(4)        | IllegalArgumentException

        9                     | IllegalArgumentException
        "9"                   | IllegalArgumentException
        new Integer(9)        | IllegalArgumentException
    }

    def "Verify that the Parity.parse() method returns the expected enumeration values"() {
        expect: "Good input results in good output"

        ArduinoSerialConfig.Parity.parse(input) == output

        where:
        input  | output

        "none" | ArduinoSerialConfig.Parity.NONE
        "None" | ArduinoSerialConfig.Parity.NONE
        "NONE" | ArduinoSerialConfig.Parity.NONE

        "even" | ArduinoSerialConfig.Parity.EVEN
        "Even" | ArduinoSerialConfig.Parity.EVEN
        "EVEN" | ArduinoSerialConfig.Parity.EVEN

        "odd"  | ArduinoSerialConfig.Parity.ODD
        "Odd"  | ArduinoSerialConfig.Parity.ODD
        "ODD"  | ArduinoSerialConfig.Parity.ODD
    }

    def "Verify that the Parity.parse() method throws the expected exceptions when fed bad arguments"() {
        when: "We feed bad input to the parse() method"
        ArduinoSerialConfig.Parity.parse(input)

        then: "We get the expected exception"
        thrown(exception)

        where:
        input   | exception

        null    | NullPointerException
        ""      | IllegalArgumentException

        "hello" | IllegalArgumentException
        "enone" | IllegalArgumentException
        "none7" | IllegalArgumentException
        "noon"  | IllegalArgumentException
        "evern" | IllegalArgumentException
        "oddd"  | IllegalArgumentException
        "oodd"  | IllegalArgumentException
    }

    def "Verify that the Stopbits.parse() method returns the expected enumeration values"() {
        expect: "Good input results in good output"

        ArduinoSerialConfig.Stopbits.parse(input) == output

        where:
        input          | output

        "one"          | ArduinoSerialConfig.Stopbits.ONE
        "1"            | ArduinoSerialConfig.Stopbits.ONE
        1              | ArduinoSerialConfig.Stopbits.ONE
        new Integer(1) | ArduinoSerialConfig.Stopbits.ONE

        "two"          | ArduinoSerialConfig.Stopbits.TWO
        "2"            | ArduinoSerialConfig.Stopbits.TWO
        2              | ArduinoSerialConfig.Stopbits.TWO
        new Integer(2) | ArduinoSerialConfig.Stopbits.TWO
    }

    def "Verify that the Stopbits.parse() method throws the expected exceptions when fed bad arguments"() {
        when: "We feed bad input to the parse() method"
        ArduinoSerialConfig.Stopbits.parse(input)

        then: "We get the expected exception"
        thrown(exception)

        where:
        input            | exception

        null             | NullPointerException
        ""               | IllegalArgumentException

        "string-not-int" | IllegalArgumentException
        8.0              | MissingMethodException
        "1.0"            | IllegalArgumentException

        -1               | IllegalArgumentException
        "-1"             | IllegalArgumentException
        "negative one"   | IllegalArgumentException
        "-one"           | IllegalArgumentException
        new Integer(-1)  | IllegalArgumentException

        3                | IllegalArgumentException
        "3"              | IllegalArgumentException
        "three"          | IllegalArgumentException
        new Integer(3)   | IllegalArgumentException
    }

    def "Verify that the setUpdateFrequency() method takes the expected range of values without throwing an exception"() {
        when: "We call the setUpdateFrequency method"
        ArduinoSerialFactory.getInstance().getArduinoSerialConfig().setUpdateFrequency(frequency)

        then: "No exception is thrown"
        notThrown(IllegalArgumentException)

        where:
        frequency << (ArduinoSerialConfig.MINIMUM_UPDATE_FREQUENCY..ArduinoSerialConfig.MAXIMUM_UPDATE_FREQUENCY)
    }

    def "Verify that the setUpdateFrequecy() method throws the expected exception when fed values outside of the acceptable range"() {
        when: "We call the setUpdateFrequency method with some bad values"
        ArduinoSerialFactory.getInstance().getArduinoSerialConfig().setUpdateFrequency(frequency)

        then: "We get the dreaded IllegalArgumentException"
        thrown(IllegalArgumentException)

        where:
        frequency << [-1, 0, 4, 21, 42, 10000000]
    }

    def "Verify that the setMissedUpdatesAllowed() method accepts numbers within the expected range without throwing an exception"() {
        when: "We call the setMissedUpdatesAllowed method with good values"
        ArduinoSerialFactory.getInstance().getArduinoSerialConfig().setMissedUpdatesAllowed(missedUpdatesAllowed)

        then:
        notThrown(IllegalArgumentException)

        where:
        missedUpdatesAllowed << (ArduinoSerialConfig.MINIMUM_MISSED_UPDATES_ALLOWED..ArduinoSerialConfig.MAXIMUM_MISSED_UPDATES_ALLOWED)
    }

    def "Verify that the setMissedUpdatesAllowed() method throws the expected exception when fed some unnaceptable values"() {
        when: "The setMissedUpdatesAllowed method is passed some invalid values"
        ArduinoSerialFactory.getInstance().getArduinoSerialConfig().setMissedUpdatesAllowed(missedUpdatesAllowed)

        then:
        thrown(IllegalArgumentException)

        where:
        missedUpdatesAllowed << [-1, 0, 1, 2, 101, 1000, 10000]
    }


}
