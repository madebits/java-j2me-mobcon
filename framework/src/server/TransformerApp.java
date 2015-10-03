package mobcon.server;

import java.net.*;
import java.io.*;

/*****************************
TransformerApp(Server side)
******************************/
public abstract class TransformerApp
{
    private String TID;
    private String IID;

    public TransformerApp()
    {
        this.TID = null;
        this.IID = null;
    }

    public TransformerApp(String tid, String iid)
    {
        this.TID = tid;
        this.IID = iid;
    }

    public void setTID(String tid)
    {
        this.TID = tid;
    }

    public void setIID(String iid)
    {
        this.IID = iid;
    }


    public String getTID()
    {
        return this.TID;
    }

    public String getIID()
    {
        return this.IID;
    }

    // To implement from each transformer
    public abstract void execute(String opcode, byte[] data, Socket sock);

}//EOC