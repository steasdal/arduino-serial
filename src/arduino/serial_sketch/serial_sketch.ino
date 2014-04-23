#include <Servo.h> 
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

#include <StandardCplusplus.h>
#include <map>

#include "command_handlers.h"
#include "util_functions.h"
#include "serial_handler.h"
#include "update_handler.h"

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

/************************************************
 REGISTER INIT HANDLERS - REGISTER INIT HANDLERS
************************************************/
void registerInitHandlers() {
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

/*********************************************
 REINITIALIZE COMMANDS - REINITIALIZE COMMANDS
**********************************************/
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
  
  registerInitHandlers();
  registerCommandHandlers(); 
  setupUpdateHandler(initializeCommands);
  
  Serial.begin(19200); 
  
  sendSerialMessage("free SRAM: " + String( freeRam() ));
}

void loop() {
  checkForUpdateExpiration();
  checkForBlink();
}

