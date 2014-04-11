/* ******* INIT DEFAULTS ****** */

const int MISSED_UPDATES_ALLOWED_DEFAULT = 5;
const int UPDATE_RATE_DEFAULT = 10;

const int BLINK_INIT_DEFAULT = 1000;
const int MOTOR_01_INIT_DEFAULT = 0;
const int MOTOR_02_INIT_DEFAULT = 0;
const int SERVO_01_INIT_DEFAULT = 0;
const int SERVO_02_INIT_DEFAULT = 0;

/* ******** INIT VALUES ******** */

int missedUpdatesAllowed = MISSED_UPDATES_ALLOWED_DEFAULT;
int updateRate = UPDATE_RATE_DEFAULT;

int blinkIntervalInit = BLINK_INIT_DEFAULT;
int motor01Init = MOTOR_01_INIT_DEFAULT;
int motor02Init = MOTOR_02_INIT_DEFAULT;
int servo01Init = SERVO_01_INIT_DEFAULT;
int servo02Init = SERVO_02_INIT_DEFAULT;

/* ******* CURRENT VALUES ****** */

unsigned long lastUpdate = 0;           // The last time an update was received
boolean connectionLive = false;         // Is this connection currently live?

int blinkInterval = blinkIntervalInit;
int motor01 = motor01Init;
int motor02 = motor02Init;
int servo01 = servo01Init;
int servo02 = servo02Init;

/* ***** BLINK VARIABLES ****** */

int led = 13;                            // This is the pin on which we'll be blinking an LED
int ledState = LOW;                      // The current LED state (LOW or HIGH)
long previousLedStateChange = 0;         // Stores the last time the LED state changed 
const String BLINK_INTERVAL_FLAG= "B";   // Serial messages that start with this string contain a blink interval

/* ***************************** */

void setup() {                
  pinMode(led, OUTPUT);   
  Serial.begin(9600);  
}

void loop() {
  checkForUpdateExpiration();
  checkForBlink();
}

/* ********************************************************************************************* */

// An update was received.  Update the lastUpdate counter.
void resetLastUpdate() {
  lastUpdate = millis();
  connectionLive = true;
}

// Check to see if we've exceedeed the time limit between updates.  If we 
// haven't seen an update for a while, reset all values to their defaults.
void checkForUpdateExpiration() {
  if( connectionLive ) {
    // First off, calculate the number of milliseconds that are allowed
    // to elapse before we consider the connection as timed out.
    unsigned long millisecondsBetweenUpdates = 1000 / updateRate;
    unsigned long millisecondsBeforeExpiration = millisecondsBetweenUpdates * missedUpdatesAllowed;
    
    // If the current time minus the previous update time is greater
    // than the number of milliseconds that have elapsed since the last
    // update, we've got an expired connection.
    unsigned long currentMillis = millis();
    
    if( currentMillis - lastUpdate > millisecondsBeforeExpiration ) {
      sendSerialMessage("Expired connection - Setting all values to their defaults");
      connectionLive = false;
    }
  }
}

void resetAllDefaults() {
  setBlinkInterval(blinkIntervalInit);
  setMotor01(motor01Init);
  setMotor02(motor02Init);
  setServo01(servo01Init);
  setServo02(servo02Init);
}

/* ********************************************************************************************* */

void setBlinkInterval(int newBlinkInterval) {
  if( newBlinkInterval <= 0 ) {
    sendSerialMessage("Negative blink value received - setting blink interval to its default");
    blinkInterval = blinkIntervalInit;
  } else {
    blinkInterval = newBlinkInterval;
    sendSerialMessage("Setting blink interval to: " + String(newBlinkInterval));
  }
}

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

/* ********************************************************************************************* */

void setMotor01(int motorValue) {
  motor01 = motorValue;
  sendSerialMessage("Setting motor01 to: " + motorValue);
}

void setMotor02(int motorValue) {
  motor02 = motorValue;
  sendSerialMessage("Setting motor02 to: " + motorValue);
}

void setServo01(int servoValue) {
  servo01 = servoValue;
  sendSerialMessage("Setting servo01 to: " + servoValue);
}

void setServo02(int servoValue) {
  servo02 = servoValue;
  sendSerialMessage("Setting servo02 to: " + servoValue);
}

/* ***********************************************************************************************
                       _       _                  _       _                  _       _ 
         ___  ___ _ __(_) __ _| |   ___  ___ _ __(_) __ _| |   ___  ___ _ __(_) __ _| |
        / __|/ _ \ '__| |/ _` | |  / __|/ _ \ '__| |/ _` | |  / __|/ _ \ '__| |/ _` | |
        \__ \  __/ |  | | (_| | |  \__ \  __/ |  | | (_| | |  \__ \  __/ |  | | (_| | |
        |___/\___|_|  |_|\__,_|_|  |___/\___|_|  |_|\__,_|_|  |___/\___|_|  |_|\__,_|_|
     

************************************************************************************************ */

const String COMMAND_START = "CMD";
const String INIT_START = "INIT";
const char   COMMAND_SEPARATOR = ',';
const char   DATA_SEPARATOR = ':';

const String MISSED_UPDATES_ALLOWED = "MISSED_UPDATES_ALLOWED";
const String UPDATE_RATE = "UPDATE_RATE";

const String BLINK = "BLINK";
const String MOTOR_01 = "MOTOR_01";
const String MOTOR_02 = "MOTOR_02";
const String SERVO_01 = "SERVO_01";
const String SERVO_02 = "SERVO_02";

String serialString = "";                // String that'll hold incoming serial data.
boolean serialStringComplete = false;    // Set to true once an entire serial string is received.

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

    // Update the variable that holds the time of last update
    resetLastUpdate();

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
// BLINK:2500,MOTOR_01:255
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

    commandHandler(command);
    
  } while( commandString.length() > 0 );
}

// This'll handle individual commands with a command name and a value separated
// by the DATA_SEPARATOR character (e.g. BLINK:1000, MOTOR_01:255, etc.)
void commandHandler(String commandString) {
  int dataSeparatorIndex = commandString.indexOf( DATA_SEPARATOR );
  String command = commandString.substring(0, dataSeparatorIndex );
  String value = commandString.substring(dataSeparatorIndex + 1, commandString.length());  
  
  int intValue = strToInt(value);
  
  if( command.equals(BLINK) ) {
    sendSerialMessage("command = BLINK");
    blinkHandler(intValue);
  } else if ( command.equals(MOTOR_01) ) {
    sendSerialMessage("command = MOTOR_01");
    motor01Handler(intValue);
  } else if ( command.equals(MOTOR_02) ) {
    sendSerialMessage("command = MOTOR_02");
    motor02Handler(intValue);
  } else if ( command.equals(SERVO_01) ) {
    sendSerialMessage("command = SERVO_01");
    servoO1Handler(intValue); 
  } else if ( command.equals(SERVO_02) ) {
    sendSerialMessage("command = SERVO_02");
    servo02Handler(intValue);
  } else {
    sendSerialMessage( "Unrecognized command: " + value );
  }
}

void blinkHandler(int blinkValue) {
  setBlinkInterval(blinkValue);
}

void motor01Handler(int motor01Value) {
  setMotor01(motor01Value);
}

void motor02Handler(int motor02Value) {
  setMotor02(motor02Value);
}

void servoO1Handler(int servo01Value) {
  setServo01(servo01Value);
}

void servo02Handler(int servo02Value) {
  setServo02(servo02Value);
}

// This method will attempt to turn a string representation of an
// integer into an actual integer.  This method will return an
// integer value of zero if the string length is zero, if the string
// can't be parsed into an integer or, of course, if the string
// value actually represents the integer value zero.
int strToInt(String intString) {
  int newInt = 0;
  
  if( intString.length() > 0 ) {
    newInt = intString.toInt();
  }
  
  return newInt;
}

// Your general purpose serial message sender.
void sendSerialMessage(String message) {
  Serial.println(message);
  Serial.flush();
} 

