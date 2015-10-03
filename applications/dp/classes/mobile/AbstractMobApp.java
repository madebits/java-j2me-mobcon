import java.util.Hashtable;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.midlet.MIDletStateChangeException;
import mobcon.message.*;
import mobcon.storeables.*;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.RuntimeCryptoException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public abstract class AbstractMobApp extends MIDlet implements CommandListener
{
protected Command exitCommand;
 Command loadCommand;
 Command loadRemoteCommand;
protected Command nextCommand;
 Command saveCommand;
 Command saveRemoteCommand;
protected Display display;
 Form form0;
 Form form1;
protected MobAppStore store = null;
public static String CID = "1a32b8bb3a603ef3dc0cd2765dade2a3";
protected String firstForm = "form0";
protected String[] listElements;
protected TextBox messageBox;
 TextField textField1;
 TextField textField2;
 TextField welcome;

public  AbstractMobApp()
{
System.out.println("METHOD: "+" AbstractMobApp [@scr, @dp, @log] ");
exitCommand = new Command("Exit", Command.EXIT, 1);
nextCommand = new Command("Next", Command.SCREEN, 1);
try{
store = new MobAppStore("Test");
store.initStore();
}catch(Exception e){System.out.println(e);}
display = Display.getDisplay(this);
}


public void callForm0()
{
System.out.println("METHOD: "+" callForm0 [@scr, @log] ");
form0 = new Form("Form0");
form0.addCommand(exitCommand);
form0.addCommand(nextCommand);
form0.setCommandListener(this);
callWelcome();
form0.append(welcome);
display.setCurrent(form0);
}


public void callForm1()
{
System.out.println("METHOD: "+" callForm1 [@scr, @log] ");
form1 = new Form("Form1");
form1.addCommand(exitCommand);
saveCommand = new Command("Save Local", Command.ITEM, 1);
form1.addCommand(saveCommand);
saveRemoteCommand = new Command("Save Remote", Command.ITEM, 1);
form1.addCommand(saveRemoteCommand);
loadCommand = new Command("Load Local", Command.ITEM, 1);
form1.addCommand(loadCommand);
loadRemoteCommand = new Command("Load Remote", Command.ITEM, 1);
form1.addCommand(loadRemoteCommand);
form1.setCommandListener(this);
callTextField1();
form1.append(textField1);
callTextField2();
form1.append(textField2);
display.setCurrent(form1);
}


public void callLoadCommand()
{
System.out.println("METHOD: "+" callLoadCommand [@scr, @scr, @log] ");
callMessageBox("FromStore", store.storeToString());
}


public void callLoadRemoteCommand()
{
System.out.println("METHOD: "+" callLoadRemoteCommand [@scr, @scr, @log] ");
retrieve(); callForm1();
}


public void callMessageBox( String label,  String text)
{
System.out.println("METHOD: "+" callMessageBox [@scr, @log] ");
messageBox = new TextBox( label, text, 256, TextField.ANY );
display.setCurrent(messageBox);
}


public void callSaveCommand()
{
System.out.println("METHOD: "+" callSaveCommand [@scr, @scr, @log] ");
setText(textField1.getString()); setText2(textField2.getString());
}


public void callSaveRemoteCommand()
{
System.out.println("METHOD: "+" callSaveRemoteCommand [@scr, @scr, @log] ");
setText(textField1.getString()); setText2(textField2.getString()); store();
}


public abstract void callTextField1();

public abstract void callTextField2();

public void callWelcome()
{
String text = "";
System.out.println("METHOD: "+" callWelcome [@scr, @log] ");
text = "First form is made of persistent fields, that can be saved locally or remotely on server(Saving remotely, means always saving locally too!!!).";
welcome = new TextField("WELCOME!", text, 256, TextField.ANY);
}


public void callWelcome( String text)
{
System.out.println("METHOD: "+" callWelcome [@scr, @log] ");
welcome = new TextField("WELCOME!", text, 256, TextField.ANY);
}


public void commandAction( Command command,  Displayable screen)
{
System.out.println("METHOD: "+" commandAction [@log] ");
System.out.println("COMMAND: "+" '"+command.getLabel()+"' executed in screen '"+screen.getTitle()+"'");
if (command == exitCommand)
{
destroyApp(false);
notifyDestroyed();
}
if (command == nextCommand)
{
if (((Screen)display.getCurrent()).getTitle() == "Form0")
{
callForm1();
}
}
if (command == saveCommand)
{
if (((Screen)display.getCurrent()).getTitle() == "Form1")
{
callSaveCommand();
}
}
if (command == saveRemoteCommand)
{
if (((Screen)display.getCurrent()).getTitle() == "Form1")
{
callSaveRemoteCommand();
}
}
if (command == loadCommand)
{
if (((Screen)display.getCurrent()).getTitle() == "Form1")
{
callLoadCommand();
}
}
if (command == loadRemoteCommand)
{
if (((Screen)display.getCurrent()).getTitle() == "Form1")
{
callLoadRemoteCommand();
}
}
}


protected byte[] decrypt( byte[] in)
{
System.out.println("METHOD: "+" decrypt [@enc, @log] ");
byte[] bkey = mobcon.util.ByteArray.stringToByteArray("a17f3aa6a66dfb0f3624e116b263d10c");
KeyParameter key = new KeyParameter(bkey);
AESLightEngine blockCipher = new AESLightEngine();
CFBBlockCipher cfbCipher = new CFBBlockCipher(blockCipher, 8);
StreamCipher streamCipher = new StreamBlockCipher(cfbCipher);
byte[] dataBytes = new byte[in.length];
streamCipher.init(false, key);
streamCipher.processBytes(in, 0, in.length, dataBytes, 0);
return dataBytes;
}


public void destroyApp( boolean unconditional)
{
System.out.println("METHOD: "+" destroyApp [@main, @dp, @log] ");
store();
try{
store.closeStore();
}catch(Exception e){System.out.println(e);}
}


protected byte[] encrypt( byte[] in)
{
System.out.println("METHOD: "+" encrypt [@enc, @log] ");
byte[] bkey = mobcon.util.ByteArray.stringToByteArray("a17f3aa6a66dfb0f3624e116b263d10c");
KeyParameter key = new KeyParameter(bkey);
AESLightEngine blockCipher = new AESLightEngine();
CFBBlockCipher cfbCipher = new CFBBlockCipher(blockCipher, 8);
StreamCipher streamCipher = new StreamBlockCipher(cfbCipher);
byte[] dataBytes = new byte[in.length];
streamCipher.init(true, key);
streamCipher.processBytes(in, 0, in.length, dataBytes, 0);
return dataBytes;
}


public String getMobileID()
{
System.out.println("METHOD: "+" getMobileID [@dp, @log] ");
MobAppStoreObject o = store.getStoreObject();
String mobid = "*";
if(o.getMobileID().equals("*"))
{
try{
NetMessage message = new NetMessage();
message.setCID(CID);
message.setPID("01");
message.setIID("01");
message.setOID("5");
NetMessage ret = new NetMessage();
ret = CTX.getNetMessageResult(message);
System.out.println("Retrieved Object from server: "+ret);
StoreableStringData sd = new StoreableStringData();
sd.record2object(ret.data);
mobid = sd.getString();
o.setMobileID(mobid);
store.store(o);
}catch(Exception e){System.out.println(this.getClass().getName()+": "+e); }
}
return o.getMobileID();
}


public java.lang.String getText()
{
System.out.println("METHOD: "+" getText [@dp, @log] ");
MobAppStoreObject o = store.getStoreObject();
return o.getText();
}


public java.lang.String getText2()
{
System.out.println("METHOD: "+" getText2 [@dp, @log] ");
MobAppStoreObject o = store.getStoreObject();
return o.getText2();
}


public void pauseApp()
{
System.out.println("METHOD: "+" pauseApp [@main, @log] ");
}


public void retrieve()
{
System.out.println("METHOD: "+" retrieve [@dp, @enc, @log] ");
try{
NetMessage message = new NetMessage();
message.setCID(CID);
message.setPID("01");
message.setIID("01");
message.setOID("2");
StoreableStringData ssa = new StoreableStringData();
ssa.setString(this.getMobileID());
message.data = ssa.object2record();
NetMessage ret = new NetMessage();
ret = CTX.getNetMessageResult(message);
System.out.println("Retrieved Object from server: "+ret);
if(!ret.getOID().equals("FF")){
MobAppStoreObject retObj = new MobAppStoreObject();
byte[] decdat;
decdat = decrypt(ret.data);
retObj.record2object(decdat);
store.store(retObj);
}else{
MobAppStoreObject retObj = new MobAppStoreObject();
store.store(retObj);
}
}catch(Exception e){System.out.println(this.getClass().getName()+": "+e); }
}


public void retrieveObject( String id,  Storeable sa)
{
System.out.println("METHOD: "+" retrieveObject [@dp, @enc, @log] ");
try{
NetMessage message = new NetMessage();
message.setCID(CID);
message.setPID("01");
message.setIID("01");
message.setOID("4");
message.data = id.getBytes();
NetMessage ret = new NetMessage();
ret = CTX.getNetMessageResult(message);
System.out.println("Retrieved Object from server");
byte[] decdat;
decdat = decrypt(ret.data);
sa.record2object(decdat);
}catch(Exception e){System.out.println(this.getClass().getName()+": "+e); }
}


public void setText( java.lang.String in)
{
System.out.println("METHOD: "+" setText [@dp, @log] ");
try{
MobAppStoreObject storeObject = null;
storeObject = store.getStoreObject();
storeObject.setText(in);
store.store(storeObject);
} catch(Exception e){System.out.println(e); }
}


public void setText2( java.lang.String in)
{
System.out.println("METHOD: "+" setText2 [@dp, @log] ");
try{
MobAppStoreObject storeObject = null;
storeObject = store.getStoreObject();
storeObject.setText2(in);
store.store(storeObject);
} catch(Exception e){System.out.println(e); }
}


public void startApp()
{
System.out.println("METHOD: "+" startApp [@main, @scr, @dp, @log] ");
viewDisplay(firstForm);
}


public void store()
{
System.out.println("METHOD: "+" store [@dp, @enc, @log] ");
try{
MobAppStoreObject storeObject = null;
storeObject = store.getStoreObject();
NetMessage message = new NetMessage();
message.setCID(CID);
message.setPID("01");
message.setIID("01");
message.setOID("1");
StoreableStringData ssa = new StoreableStringData();
ssa.setString(this.getMobileID());
byte[] outData = encrypt(storeObject.object2record());
ssa.setData(outData);
message.data = ssa.object2record();
CTX.sendNetMessage(message);
}catch(Exception e){System.out.println(this.getClass().getName()+": "+e); }
}


public String storeObject( Storeable sa)
{
System.out.println("METHOD: "+" storeObject [@dp, @enc, @log] ");
try{
NetMessage message = new NetMessage();
message.setCID(CID);
message.setPID("01");
message.setIID("01");
message.setOID("3");
message.data = encrypt(sa.object2record());
NetMessage ret = new NetMessage();
ret = CTX.getNetMessageResult(message);
return new String(ret.data);
}catch(Exception e){System.out.println(this.getClass().getName()+": "+e); return null;}
}


public void viewDisplay( String displayName)
{
System.out.println("METHOD: "+" viewDisplay [@scr, @log] ");
if(displayName.equals("form0")) callForm0();
if(displayName.equals("form1")) callForm1();
}



} //EOC
