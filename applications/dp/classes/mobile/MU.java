import java.io.*;
import javax.microedition.io.*;
import mobcon.message.*;

public class MU
{
public static String CONNECTOR_URL = "socket://localhost:1079";

public static NetMessage getNetMessageResult( NetMessage o)
{
MessageHandler mh = new MessageHandler(false, CONNECTOR_URL, o);
NetMessage result = null;
mh.start();
result = mh.getResult();
return result;
}


public static void sendNetMessage( NetMessage o)
{
MessageHandler mh = new MessageHandler(true, CONNECTOR_URL, o);
mh.start();
}



} //EOC
