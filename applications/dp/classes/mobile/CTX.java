import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import mobcon.message.*;

public class CTX
{
public static String STR_EMPTY = "";
public static int REMOTE = 3;
public static int REMOTE_ALWAYS = 2;
public static int REMOTE_AS_NEEDED = 3;
public static int REMOTE_NEVER = 1;

public  CTX()
{
}


public static NetMessage getNetMessageResult( NetMessage o)throws Exception
{
return MU.getNetMessageResult(o);
}


public static String noNull( String s)
{
return (s == null) ? STR_EMPTY : s;
}


public static void sendNetMessage( NetMessage o)throws Exception
{
MU.sendNetMessage(o);
}


public static boolean validate( String value,  int min,  int max)
{
value = noNull(value);
return validate(value.length(), min, max);
}


public static boolean validate( int value,  int min,  int max)
{
if((value < min) || (value > max)) return false;
return true;
}



} //EOC
