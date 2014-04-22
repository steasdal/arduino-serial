#include <Servo.h> 
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

#include <StandardCplusplus.h>
#include <map>

std::map<String, void(*)(int)> initHandlerMap;
std::map<String, void(*)(int)> commandHandlerMap;

void registerInitHandler(String initName, void(*initHandler)(int)) {
  initHandlerMap.insert( std::pair<String, void(*)(int)> (initName, initHandler) );
}

void registerCommandHandler(String commandName, void(*commandHandler)(int)) {
  commandHandlerMap.insert( std::pair<String, void(*)(int)> (commandName, commandHandler) );
}

/************************************************
 BLINK BLINK BLINK BLINK BLINK BLINK BLINK BLINK 
************************************************/
const String BLINK = "BLINK";

int blinkIntervalInit = 250;             // Set blink interval initial value to 250
int blinkInterval = blinkIntervalInit;   // Set blink interval to its initial value

int led = 13;                            // This is the pin on which we'll be blinking an LED
int ledState = LOW;                      // The current LED state (LOW or HIGH)
long previousLedStateChange = 0;         // Stores the last time the LED state changed 

void updateBlinkIntervalInit(int newBlinkIntervalInit) {
  blinkIntervalInit = newBlinkIntervalInit;
  sendInitMessage(BLINK, newBlinkIntervalInit);
}

void setBlinkInterval(int newBlinkInterval) {
  if( newBlinkInterval <= 0 ) {
    // If a negative blink value is received, reset to initial value
    blinkInterval = blinkIntervalInit;
  } 
  else {
    blinkInterval = constrain(newBlinkInterval, 10, 10000);
    sendUpdateMessage(BLINK, blinkInterval);
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
  } 
  else {
    ledState = LOW;
  }
}

/*******************************************
 SERVOS - SERVOS - SERVOS - SERVOS - SERVOS
*******************************************/
const String SERVO_01 = "SERVO_01";
const String SERVO_02 = "SERVO_02";

Servo servo01;
Servo servo02;

int servo01Init = 90;
int servo02Init = 90;

void updateServo01Init(int newInitValue) {
  servo01Init = newInitValue;
  sendInitMessage(SERVO_01, newInitValue);
}

void updateServo02Init(int newInitValue) {
  servo02Init = newInitValue;
  sendInitMessage(SERVO_02, newInitValue);
}

void setServo01(int servoValue) {
  servo01.write( constrain(servoValue, 0, 180) );
  sendUpdateMessage(SERVO_01, servoValue);
}

void setServo02(int servoValue) {
  servo02.write( constrain(servoValue, 0, 180) );
  sendUpdateMessage(SERVO_02, servoValue);
}

/*******************************************
 MOTORS - MOTORS - MOTORS - MOTORS - MOTORS
*******************************************/
const String MOTOR_01 = "MOTOR_01";
const String MOTOR_02 = "MOTOR_02";

int motor01Init = 0;
int motor02Init = 0;

Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *motor01 = AFMS.getMotor(1);
Adafruit_DCMotor *motor02 = AFMS.getMotor(2);

void updateMotor01Init(int newInitValue) {
  motor01Init = newInitValue;
  sendInitMessage(MOTOR_01, newInitValue);
}

void updateMotor02Init(int newInitValue) {
  motor02Init = newInitValue;
  sendInitMessage(MOTOR_02, newInitValue);
}

void setMotor01(int motorValue) {
  setMotorSpeed(motor01, motorValue);
  sendUpdateMessage(MOTOR_01, motorValue);
}

void setMotor02(int motorValue) {
  setMotorSpeed(motor02, motorValue);
  sendUpdateMessage(MOTOR_02, motorValue);
}

void setMotorSpeed(Adafruit_DCMotor *motor, int speed) {
  speed = constrain(speed, -255, 255);
 
  if( speed == 0 ) {
    motor->run(RELEASE);
  } else if( speed > 0 ) {
    motor->setSpeed( speed );
    motor->run( FORWARD );
  } else {
    speed = abs( speed );
    motor->setSpeed( speed );
    motor->run( BACKWARD );
  }
}

/*************************************************
 UPDATE HANDLER - UPDATE HANDLER - UPDATE HANDLER
*************************************************/

const String UPDATE_RATE = "UPDATE_RATE";
const String MISSED_UPDATES_ALLOWED = "MISSED_UPDATES_ALLOWED";

int updateRate = 5;
int missedUpdatesAllowed = 10;

unsigned long lastUpdate = 0;           // The last time an update was received
boolean connectionLive = false;         // Is this connection currently live?

void updateRateInitHandler(int initialUpdateRate) {
  updateRate = initialUpdateRate;
  sendInitMessage(UPDATE_RATE, initialUpdateRate);
}

void missedUpdatesAllowedInitHandler(int initialMissedUpdatesAllowed ) {
  missedUpdatesAllowed = initialMissedUpdatesAllowed;
  sendInitMessage(MISSED_UPDATES_ALLOWED, initialMissedUpdatesAllowed);
}

// This method will be called by the serialRounter 
// method every time a new command is received.
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

    // If the current time minus the previous update time is greater than 
    // the number of milliseconds that have elapsed since the last update, 
    // we've got an expired connection.  If that happens, reset all commands 
    // to their initial values by calling the initializeCommands() method.
    unsigned long currentMillis = millis();

    if( currentMillis - lastUpdate > millisecondsBeforeExpiration ) {
      initializeCommands();
      connectionLive = false;
    }
  }
}

/************************************************
 REGISTER INIT HANDLERS - REGISTER INIT HANDLERS
************************************************/
void registerInitHandlers() {
  registerInitHandler(UPDATE_RATE, updateRateInitHandler);
  registerInitHandler(MISSED_UPDATES_ALLOWED, missedUpdatesAllowedInitHandler);
  registerInitHandler(BLINK, updateBlinkIntervalInit);
  registerInitHandler(SERVO_01, updateServo01Init);
  registerInitHandler(SERVO_02, updateServo02Init);
  registerInitHandler(MOTOR_01, updateMotor01Init);
  registerInitHandler(MOTOR_02, updateMotor02Init);
}

/******************************************************
 REGISTER COMMAND HANDLERS - REGISTER COMMAND HANDLERS
******************************************************/
void registerCommandHandlers() {
  registerCommandHandler(BLINK, setBlinkInterval);
  registerCommandHandler(SERVO_01, setServo01);
  registerCommandHandler(SERVO_02, setServo02);
  registerCommandHandler(MOTOR_01, setMotor01);
  registerCommandHandler(MOTOR_02, setMotor02);
}

/**********************************************
 REINITIALIZE COMMANDS - REINITIALIZED COMMANDS
***********************************************/
void initializeCommands() {
  blinkInterval = blinkIntervalInit;
  setServo01(servo01Init);
  setServo02(servo02Init);
  setMotor01(motor01Init);
  setMotor02(motor02Init);
}

/****************************************************************
 SETUP AND MAIN LOOP - SETUP AND MAIN LOOP - SETUP AND MAIN LOOP
****************************************************************/

void setup() {     
  pinMode(led, OUTPUT);   

  AFMS.begin();
  
  servo01.attach(9);
  servo01.write(servo01Init);
  servo02.attach(10);
  servo02.write(servo02Init);
  
  Serial.begin(19200); 
  
  registerInitHandlers();
  registerCommandHandlers(); 
  
  sendSerialMessage("free SRAM: " + String( freeRam() ));
}

void loop() {
  checkForUpdateExpiration();
  checkForBlink();
}

/**************************************************************************************************
 SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER
**************************************************************************************************/

const String COMMAND_START = "CMD";
const String INIT_START = "INIT";
const char   COMMAND_SEPARATOR = ',';
const char   DATA_SEPARATOR = ':';

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
    } 
    else {
      // append inChar to the serialString
      serialString += inChar;
    }
  }
}

void serialRouter(String serialString) {
  if ( serialString.startsWith( INIT_START ) ) {
    if( serialString.length() > INIT_START.length() ) {
      String initPlusSeparator = INIT_START + COMMAND_SEPARATOR;
      String initCommand = serialString.substring( initPlusSeparator.length() );
      
      initStringHandler( initCommand );
    }
  } else if( serialString.startsWith( COMMAND_START ) ) {

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

/* ********************************************************************************************* */

// This method will handle init strings which will take the following form:
// UPDATE_RATE:10
// MISSED_UPDATES_ALLOWED:3
// Note that init commands will only be transmitted one at a time.  We should
// never encounter multiple init statements in a single init string.
void initStringHandler(String initString) {
  String initName = getCommandName(initString);
  int initValue = getCommandValue(initString);
  
  // Using initName as the key, lookup the corresponding
  // init handler function in the initHandlerMap map and
  // execute it passing initValue as the integer argument.
  if( initHandlerMap.count( initName ) > 0 ) {
    void(*initHandler)(int) = initHandlerMap[ initName ];
    initHandler( initValue );
  } else {
    sendSerialMessage( "Unrecognized init command: " + initName );
  }
}

/* ********************************************************************************************* */

// This method will handle command strings which will typically look something like this:
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
    } 
    else {
      command = commandString;
      commandString = "";
    }

    commandHandler(command);

  } 
  while( commandString.length() > 0 );
}

// This'll handle individual commands with a command name and a value separated
// by the DATA_SEPARATOR character (e.g. BLINK:1000, MOTOR_01:255, etc.)
void commandHandler(String commandString) {
  String commandName = getCommandName(commandString);
  int commandValue = getCommandValue(commandString);
  
  // Using commandName as the key, lookup the corresponding
  // command handler function in the commandHandlerMap map and
  // execute it passing commandValue as the integer argument.
  if( commandHandlerMap.count( commandName ) > 0 ) {
    void(*commandHandler)(int) = commandHandlerMap[ commandName ];
    commandHandler( commandValue );
  } else {
    sendSerialMessage( "Unrecognized command: " + commandName );
  }
}

// A command will be of the format NAME:VALUE.  This method
// returns the NAME portion of the command.
String getCommandName(String command) {
  int dataSeparatorIndex = command.indexOf( DATA_SEPARATOR );
  return command.substring(0, dataSeparatorIndex );
}

// A command will be of the format NAME:VALUE.  This method returns 
// the VALUE portion of the command parsed into an integer value.
int getCommandValue(String command) {
  int dataSeparatorIndex = command.indexOf( DATA_SEPARATOR );
  return strToInt( command.substring(dataSeparatorIndex + 1, command.length()) );
}

/******************************************************************************
 UTILITY FUNCTIONS - UTILITY FUNCTIONS - UTILITY FUNCTIONS - UTILITY FUNCTIONS
******************************************************************************/

// This method calculates the amount of space (free memory) between the heap and the stack.
// The amount of memory returned does NOT include bits of fragmented memory in the heap.
int freeRam () 
{
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
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

void sendInitMessage(String initName, int initValue) {
  sendSerialMessage("Initializing " + initName + " to " + String(initValue)); 
}

void sendUpdateMessage(String updateName, int updateValue) {
  sendSerialMessage("Updating " + updateName + " to " + String(updateValue));
}

// Your general purpose serial message sender.
void sendSerialMessage(String message) {
  Serial.println(message);
  Serial.flush();
} 

