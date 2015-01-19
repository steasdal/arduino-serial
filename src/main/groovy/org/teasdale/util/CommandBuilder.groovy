package org.teasdale.util

import org.teasdale.impl.ArduinoSerialCommand
import org.teasdale.impl.ArduinoSerialConfigImpl

class CommandBuilder {

    private static final String INIT_START = "I"
    private static final String COMMAND_START = "C"
    private static final String UPDATE_RATE = "UPDATE_RATE"
    private static final String MISSED_UPDATES_ALLOWED = "MISSED_UPDATES_ALLOWED"
    private static final String COMMAND_SEPARATOR = ","
    private static final String DATA_SEPARATOR = ":"
    private static final String NEWLINE = "\n"

    public static String buildUpdateFrequencyInitString(ArduinoSerialConfigImpl config) {
        return buildInitString(UPDATE_RATE, config.getUpdateFrequency().toString())
    }

    public static String buildMissedUpdatesAllowedInitString(ArduinoSerialConfigImpl config) {
        return buildInitString(MISSED_UPDATES_ALLOWED, config.getMissedUpdatesAllowed().toString())
    }

    private static String buildInitString(String initName, String initValue) {
        return INIT_START + COMMAND_SEPARATOR + initName + DATA_SEPARATOR + initValue + NEWLINE
    }

    public static Collection<String> buildCommandInitStrings(ArduinoSerialConfigImpl config) {
        Set<String> commandInitStrings = new HashSet<String>()

        synchronized( config.getCommands() ) {
            config.getCommands().each { String key, ArduinoSerialCommand command ->
                String commandInitString = INIT_START + COMMAND_SEPARATOR + command.name + DATA_SEPARATOR + command.initialValue.toString() + NEWLINE
                commandInitStrings.add commandInitString
            }
        }

        return commandInitStrings
    }

    public static String buildUpdateString(ArduinoSerialConfigImpl config) {
        StringBuffer stringBuffer = new StringBuffer(COMMAND_START)

        Map<String, ArduinoSerialCommand> commands = config.getCommands()

        // Iterating over entrySet() with a foreach loop uses a "weakly concurrent"
        // iterator which may help prevent previously seen threading issues.
        for(Map.Entry<String, ArduinoSerialCommand> entry: commands.entrySet()) {
            ArduinoSerialCommand command = entry.getValue()

            if(command.updatePending) {
                stringBuffer.append(COMMAND_SEPARATOR + command.name + DATA_SEPARATOR + command.currentValue.toString())
                command.updatePending = false
            }
        }

        /*
        synchronized( config.getCommands() ) {
            config.getCommands().each { String key, ArduinoSerialCommand command ->
                if(command.updatePending) {
                    stringBuffer.append(COMMAND_SEPARATOR + command.name + DATA_SEPARATOR + command.currentValue.toString())
                    command.updatePending = false
                }
            }
        }
        */

        stringBuffer.append(NEWLINE)
        return stringBuffer.toString()
    }
}
