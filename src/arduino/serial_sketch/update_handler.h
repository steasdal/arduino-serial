/****************************************************************************************
 Here you'll find the update handler.  The variables and functions herein are responsible
 for keeping track of the last time an update was received.  If no updates are received
 within a certain period of time, the commandInitializer function will be called to set
 all commands to their initial values.
****************************************************************************************/

const String UPDATE_RATE = "UPDATE_RATE";
const String MISSED_UPDATES_ALLOWED = "MISSED_UPDATES_ALLOWED";

int updateRate = 5;
int missedUpdatesAllowed = 10;

unsigned long lastUpdate = 0;           // The last time an update was received
boolean connectionLive = false;         // Is this connection currently live?

// This is a pointer to the function that we'll call when we stop receiving commands.
void(*commandInitializer)(void);

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
      commandInitializer();
      connectionLive = false;
    }
  }
}

// This method will be called in the main setup() method.  We'll register
// our two init handlers, register for update notifications from serial_handler,
// and save off a pointer to the function that we'll call when the conneciton
// times out and all commands get reset to their initial values.
void setupUpdateHandler( void(*function)(void) ) {
  registerInitHandler(UPDATE_RATE, updateRateInitHandler);
  registerInitHandler(MISSED_UPDATES_ALLOWED, missedUpdatesAllowedInitHandler);
  registerForUpdateNotifications(resetLastUpdate);
  commandInitializer = function;
}
