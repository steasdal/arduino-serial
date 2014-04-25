/**************************************************************************************************
 SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER
**************************************************************************************************/

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

