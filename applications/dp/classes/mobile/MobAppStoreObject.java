import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import javax.microedition.rms.*;
import mobcon.message.*;
import mobcon.storeables.*;

public class MobAppStoreObject implements Storeable
{
public static final String AllId = "*";
private String id = "MobAppStoreObject";
public String mobileID = "*";
public static final java.lang.String AllText = "*";
public static final java.lang.String AllText2 = "*";
private java.lang.String text;
private java.lang.String text2;

public  MobAppStoreObject()
{
  text = AllText;
  text2 = AllText2;
}


public  MobAppStoreObject( String id,  java.lang.String text,  java.lang.String text2)
{
try{
setText(text);
setText2(text2);
setId(id);
}catch(Exception e){e.printStackTrace();}
}


public  MobAppStoreObject( java.lang.String text,  java.lang.String text2)
{
try{
setText(text);
setText2(text2);
}catch(Exception e){e.printStackTrace();}
}


public String getId()
{
return this.id;
}


public String getMobileID()
{
return this.mobileID;
}


public java.lang.String getText()
{
return text;
}


public java.lang.String getText2()
{
return text2;
}


public byte[] object2record()throws IOException
{
 ByteArrayOutputStream baos = new ByteArrayOutputStream();
 DataOutputStream outputStream = new DataOutputStream(baos);
byte[] out;
outputStream.writeUTF(this.getId());
outputStream.writeUTF(this.getMobileID());
outputStream.writeUTF(this.getText());
outputStream.writeUTF(this.getText2());
return baos.toByteArray();
}


public void record2object( byte[] rec)throws Exception
{
 ByteArrayInputStream bais = new ByteArrayInputStream(rec);
 DataInputStream inputStream = new DataInputStream(bais);
byte[] out;
this.setId(inputStream.readUTF());
this.setMobileID(inputStream.readUTF());
this.setText(inputStream.readUTF());
this.setText2(inputStream.readUTF());
}


public void setId( String id)
{
this.id = id;
}


public void setMobileID( String set)
{
this.mobileID = set;
}


public void setText( java.lang.String value)throws Exception
{
text = value;
}


public void setText2( java.lang.String value)throws Exception
{
text2 = value;
}


public String toString()
{
return this.id+" = "+this.text+" / "+this.text2+" / "+"";
}



} //EOC
