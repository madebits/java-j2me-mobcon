package mobcon.message;

import java.io.*;
import javax.microedition.io.*;

/*******************************
MessageHandler makes new Thread
********************************/
public class MessageHandler extends Thread
{
    private static boolean debug = true;

    public String url = null;
    public NetMessage netMess = null;

    // Is true when there is a send, false when there is get
    boolean send;
    // The result of the get is saved here
    public NetMessage result;

    public boolean finished;

    public MessageHandler(boolean snd, String url, NetMessage o)
    {
        super();
        this.send = snd;
        this.url = url;
        this.netMess = o;
        this.result = null;

        this.finished = false;
    }

    public void run()
    {
        if(send) sendNetMessage(netMess);
        if(!send) getNetMessage(netMess);
    }

    public void sendNetMessage(NetMessage o)
    {
        SocketConnection sc = null;
        OutputStream os = null;
        DataOutputStream out = null;

        System.out.println(this.getClass().getName()+": "+"Sending message to server");
        if(debug) System.out.println(this.getClass().getName()+": "+o);

        try
        {
            sc = (SocketConnection) Connector.open(url);
            sc.setSocketOption(SocketConnection.LINGER, 5);
            os = sc.openOutputStream();
            out = new DataOutputStream(os);
            out.write(NetMessage.toByte(o));
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in sending Net-Message "+e);
        }finally{
            try
            {
                out.close();
                os.close();
                sc.close();
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": Error in closing streams "+e);
            }
        }
    }

    public synchronized void getNetMessage(NetMessage o)
    {
        SocketConnection sc = null;
        InputStream is = null;
        DataInputStream in = null;

        OutputStream os = null;
        DataOutputStream out = null;

        System.out.println(this.getClass().getName()+": "+"Getting message from server");
        if(debug) System.out.println(this.getClass().getName()+": "+"Sended request:");
        if(debug) System.out.println(this.getClass().getName()+": "+o);

        try
        {
            sc = (SocketConnection) Connector.open(url);
            sc.setSocketOption(SocketConnection.LINGER, 5);

            os = sc.openOutputStream();

            out = new DataOutputStream(os);
            out.write(NetMessage.toByte(o));

            is = sc.openInputStream();
            in = new DataInputStream(is);

            result = new NetMessage();
            result.CID = o.CID;
            result.PID = o.PID;
            result.IID = o.IID;
            result.setOID("0");

            int dataLength = in.readInt();
            if(dataLength > 0)
            {
                result.data = new byte[dataLength];
                in.readFully(result.data);
            }else{
                result.setOID("FF");
            }

            //System.out.println(this.getClass().getName()+": "+"RESULT = "+result.toString());

        }catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in getting NetMessage "+e);
        }finally{
            try
            {
                out.close();
                os.close();

                in.close();
                is.close();
                sc.close();

            }catch(Exception e){
                System.out.println(this.getClass().getName()+": Error in closing streams "+e);
            }
        }

        this.finished = true;
        notify();
    }

    public synchronized NetMessage getResult()
    {
        while(!this.finished) {
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println(this.getClass().getName()+": Getting result interrupted "+e);
            }

        }

        return this.result;
    }

}