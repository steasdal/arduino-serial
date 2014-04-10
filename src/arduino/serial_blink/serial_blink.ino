int led = 13;                            // LED on pin 13 - this is what we'll be blinkin'
int blinkInterval = 500;                 // Initial blink interval in milliseconds

String serialString = "";                // String that'll hold incoming serial data.
boolean serialStringComplete = false;    // Set to true once an entire serial string is received.

int ledState = LOW;                      // The current LED state (LOW or HIGH)
long previousLedStateChange = 0;         // Stores the last time the LED state changed 
const String BLINK_INTERVAL_FLAG= "B";   // Serial messages that start with this string contain a blink interval

/* ***************************** */

const String COMMAND_START = "CMD";
const char COMMAND_SEPARATOR = ',';
const char DATA_SEPARATOR = ':';

const String BLINK = "BLINK";
const String MOTOR_01 = "MOTOR_01";
const String MOTOR_02 = "MOTOR_02";

/* ***************************** */

void setup() {                
  pinMode(led, OUTPUT);   
  Serial.begin(9600);  
}

void loop() {
  checkForBlink();
}

/* ********************************************************************************************* */

void setBlinkInterval(String intervalString) {
  if( intervalString.length() <= 0) {
    sendSerialMessage("Zero length blink interval string");
  } else {
    
    // Attempt to convert interval string to integer.  This method will return zero
    // if the string can't be parsed to an int (or if the string is "0", of course).
    int newInterval = intervalString.toInt();
    
    if( newInterval <= 0 ) {
      sendSerialMessage("Unacceptable blink interval: " + intervalString);
    } else {
      blinkInterval = newInterval;
      sendSerialMessage("Setting blink interval to: " + String(newInterval));
    }
  }
}

/*
 SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read(); 
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it.  Note
    // that we're NOT appending the newline character.
    if (inChar == '\n') {
      serialRouter(serialString);
      serialString = "";
    } else {
      // append inChar to the serialString
      serialString += inChar;
    }
  }
}

void serialRouter(String serialString) {
  if( serialString.startsWith( COMMAND_START ) ) {

    // TODO: update the flag indicating the last received update

    // If there's something after COMMAND_START, 
    // then we've got some actual update data!
    if( serialString.length() > COMMAND_START.length() ){
      String commandPlusSeparator = COMMAND_START + COMMAND_SEPARATOR;
      String commands = serialString.substring( commandPlusSeparator.length() );
      commandStringHandler( commands );
    }
  }
}

// This'll handle command strings which might look a little something like this:
// BLINK:1000
// BLINK:2500,MOTOR_01:255,MOTOR_02:255
// BLINK:500,MOTOR_01:50,MOTOR_02:50,SERVO_01:75,SERVO_02:120
void commandStringHandler(String commandString) {

  do {
    
    String command = "";
    int commandSeparatorIndex = commandString.indexOf( COMMAND_SEPARATOR );
    
    if( commandSeparatorIndex > 0 ) {
      command = commandString.substring( 0, commandSeparatorIndex );
      commandString = commandString.substring( (commandSeparatorIndex + 1), commandString.length() );
    } else {
      command = commandString;
      commandString = "";
    }
    
    // Handle the command!
    commandHandler(command);
    
  } while( commandString.length() > 0 );

}

// This'll handle individual commands with a command name and a value separated
// by the DATA_SEPARATOR character (e.g. BLINK:1000, MOTOR_01:255, etc.)
void commandHandler(String commandString) {
  int dataSeparatorIndex = commandString.indexOf( DATA_SEPARATOR );
  String command = commandString.substring(0, dataSeparatorIndex );
  String value = commandString.substring(dataSeparatorIndex + 1, commandString.length());  
  
  if( command == BLINK ) {
    blinkHandler(value);
  } else if ( command == MOTOR_01 ) {
    motor01Handler(value);
  } else if ( command == MOTOR_02 ) {
    motor02Handler(value);
  }
}

void blinkHandler(String blinkValue) {
  sendSerialMessage( "setting blink to " + blinkValue );
  setBlinkInterval(blinkValue);
}

void motor01Handler(String motorValue) {
  
}

void motor02Handler(String motorValue) {
  
}


// Your general purpose serial message sender.
void sendSerialMessage(String message) {
  Serial.println(message);
  Serial.flush();
} 

/* ********************************************************************************************* */

// Check to see if it's time to blink.  This method compares 
// the current millis to the last time the LED state was changed.
// If the elapsed number of milliseconds is greater than the
// blink interval, flip the LED state.
void checkForBlink() {
   unsigned long currentMillis = millis();
  
  if(currentMillis - previousLedStateChange > blinkInterval) {
      previousLedStateChange = currentMillis;
      switchLedState();
      digitalWrite(led, ledState);
  }
}

void switchLedState() {
  if(ledState == LOW) {
    ledState = HIGH;
  } else {
    ledState = LOW;
  }
}

