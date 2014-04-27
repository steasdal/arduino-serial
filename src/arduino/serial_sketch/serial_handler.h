/****************************************************************************************
 All serial handling is, uh... handled by the methods in this file.  Incoming serial
 data is processed by the processSerialData() function which is called from from the 
 main loop() function.
 
 Incoming init and update command/value pairs are captured here and handled by looking
 up their handler functions (see the command_handlers.h file). 
****************************************************************************************/

/***** FUNCTION PROTOTYPES - FUNCTION PROTOTYPES *****/
void initStringHandler(String initString);
void commandStringHandler(String commandString);
String getCommandName(String command);
int getCommandValue(String command);
/***** FUNCTION PROTOTYPES - FUNCTION PROTOTYPES *****/

// We'll keep a function pointer to a function that we'll
// need to call every time a new command update comes in.
void(*updateNotifier)(void);

// This method is how we'll register the update function
// that'll be called on every new command update.
void registerForUpdateNotifications( void(*function)(void) ) {
  updateNotifier = function;
}

const unsigned char WAITING = 0;
const unsigned char INIT = 1;
const unsigned char COMMAND = 2;

const char INIT_START = 'I';
const char COMMAND_START = 'C';
const char COMMAND_SEPARATOR = ',';
const char DATA_SEPARATOR = ':';
const char NEWLINE = '\n';

String buffer = "";

int mode = WAITING;
boolean capturing = false;

void setupSerialHandler() {
  buffer.reserve(64);
}

// The original plan for handling incoming serial data resulted in much prettier code.
// It involved saving entire command strings with multiple commands right up to the newline
// character in a big buffer and then parsing the whole command string.  I quickly realized
// how scarce the resources are on an Arduino (2k of SRAM) and how important it is to keep
// the tiny serial buffer empty and rewrote it as this efficient but unsightly monstrosity.
//
// All incoming strings are going to take one of the following forms:
//
// I,BLINK:100                                        - Initialization string
// C                                                  - Lone command mode character
// C,BLINK:250                                        - Command string with single command
// C,BLINK:250,SRV1:0,SRV2:180,MTR1:25,MTR2:25...     - Command string with multiple commands
// 
// All incoming strings are going to begin with either an "I" or a "C" which sets the mode
// to "initialize" or "command" respectively and will terminate with a newline character (\n).
// Init strings will only be sent one at a time (although this function could handle multiple
// init commands in a single string).  Command strings will have ZERO or MORE commands following 
// the initial "C" mode character.
// 
// A "C" mode character sent by itself causes the function pointed to by the "updateNotifer"
// function pointer to be called (as does any other command string with one or more commands).
//
// All commands (both init and regular command commands) take the same form: <IDENTIFIER><VALUE>
// Multiple commands in a single command string will be separated with the COMMAND_SEPARATOR
// character (a comma: ',').
//
void processSerialData() {
  while( Serial.available() ) {
    
    char inChar = (char)Serial.read();
    
    if( mode == WAITING ) {      
      
      if( inChar == INIT_START ) {
        mode = INIT;
      } else if( inChar == COMMAND_START ) {
        mode = COMMAND;
        updateNotifier();
      } // else throw character away      
      
    } else {
      
      if( !capturing ) {
        
        if( inChar == COMMAND_SEPARATOR ) {
          capturing = true;
        } else if( inChar == NEWLINE ) {
          mode = WAITING;
        } // else throw character away
        
      } else {
        // We're capturing data in here!
        
        if( inChar == COMMAND_SEPARATOR || inChar == NEWLINE ) {
          
          // Process the contents of the buffer
          if( mode == INIT ) {
            initStringHandler(buffer);
          } else if( mode == COMMAND ) {
            commandStringHandler(buffer);
          } // else wtf?
          
          // Prepare the buffer for the next command
          buffer = "";          
          
          if( inChar == NEWLINE ) {
            mode = WAITING;
            capturing = false;
          }
          
        } else {
         
          // Append the character to the buffer
          buffer += inChar;
          
        }
      }      
    }
  }
}

// This method will handle init strings which will take the following form:
//
// UPDATE_RATE:10
// MISSED_UPDATES_ALLOWED:3
// SERVO_01:90
//
void initStringHandler(String initString) {
  String initName = getCommandName(initString);
  int initValue = getCommandValue(initString);
  
  if( initHandlerExists( initName ) ) {
    handleInit( initName, initValue );
  } else {
    sendSerialMessage( "I?: " + initName );
  }
}

// This method will handle command strings which'll typically look like this:
// 
// BLINK:1000
// SERVO_01:180
// MOTOR_02:255
//
void commandStringHandler(String commandString) {
  String commandName = getCommandName(commandString);
  int commandValue = getCommandValue(commandString);
  
  if( commandHandlerExists( commandName ) ) {
    handleCommand( commandName, commandValue );
  } else {
    sendSerialMessage( "C?: " + commandName );
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

