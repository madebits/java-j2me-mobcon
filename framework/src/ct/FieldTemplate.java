package mobcon.ct;

import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class FieldTemplate extends TaggedElement
{
    protected String access = "";
    protected String type = "";
    protected String name = "";
    protected String value = "";

    public FieldTemplate() {}

    public FieldTemplate(String tag)
    {
        this.setTag(tag);
    }

    public FieldTemplate(String tmpType, String tmpName)
    {
        type = tmpType;
        name = tmpName;
    }

    public String getID()
    {
        return type+" "+name;
    }

    public String getAccess()
    {
        return access;
    }

    public void setAccess(String tmpAccess)
    {
        this.access = tmpAccess;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String tmpType)
    {
        type = tmpType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String tmpName)
    {
        name = tmpName;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String tmpValue)
    {
        value = tmpValue;
    }

    //For declaration of fields
    public String toString2()
    {
        if(value == "")
        {
            return this.access + " " + type + " " + name;
        }else{
            return this.access + " " + type + " " + name + " = " + value;
        }
    }

    //Outputs only the name
    public String toString()
    {
        return this.type+" "+this.name;
    }

    public String stringRepresentation()
    {
        return this.toString();
    }

} //EOC
