package mobcon.server;

import java.net.*;
import java.io.*;
import java.util.TreeMap;

/*****************************
ContainerApp(Server side)
******************************/
public class ContainerApp
{
    private String CID;

    private TreeMap transformerApps;

    public ContainerApp()
    {
        transformerApps = new TreeMap();
    }

    public ContainerApp(String cid)
    {
        transformerApps = new TreeMap();

        this.CID = cid;
    }

    public void addTransformerApp(TransformerApp ta)
    {
        String id = ta.getTID();
        id = id+ta.getIID();
        transformerApps.put(id, ta);
    }

    public TransformerApp getTransformerApp(String tid, String iid)
    {
        String id = tid+iid;
        return ((TransformerApp)transformerApps.get(id));
    }

    public TransformerApp getTransformerApp(String id)
    {
        return ((TransformerApp)transformerApps.get(id));
    }

    public String getCID()
    {
        return this.CID;
    }

    public void setCID(String cid)
    {
        this.CID = cid;
    }

}//EOC