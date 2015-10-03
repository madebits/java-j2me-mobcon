import mobcon.message.*;
import mobcon.server.*;
import mobcon.storeables.*;

//import javax.microedition.lcdui.*;
import java.awt.Image;

import java.net.*;
import java.io.*;
import java.util.TreeMap;

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.*;

/*****************************
Test Case (Server-Side)
******************************/
public class MobRayTrans extends TransformerApp
{
    MobRayDataBase db = null;

    public MobRayTrans(String tid, String iid)
    {
        super(tid, iid);
        db = new MobRayDataBase();

        // Only once, comment out after...
        try{
            db.init();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+"Error in DataTransfer = "+e);
        }
        db.close();
    }

    public void execute(String opcode, byte[] data, Socket sock)
    {
        System.out.println(this.getClass().getName()+": "+"Executing OP = "+opcode);

        // Return message to client, never executed
        if(opcode.equals("00"))
        {
        }

        // Store Entry in Database
        if(opcode.equals("01"))
        {
            db.connect();

            System.out.println(this.getClass().getName()+": "+"OP = Store Entry in DB");

            DataBaseEntry entry = new DataBaseEntry();
            try
            {
                entry.record2object(data);
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+"Error in DataTransfer = "+e);
            }

            db.add(entry);
            System.out.println(this.getClass().getName()+": "+"Storing Entry = "+entry);

            db.close();
        }

        // Send Entry with "Sended ID" from Database to client

        if(opcode.equals("02"))
        {
            db.connect();

            System.out.println(this.getClass().getName()+": "+"OP = Send Entry back from DataBase");
            String id = new String(data);

            DataBaseEntry entry = db.get(id);

            byte[] outData = null;

            try
            {
                outData = entry.object2record();
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }

            System.out.println(this.getClass().getName()+": "+"Sending Entry to client");

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

            db.close();
        }

        // Send Storeable String-Array with the entries as elements
        if(opcode.equals("03"))
        {
            db.connect();

            System.out.println(this.getClass().getName()+": "+"OP = Send storeable String-Array with the entries as elements");

            StoreableStringArray ssa = new StoreableStringArray();
            ssa.setStringArray(db.entriesToString());

            byte[] outData = null;

            try
            {
                outData = ssa.object2record();
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }

            System.out.println(this.getClass().getName()+": "+"Sending storeable String-Array to client");

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

            db.close();
        }

        System.out.println(this.getClass().getName()+": "+"Finished OP: "+opcode);
        System.out.println(this.getClass().getName()+": "+"---------------");

    }
/*
try{
File fi = new File("xray.png");
FileInputStream fis = new FileInputStream(fi);


System.out.println("Image: "+fis.available());

ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
int ch;
int i = 0;
while ((ch = fis.read()) != -1)
{
    if(i==0) System.out.println("fromFile: "+ch);
    bStrm.write(ch);
    i++;
}
byte[] im = bStrm.toByteArray();


File fo = new File("out.png");
FileOutputStream fos = new FileOutputStream(fo);
fos.write(bStrm.toByteArray());
fos.close();

bStrm.close();

for(i=0; i < im.length;i++)
{
    if(im[i]==137) System.out.println("Found "+i);
}
System.out.println("fromArray: "+im[0]);

System.out.println("Image: "+im.length);


Image test = Image.createImage(im, 0, im.length);
fis.close();
} catch(Exception exc)
{
  System.out.println("Image: "+exc);
}
*/


}//EOC