package org.teasdale.impl

import org.teasdale.throwable.ArduinoSerialMethodOrderException
import spock.lang.Specification

class ArduinoSerialConnectionImplSpec extends Specification {

    ArduinoSerialConfigImpl arduinoSerialConfigImpl
    ArduinoSerialConnectionImpl arduinoSerialConnectionImpl

    def setup() {
        arduinoSerialConfigImpl = new ArduinoSerialConfigImpl()
        arduinoSerialConnectionImpl = new ArduinoSerialConnectionImpl(arduinoSerialConfigImpl)
    }

    def "Verify that the verifyOpenState() doesn't throw an exception when the proper state is encountered"() {
        setup:
        arduinoSerialConnectionImpl.serialState = ArduinoSerialConnectionImpl.SerialState.UNOPENED

        when:
        arduinoSerialConnectionImpl.verifyOpenState()

        then:
        notThrown(ArduinoSerialMethodOrderException)
    }

    def "Verify that verifyOpenState() throws the expected exception when it encounters an unacceptable state"() {
        setup:
        arduinoSerialConnectionImpl.serialState = serialState

        when:
        arduinoSerialConnectionImpl.verifyOpenState()

        then:
        thrown(ArduinoSerialMethodOrderException)

        where:
        serialState << [ArduinoSerialConnectionImpl.SerialState.OPENED, ArduinoSerialConnectionImpl.SerialState.CLOSED]
    }

    def "Verify that the serialState is set properly when the setStateOpened() method is called"() {
        setup: "make sure the current serialState is set to UNOPENED"
        arduinoSerialConnectionImpl.serialState = ArduinoSerialConnectionImpl.SerialState.UNOPENED

        when: "we call setStateOpened()"
        arduinoSerialConnectionImpl.setStateOpened()

        then: "we verify that serialState is set to OPENED"
        arduinoSerialConnectionImpl.serialState == ArduinoSerialConnectionImpl.SerialState.OPENED
    }

    def "Verify that the verifyWriteState() method does not throw an exception when the proper state is encountered"() {
        setup:
        arduinoSerialConnectionImpl.serialState = ArduinoSerialConnectionImpl.SerialState.OPENED

        when:
        arduinoSerialConnectionImpl.verifyWriteState()

        then:
        notThrown(ArduinoSerialMethodOrderException)
    }

    def "Verify that the verifyWriteState() method throws the expected exception when it encounters an unacceptable state"() {
        setup:
        arduinoSerialConnectionImpl.serialState = serialState

        when:
        arduinoSerialConnectionImpl.verifyWriteState()

        then:
        thrown(ArduinoSerialMethodOrderException)

        where:
        serialState << [ArduinoSerialConnectionImpl.SerialState.UNOPENED, ArduinoSerialConnectionImpl.SerialState.CLOSED]
    }

    def "Verify that validateByteArray does not throw an exception when passed a valid byte array"() {
        setup: "whip up a valid byte array"
        byte[] byteArray = "I'm a valid string!".getBytes()

        when: "call the validateByteArray method"
        arduinoSerialConnectionImpl.validateByteArray(byteArray)

        then: "all's well"
        notThrown(IllegalArgumentException)
    }

    def "Verify that validateByteArray method throws an exception when passed a bad byte array"() {
        when: "call the validateByteArray and pass it a bad byte array"
        arduinoSerialConnectionImpl.validateByteArray(byteArray)

        then: "exception time baby!"
        thrown(exception)

        where: "we setup a matrix of bad byte array types (null, empty) and their corresponding exceptions"
        byteArray   | exception
        null        | NullPointerException
        new byte[0] | IllegalArgumentException
    }

    def "Verify that verifyCloseState is cool when SerialState is OPENED"() {
        setup:
        arduinoSerialConnectionImpl.serialState = ArduinoSerialConnectionImpl.SerialState.OPENED

        when:
        arduinoSerialConnectionImpl.verifyCloseState()

        then:
        notThrown(ArduinoSerialMethodOrderException)
    }

    def "Verify that verifyCloseState thrown an exception when SerialState is not OPENED"() {
        setup:
        arduinoSerialConnectionImpl.serialState = serialState

        when:
        arduinoSerialConnectionImpl.verifyCloseState()

        then:
        thrown(ArduinoSerialMethodOrderException)

        where:
        serialState << [ArduinoSerialConnectionImpl.SerialState.UNOPENED, ArduinoSerialConnectionImpl.SerialState.CLOSED]
    }

    def "Verify that setStateClosed does, indeed, set serialState to CLOSED"() {
        setup: "make sure the current serialState is set to OPENED"
        arduinoSerialConnectionImpl.serialState = ArduinoSerialConnectionImpl.SerialState.OPENED

        when: "we call setStateClosed()"
        arduinoSerialConnectionImpl.setStateClosed()

        then: "we verify that serialState is set to CLOSED"
        arduinoSerialConnectionImpl.serialState == ArduinoSerialConnectionImpl.SerialState.CLOSED
    }
}
