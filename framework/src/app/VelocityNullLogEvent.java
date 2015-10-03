package mobcon.app;

import org.apache.velocity.app.event.NullSetEventHandler;

public class VelocityNullLogEvent implements NullSetEventHandler
{
    private boolean logNull;

    public VelocityNullLogEvent(boolean log)
    {
        logNull = log;
    }

    public boolean shouldLogOnNullSet( String lhs, String rhs )
    {
        return logNull;
    }

}
