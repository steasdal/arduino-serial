package org.teasdale.util

import org.teasdale.impl.ArduinoSerialConnectionImpl

/**
 * This class manages the thread that sends updates to the Arduino
 */
public class DataUpdater
{
    private TheThread theThread = null
    ArduinoSerialConnectionImpl arduinoSerialConnectionImpl = null

    /**
     * Ye olde constructor
     *
     * @param arduinoSerialConnectionImpl An instance of {@link ArduinoSerialConnectionImpl}
     * that's been properly configured and is ready to start sending some emm-effin' data!
     */
    public DataUpdater ( ArduinoSerialConnectionImpl arduinoSerialConnectionImpl ) {
        this.arduinoSerialConnectionImpl = arduinoSerialConnectionImpl
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

        public void run()
        {
            /* Setup Here */

            while( timeToStop != true )
            {
                /* Do Work, Bitch! */

            }

            /* Cleanup Here */

        }
    }
}
