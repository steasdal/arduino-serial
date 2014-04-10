package org.teasdale.util

import org.teasdale.impl.ArduinoSerialConfigImpl
import org.teasdale.impl.ArduinoSerialConnectionImpl

/**
 * This class manages the thread that sends updates to the Arduino
 */
public class DataUpdater
{
    private TheThread theThread = null
    ArduinoSerialConnectionImpl connection = null
    ArduinoSerialConfigImpl config = null

    /**
     * Ye olde constructor
     *
     * @param arduinoSerialConnectionImpl An instance of {@link ArduinoSerialConnectionImpl}
     * that's been properly configured and is ready to start sending some emm-effin' data!
     */
    public DataUpdater ( ArduinoSerialConnectionImpl arduinoSerialConnectionImpl ) {
        this.connection = arduinoSerialConnectionImpl
        this.config = arduinoSerialConnectionImpl.arduinoSerialConfigImpl
    }

    public void start()
    {
        if( theThread == null )
        {
            theThread = new TheThread();
            theThread.start();
        }
    }

    public void stop()
    {
        if( theThread != null )
        {
            // Set the flag that tells the thread to finish...
            theThread.stopThread();

            // Wait for the thread to finish
            try
            {
                theThread.join();
            }
            catch (InterruptedException e)
            {
                // Presumably no reason why this should happen.
            }

            // Stick a fork in it.
            theThread = null;
        }
    }

    public boolean isRunning()
    {
        return theThread != null;
    }

    public String getStatus()
    {
        if( theThread != null )
        {
            return "Running";
        }
        else
        {
            return "Not Running";
        }
    }

    /*************************************************************************/

    private class TheThread extends Thread
    {
        private boolean timeToStop = false;

        /**
         * Signal the thread that it's time to stop.
         */
        public void stopThread()
        {
            timeToStop = true;
        }

        /*
         * Do Work
         */
        public void run()
        {
            long millisecondsBetweenUpdates = calculateWaitTimeInMillis( config.getUpdateFrequency() )

            while( !timeToStop )
            {
                // Send a regularly scheduled update to the Arduino
                String updateString = CommandBuilder.buildUpdateString(config)
                long elapsedMilliseconds = sendUpdate(updateString)

                // Calculate how long we need to wait before sending the next update, sleep that long.
                sleep( calculateMillisecondsToWait(millisecondsBetweenUpdates, elapsedMilliseconds) )
            }
        }

        long calculateWaitTimeInMillis(int updateFrequency) {
            return (1000 / updateFrequency).toLong()
        }

        long sendUpdate(String updateString) {
            Date dateBeforeUpdate = new Date()
            connection.syncronizedWriteBytes(updateString.getBytes())
            Date dateAfterUpdate = new Date()

            // return the number of milliseconds that elapsed during the update
            return dateAfterUpdate.getTime() - dateBeforeUpdate.getTime()
        }

        long calculateMillisecondsToWait( long millisecondsBetweenUpdates, long elapsedMilliseconds) {
            // Calculate how long to wait until the next upate
            long millisecondsToNextUpdate = millisecondsBetweenUpdates - elapsedMilliseconds

            // If the last update took a particularly long time, the number of elapsed
            // milliseconds might have taken longer than the interval between updates.
            // If that happened, millisecondsToNextUpdate will be negative.  Set it to zero.
            return millisecondsToNextUpdate >= 0 ? millisecondsToNextUpdate : 0
        }
    }
}
