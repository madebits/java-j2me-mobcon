import mobcon.message.*;
import javax.microedition.lcdui.*;

import mobcon.server.*;

import java.net.*;
import java.io.*;
import java.util.TreeMap;

/*****************************
Data Persistence (Server-Side)
******************************/
public class DataPersistence extends TransformerApp
{
    TreeMap storedObjects = null;
    int nextId;

    public DataPersistence(String tid, String iid)
    {
        super(tid, iid);
        storedObjects = new TreeMap();
        storedObjects.put("0", null);
        nextId = 1;
/*
byte[] outData;
try{
    File f = new File("xray.png");
    FileInputStream fis = new FileInputStream(f);
    outData = new byte[(int)f.length()];
    fis.read(outData);
    fis.close();

    Image i = Image.createImage(outData, 0, outData.length);
    //Image i = Image.createImage("/xray.png");
    fis.close();
}catch(Exception e){ System.out.println(this.getClass().getName()+": Error byting File "+e);}
*/
    }

    public void execute(int opcode, byte[] data, Socket sock)
    {
        System.out.println(this.getClass().getName()+": "+"Executing OP = "+opcode);

        // Return message to client, never executed
        if(opcode == 0)
        {
        }

        // Store object local at first position in TreeMap (0, reserved for StoreObject)
        if(opcode == 1)
        {
            System.out.println(this.getClass().getName()+": "+"OP = Store object local");

            String id = ""+0;
            storedObjects.put(id, data);
        }

        // Send object back from local at first position in TreeMap (0, reserved for StoreObject)
        if(opcode == 2)
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send data back from local (0)");
            OutputStream os = null;
            DataOutputStream out = null;

            System.out.println(this.getClass().getName()+": "+"Sending message to client");

            String id = ""+0;

            byte[] outData = null;

            if(storedObjects.get(id) != null)
            {
                outData = (byte[])storedObjects.get(id);
            } else {
                outData = new byte[0];
            }
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

        // Store object local, return id
        if(opcode == 3)
        {
            System.out.println(this.getClass().getName()+": "+"OP = Store object local, return id");
            String id = ""+nextId;
            storedObjects.put(id, data);

            byte [] idByte = id.getBytes();

            OutputStream os = null;
            DataOutputStream out = null;

            System.out.println(this.getClass().getName()+": "+"Sending message to client");

            try
            {
                os = sock.getOutputStream();
                out = new DataOutputStream(os);

                out.writeInt(idByte.length);
                out.write(idByte);

                nextId++;
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

        // Send object back from local TreeMap at position id
        if(opcode == 4)
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send data back from local");
            String id = new String(data);

            byte[] outData = (byte[])storedObjects.get(id);

            System.out.println(this.getClass().getName()+": "+"Sending message to client");

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

        if(opcode == 5)
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send image back from local");

            byte[] outData = null;

            try{
                File f = new File("xray.png");
                FileInputStream fis = new FileInputStream(f);
                outData = new byte[(int)f.length()];
                fis.read(outData);
                fis.close();
            }catch(Exception e){ System.out.println(this.getClass().getName()+": Error byting File "+e);}

            System.out.println(this.getClass().getName()+": "+"Sending message to client");

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