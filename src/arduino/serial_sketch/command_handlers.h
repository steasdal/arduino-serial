/**************************************************************************
 COMMAND HANDLERS - COMMAND HANDLERS - COMMAND HANDLERS - COMMAND HANDLERS
**************************************************************************/

std::map<String, void(*)(int)> initHandlerMap;

void registerInitHandler(String initName, void(*initHandler)(int)) {
  initHandlerMap.insert( std::pair<String, void(*)(int)> (initName, initHandler) );
}

boolean initHandlerExists(String initName) {
  return( initHandlerMap.count( initName ) > 0 );
}

void handleInit( String initName, int initValue ) {
  void(*initHandler)(int) = initHandlerMap[ initName ];
  initHandler( initValue );
}

std::map<String, void(*)(int)> commandHandlerMap;

void registerCommandHandler(String commandName, void(*commandHandler)(int)) {
  commandHandlerMap.insert( std::pair<String, void(*)(int)> (commandName, commandHandler) );
}

boolean commandHandlerExists(String commandName) {
  return( commandHandlerMap.count( commandName ) > 0 );
}

void handleCommand( String commandName, int commandValue ) {
  void(*commandHandler)(int) = commandHandlerMap[ commandName ];
  commandHandler( commandValue );
}
