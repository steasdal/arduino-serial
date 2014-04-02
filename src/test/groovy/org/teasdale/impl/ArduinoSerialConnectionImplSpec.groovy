package org.teasdale.impl

import jssc.SerialPort
import org.teasdale.throwable.ArduinoSerialMethodOrderException
import spock.lang.Specification

class ArduinoSerialConnectionImplSpec extends Specification {

    ArduinoSerialConfigImpl arduinoSerialConfigImpl
    ArduinoSerialConnectionImpl arduinoSerialConnectionImpl
    def mockedSerialPort = Mock(SerialPort)

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
}
