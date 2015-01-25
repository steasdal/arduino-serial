package org.teasdale.util

import org.teasdale.impl.ArduinoSerialConfigImpl

import java.util.concurrent.ConcurrentHashMap

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

        Map<String, Integer> updateValues = config.getUpdateValues()

        for(Map.Entry<String, Integer> updateValue: updateValues.entrySet()) {
            String commandInitString = INIT_START + COMMAND_SEPARATOR + updateValue.getKey() + DATA_SEPARATOR + updateValue.getValue().toString() + NEWLINE
            commandInitStrings.add commandInitString
        }

        return commandInitStrings
    }

    public static String buildUpdateString(ArduinoSerialConfigImpl config) {
        StringBuffer stringBuffer = new StringBuffer(COMMAND_START)

        ConcurrentHashMap<String, Integer> updateValues = config.getUpdateValues()
        ConcurrentHashMap<String, Boolean> updateFlags = config.getUpdateFlags()

        for(Map.Entry<String, Boolean> updateFlag: updateFlags.entrySet()) {
            if(updateFlag.getValue() == Boolean.TRUE) {
                Integer updateValue = updateValues.get(updateFlag.getKey())

                stringBuffer.append(COMMAND_SEPARATOR + updateFlag.getKey() + DATA_SEPARATOR + updateValue.toString())
                updateFlags.replace(updateFlag.getKey(), Boolean.TRUE, Boolean.FALSE)
            }
        }

        stringBuffer.append(NEWLINE)
        return stringBuffer.toString()
    }
}
