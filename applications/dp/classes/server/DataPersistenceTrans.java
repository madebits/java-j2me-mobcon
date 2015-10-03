import mobcon.message.*;
import mobcon.server.*;
import mobcon.util.RandomGUID;
import mobcon.storeables.StoreableStringData;

import javax.microedition.lcdui.*;

import java.net.*;
import java.io.*;
import java.util.TreeMap;

/*****************************
Data Persistence (Server-Side)
******************************/
public class DataPersistenceTrans extends TransformerApp
{
    TreeMap storedObjects = null;
    int nextId;
    boolean encrypt = false;

    public DataPersistenceTrans(String tid, String iid)
    {
        super(tid, iid);
        storedObjects = new TreeMap();
        //storedObjects.put("0", null);
        nextId = 0;
    }

    public void setEncrypt(boolean set)
    {
        encrypt = set;
    }

    public boolean getEncrypt()
    {
        return encrypt;
    }

    public void execute(String opcode, byte[] data, Socket sock)
    {
        System.out.println(this.getClass().getName()+": "+"Executing OP = "+opcode);

        // Return message to client, never executed
        if(opcode.equals("00"))
        {
        }

        // Store object local at first position in TreeMap (0, reserved for StoreObject)
        if(opcode.equals("01"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Store object local");

            StoreableStringData ssa = new StoreableStringData();

            try
            {
                ssa.record2object(data);
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }
            storedObjects.put(ssa.getString(), ssa.getData());
        }

        // Send object back from local at first position in TreeMap (0, reserved for StoreObject)
        if(opcode.equals("02"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send data back from local (0)");

            StoreableStringData ssa = new StoreableStringData();

            try
            {
                ssa.record2object(data);
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }


            OutputStream os = null;
            DataOutputStream out = null;

            System.out.println(this.getClass().getName()+": "+"Sending message to client");

            byte[] outData = null;

            if(storedObjects.get(ssa.getString()) != null)
            {
                outData = (byte[])storedObjects.get(ssa.getString());
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
        if(opcode.equals("03"))
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
        if(opcode.equals("04"))
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

        // Send the generated mobileID back
        if(opcode.equals("05"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Send the generated mobileID back");

            RandomGUID myGUID = new RandomGUID(false);
            String GUID = myGUID.toString().toLowerCase();

            StoreableStringData ssa = new StoreableStringData();
            ssa.setString(GUID);
            byte[] outData = new byte[0];

            try
            {
                outData = ssa.object2record();
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }

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