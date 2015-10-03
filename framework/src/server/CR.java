package mobcon.server;

import mobcon.message.*;

import java.net.*;
import java.io.*;
import java.util.TreeMap;

/*****************************
Connector Router (Server side)
******************************/
public class CR
{
    private TreeMap containerApps;

    public CR()
    {
        containerApps = new TreeMap();
    }

    public void addContainerApp(ContainerApp ca)
    {
        containerApps.put(ca.getCID(), ca);
    }

    public ContainerApp getContainerApp(String cid)
    {
        return ((ContainerApp)containerApps.get(cid));
    }


    public void process(Socket s) throws Exception
    {
        DataInputStream in = new DataInputStream(s.getInputStream());

        NetMessage message = NetMessage.streamToNetMessage(in);

        System.out.println(this.getClass().getName()+": "+"Evaluating Message from client...");

        System.out.println(this.getClass().getName()+": "+"MESSAGE = "+message.toString());

        ContainerApp ca = getContainerApp(message.getCID());
        TransformerApp ta = ca.getTransformerApp(message.getPID(), message.getIID());

        ta.execute(message.getOID(), message.data, s);

    }

}//EOC