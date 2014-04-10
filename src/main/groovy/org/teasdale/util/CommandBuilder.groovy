package org.teasdale.util

import org.teasdale.impl.ArduinoSerialCommand
import org.teasdale.impl.ArduinoSerialConfigImpl

class CommandBuilder {

    private static final String INIT_START = "INIT"
    private static final String COMMAND_START = "CMD"
    private static final String COMMAND_SEPARATOR = ","
    private static final String DATA_SEPARATOR = ":"
    private static final String NEWLINE = "\n"

    public static String buildUpdateString(ArduinoSerialConfigImpl config) {
        StringBuffer stringBuffer = new StringBuffer(COMMAND_START)

        synchronized( config.getCommands() ) {
            config.getCommands().each { String key, ArduinoSerialCommand command ->
                if(command.updatePending) {
                    stringBuffer.append(COMMAND_SEPARATOR + command.name + DATA_SEPARATOR + command.currentValue.toString())
                    command.updatePending = false
                }
            }
        }

        stringBuffer.append(NEWLINE)

        return stringBuffer.toString()
    }
}
