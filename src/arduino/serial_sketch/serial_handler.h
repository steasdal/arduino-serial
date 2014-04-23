/**************************************************************************************************
 SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER - SERIAL COMMAND HANDLER
**************************************************************************************************/

/***** FUNCTION PROTOTYPES - FUNCTION PROTOTYPES *****/
void serialRouter(String serialString);
void initStringHandler(String initString);
void commandStringHandler(String commandString);
void commandHandler(String commandString);
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

const String COMMAND_START = "CMD";
const String INIT_START = "INIT";
const char   COMMAND_SEPARATOR = ',';
const char   DATA_SEPARATOR = ':';

String serialString = "";

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
    
    // If the incoming character is a newline, pass the completed 
    // string off to the serialRouter and reset the serialString.
    if (inChar == '\n') {
      serialRouter(serialString);
      serialString = "";
    } 
    else {
      serialString += inChar;
    }
  }
}

void serialRouter(String serialString) {
  if ( serialString.startsWith( INIT_START ) ) {
    if( serialString.length() > INIT_START.length() ) {
      initStringHandler( serialString.substring( (INIT_START + COMMAND_SEPARATOR).length() ) );
    }
  } else if( serialString.startsWith( COMMAND_START ) ) {
    if( serialString.length() > COMMAND_START.length() ){
      commandStringHandler( serialString.substring( (COMMAND_START + COMMAND_SEPARATOR).length() ) );
    }
    
    // Update the variable that holds the time of last update
    updateNotifier();
  }
}

// This method will handle init strings which will take the following form:
//
// UPDATE_RATE:10
// MISSED_UPDATES_ALLOWED:3
//
// Note that init commands will only be transmitted one at a time.  We should
// never encounter multiple init statements in a single init string.
void initStringHandler(String initString) {
  String initName = getCommandName(initString);
  int initValue = getCommandValue(initString);
  
  if( initHandlerExists( initName ) ) {
    handleInit( initName, initValue );
  } else {
    sendSerialMessage( "Unrecognized init command: " + initName );
  }
}

// This method will handle command strings which will typically look something like this:
//
// BLINK:1000
// BLINK:2500,MOTOR_01:255
// BLINK:500,MOTOR_01:50,MOTOR_02:50,SERVO_01:75,SERVO_02:120
//
// We'll split string into individual commands and pass 
// each one off to the commandHandler function below.
void commandStringHandler(String commandString) {
  int start = 0;
  int index = -1;
  
  do {
    index = commandString.indexOf( COMMAND_SEPARATOR, start );
    
    if( index > 0 ) {
      commandHandler( commandString.substring( start, index ) );
    } else {
      commandHandler( commandString.substring( start ) );
    }
    
    start = index + 1;    
  } while ( index > 0 );
}

// This'll handle individual commands with a command name and a value separated
// by the DATA_SEPARATOR character (e.g. BLINK:1000, MOTOR_01:255, etc.)
void commandHandler(String commandString) {
  String commandName = getCommandName(commandString);
  int commandValue = getCommandValue(commandString);
  
  if( commandHandlerExists( commandName ) ) {
    handleCommand( commandName, commandValue );
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

