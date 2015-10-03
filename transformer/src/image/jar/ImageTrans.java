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
public class ImageTrans extends TransformerApp
{
    boolean memoryCheck = false;

    public ImageTrans(String tid, String iid)
    {
        super(tid, iid);
    }

    public void execute(String opcode, byte[] data, Socket sock)
    {
        System.out.println(this.getClass().getName()+": "+"Executing OP = "+opcode);

        // Return message to client, never executed
        if(opcode.equals("00"))
        {
        }

        // Transform Image and send it to client
        if(opcode.equals("01"))
        {
            System.out.println(this.getClass().getName()+": "+"OP = Transform Image and send it to client");
            ImageTransData itd = new ImageTransData(data);

            // Get the image data
            String PNG_MIME = "image/png";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            Image img = Jimi.getImage(itd.getName());

            // Reducing
            ColorReducer reducer = new ColorReducer(itd.getColors(), itd.getDither());
            try{
                img = reducer.getColorReducedImage( img );
            }
            catch( JimiException e ){
                System.out.println(this.getClass().getName()+": Error reducing Image "+e);
            }

            GraphicsUtils.waitForImage( img );
            // End Reducing

            // Scale
            img = img.getScaledInstance(itd.getWidth(), itd.getHeight(),Image.SCALE_SMOOTH);
            GraphicsUtils.waitForImage( img );
            // End Scale

            try
            {
                Jimi.putImage(PNG_MIME, img, bos);

                // If not sufficient memory, reduce size again
                if(memoryCheck)
                {
                    while((itd.getMemory() < bos.size()) && (itd.getWidth()*0.9>1) && (itd.getHeight()*0.9>1))
                    {
                        bos.reset();
                        itd.setWidth((int)(itd.getWidth()*0.9));
                        itd.setHeight((int)(itd.getHeight()*0.9));
                        img = img.getScaledInstance(itd.getWidth(), itd.getHeight(),Image.SCALE_SMOOTH);
                        GraphicsUtils.waitForImage( img );
                        Jimi.putImage(PNG_MIME, img, bos);
                    }
                }

            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }
            // END Get the image data

            byte[] outData = null;
            try
            {
                outData = bos.toByteArray();
                System.out.println(this.getClass().getName()+": Image-Name "+itd.getName());
                System.out.println(this.getClass().getName()+": Image-Size "+outData.length);
                System.out.println(this.getClass().getName()+": Image-Width "+itd.getWidth());
                System.out.println(this.getClass().getName()+": Image-Height "+itd.getHeight());
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }finally{
                try{
                    bos.close();
                }catch(Exception e){ System.out.println(this.getClass().getName()+": "+e);}
            }

            System.out.println(this.getClass().getName()+": "+"Sending Image to client");

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

class ImageTransData
{
    private String name = null;
    private int colors = 0;
    private boolean dither = false;
    private int width = 0;
    private int height = 0;
    private int memory = 0;

    public ImageTransData(byte[] data)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream inputStream = new DataInputStream(bais);

            this.setName(inputStream.readUTF());
            this.setColors(inputStream.readInt());
            this.setWidth(inputStream.readInt());
            this.setHeight(inputStream.readInt());
            this.setDither(inputStream.readBoolean());
            this.setMemory(inputStream.readInt());
        } catch(Exception exc){
          System.out.println(this.getClass().getName()+": "+exc);
        }

    }

    public void setName(String in)
    {
        this.name = in;
    }

    public void setColors(int in)
    {
        this.colors = in;
    }

    public void setWidth(int in)
    {
        this.width = in;
    }

    public void setHeight(int in)
    {
        this.height = in;
    }

    public void setDither(boolean in)
    {
        this.dither = in;
    }

    public void setMemory(int in)
    {
        this.memory = in;
    }

    public String getName()
    {
        return this.name;
    }

    public int getColors()
    {
        return this.colors;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public boolean getDither()
    {
        return this.dither;
    }

    public int getMemory()
    {
        return this.memory;
    }


}