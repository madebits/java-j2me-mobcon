package vdoclet;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.LogSystem;

public class GeneratorLogSystem implements LogSystem {

    //---( Constructor )---

    public GeneratorLogSystem() {
    }

    //---( Initialisation )---

    public void init( RuntimeServices rs )
        throws Exception
    {
    }
    
    //---( Logging )---

    /**
     * Log a message
     *
     * @param level severity level
     * @param message complete error message
     */
    public void logVelocityMessage( int level, String message ) {
        if (level == ERROR_ID) {
            System.err.println( message );
        }
    }

}
