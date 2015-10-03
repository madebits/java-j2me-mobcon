package mobcon.ct;

import java.util.Vector;

public class TaggedCode extends TaggedElement{
    private String code = "";

    public TaggedCode()
    {
        super();
    }

    public TaggedCode(String code)
    {
        tags = new Vector();
        this.code = code;
    }

    public TaggedCode(String code, String tag)
    {
        tags = new Vector();
        this.code = code;
        this.setTag(tag);
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String in)
    {
        this.code = in;
    }

    public void addCode(String in)
    {
        this.code = this.code+System.getProperty("line.separator")+in;
    }

    public String stringRepresentation()
    {
        return this.code;
    }
}