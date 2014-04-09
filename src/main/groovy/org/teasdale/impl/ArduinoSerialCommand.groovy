package org.teasdale.impl

class ArduinoSerialCommand {
    public ArduinoSerialCommand(String name, int initialValue) {
        this.name = name
        this.initialValue = initialValue
        this.currentValue = currentValue
        this.updatePending = false
    }

    String name
    int initialValue
    int currentValue
    boolean updatePending

    public void updateValue(int value) {
        currentValue = value
        updatePending = true
    }

    public void updateSent() {
        updatePending = false
    }
}
