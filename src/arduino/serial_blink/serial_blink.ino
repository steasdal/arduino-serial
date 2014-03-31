int led = 13;                            // LED on pin 13 - this is what we'll be blinkin'
int blinkInterval = 500;                 // Initial blink interval in milliseconds

String serialString = "";                // String that'll hold incoming serial data.
boolean serialStringComplete = false;    // Set to true once an entire serial string is received.

int ledState = LOW;                      // The current LED state (LOW or HIGH)
long previousLedStateChange = 0;         // Stores the last time the LED state changed 
const String BLINK_INTERVAL_FLAG= "B";   // Serial messages that start with this string contain a blink interval

void setup() {                
  pinMode(led, OUTPUT);   
  Serial.begin(9600);  
}

void loop() {
  checkSerialString();
  checkForBlink();
}

/* ********************************************************************************************* */

// If a full serial string was received, run it through the
// parseSerialString method and reset the string and flag.
void checkSerialString() {
  if(serialStringComplete) {
    parseSerialString();
    serialString = "";
    serialStringComplete = false;
  }
}

void parseSerialString() {
  if(serialString.length() > 0) {
    if(serialString.startsWith(BLINK_INTERVAL_FLAG)) {
      setBlinkInterval( serialString.substring(1) );
    }
  }
}

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
    // add it to the inputString:
    serialString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      serialStringComplete = true;
    } 
  }
}

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

