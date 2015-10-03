import mobcon.server.*;

import java.net.*;
import java.io.*;


//import javax.microedition.lcdui.Image;
import java.awt.Toolkit;

/**********
Test Server
***********/
public class TestServer
{
    public static int CONNECTOR_PORT = 1079;

    public static void main(String[] args)
    {
        MobConMain mcm = new MobConMain();

        ServerSocket servSock = null;
        int port = CONNECTOR_PORT;
        try
        {
            servSock = new ServerSocket(port);
            System.out.println("Server: "+"Starting Server");
            while(true)
            {
                try
                {
                    Socket newSock = servSock.accept();
                    System.out.println("Server: "+"Launching new connection with "+newSock.getRemoteSocketAddress());
                    mcm.process(newSock);
                }catch (Exception exc)
                {
                    System.out.println("Server: Error in processing = "+exc);
                }
              }
          } catch(Exception exc)
          {
              System.out.println("Server: "+exc);
          }
          finally
          {
            System.out.println("Server: "+"Closing Server");
            try
              {
                  if (servSock != null) servSock.close();
              }
            catch(IOException exc)
              {
                  System.out.println("Server: "+exc);
              }
          }

    }

}//EOC