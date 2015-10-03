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

public class MobApp extends AbstractMobApp
{

public  MobApp()
{
super();
}


public void callTextField1()
{

        String text = "";
        text = getText();
        textField1 = new TextField("TextField1", text, 256, TextField.ANY);
    
}


public void callTextField2()
{

        String text = "";
        text = getText2();
        textField2 = new TextField("TextField2", text, 256, TextField.ANY);
    
}



} //EOC
