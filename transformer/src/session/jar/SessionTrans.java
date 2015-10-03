import mobcon.message.*;
import mobcon.server.*;
import mobcon.storeables.*;

//import javax.microedition.lcdui.*;
import java.awt.Image;

import java.net.*;
import java.io.*;
import java.util.TreeMap;
import java.util.Hashtable;

/*****************************
Session Transformer (Server-Side)
******************************/
public class SessionTrans extends TransformerApp
{
    TreeMap contextStore;

    public SessionTrans(String tid, String iid)
    {
        super(tid, iid);
        contextStore = new TreeMap();
    }

    public void execute(String opcode, byte[] data, Socket sock)
    {
        System.out.println(this.getClass().getName()+": "+"Executing OP = "+opcode);

        // Return message to client, never executed
        if(opcode.equals("00"))
        {
        }

        // Store Context on server
        if(opcode.equals("01"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Store context on server");

            try
            {
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream inputStream = new DataInputStream(bais);

                String sessionId = inputStream.readUTF();
                inputStream.close();
                contextStore.put(sessionId, data);
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+"Error in DataTransfer = "+e);
            }
        }

        // Send Context to client
        if(opcode.equals("02"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send context to client");

            String sessionId = new String(data);
            byte[] outData = null;
            if(contextStore.get(sessionId) != null)
            {
                outData = (byte[])contextStore.get(sessionId);
            }else{
                outData = new byte[0];
            }

            OutputStream os = null;
            DataOutputStream out = null;

            try
            {
                os = sock.getOutputStream();
                out = new DataOutputStream(os);

                out.writeInt(outData.length);
                out.write(outData);
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }finally{
                try
                {
                    out.close();
                    os.close();
                }catch(Exception e){ System.out.println(this.getClass().getName()+": "+e);}
            }
        }


        System.out.println(this.getClass().getName()+": "+"Finished OP: "+opcode);
        System.out.println(this.getClass().getName()+": "+"---------------");

    }

}//EOC