/****************************************************************************************
 This is just a collection of utilitiy functions that didn't logically fit anywhere else
****************************************************************************************/

// This method calculates the amount of space (free memory) between the heap and the stack.
// The amount of memory returned does NOT include bits of fragmented memory in the heap.
int freeRam () 
{
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

// This method will attempt to turn a string representation of an integer into an actual 
// integer.  This method will return an integer value of zero if the string length is 
// zero, if the string can't be parsed into an integer or, of course, if the string value
// actually represents the integer value zero.
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

// Send a serial message when a command is initialized
void sendInitMessage(String initName, int initValue) {
  sendSerialMessage("Initialized " + initName + " to " + String(initValue)); 
}

// Send a serial message when a command is updated
void sendUpdateMessage(String updateName, int updateValue) {
  sendSerialMessage( updateName + ":" + String(updateValue));
}


