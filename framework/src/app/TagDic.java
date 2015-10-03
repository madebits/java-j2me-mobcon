package mobcon.app;

import java.util.PropertyResourceBundle;
import java.io.FileInputStream;
import java.io.InputStream;

public class TagDic
{
    //prefix is the prefix to all other tags
    private String prefix = "";
    //Here are the keys stored
    private PropertyResourceBundle prb;

    public TagDic()
    {
    }

    public void init(InputStream is)
    {
        //Reads pairs of Strings (key=value) from InputStream coming from a JAR-File
        try{
            prb = new PropertyResourceBundle(is);
            prefix = prb.handleGetObject("PREFIX").toString();
        }catch (Exception e){
            System.out.println(this.getClass().getName()+": Error in reading Tag-Dic "+e);
        }

    }

    public void init(String fileName)
    {
        //Reads pairs of Strings (key=value) from file
        try{
            prb = new PropertyResourceBundle(new FileInputStream(fileName));
            prefix = prb.handleGetObject("PREFIX").toString();
        }catch (Exception e){
            System.out.println(this.getClass().getName()+": Error in reading Tag-Dic "+e);
        }

    }

    public String getTag(String key)
    {
        // returns tag in Tags for the key(property)
        return (this.prefix + "." + prb.handleGetObject(key).toString());
    }

    public String getPrefix()
    {
        // returns Prefix of all Tags
        return this.prefix;
    }
}
