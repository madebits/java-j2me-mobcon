package mobcon.ct;

import java.util.Vector;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;

public class MethodTemplate extends TaggedElement
{
    public String type = "";
    public String name = "";
    public String access = "";
    public TreeMap exceptions;
    public ArrayList args;
    public boolean abstractMethod = false;
    //In fields are stored fields, which are declared in the method
    public ArrayList fields;
    //Begin: All actions to prepare, initialize or pre-check
    public ArrayList begin;
    //Body: Workflow of the method
    public ArrayList body;
    //End: Cleaning up, returning values
    public ArrayList end;

    protected MixTemplate mixTemplate;

    public MethodTemplate()
    {
        fields = new ArrayList();
        args = new ArrayList();
        exceptions = new TreeMap();
        begin = new ArrayList();
        body = new ArrayList();
        end = new ArrayList();

        mixTemplate = new DefaultMixTemplate();
    }

    public MethodTemplate(String tag)
    {
        fields = new ArrayList();
        args = new ArrayList();
        exceptions = new TreeMap();
        begin = new ArrayList();
        body = new ArrayList();
        end = new ArrayList();

        mixTemplate = new DefaultMixTemplate();

        this.setTag(tag);
    }

    public String getID()
    {
        //ID is Method-Name plus all the types in parameterlist (for overloading), not perfect like this...
        String id = this.name;

        for (Iterator iter = args.iterator(); iter.hasNext() ;)
        {
            id = id + " " +((FieldTemplate)iter.next()).getID();
        }

        return id;
    }

    public String getAccess()
    {
        if(abstractMethod) return access+" abstract";
        else return access;
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

    public void setAbstract(boolean set)
    {
        this.abstractMethod = set;
    }

    public boolean getAbstract()
    {
        return this.abstractMethod;
    }

    public void addException(String exception)
    {
        if(exceptions.get(exception) == null)
        {
            exceptions.put(exception, exception);
        } else {
            // fail silently
            System.out.println(this.getClass().getName()+": Exception exists");
        }

    }

    public MethodTemplate mixMethodTemplates(MethodTemplate mt2)
    {
        return this.mixTemplate.mixMethodTemplates(this, mt2);
    }

    /************ Parameter ************/
    public void addParameter(FieldTemplate ft)
    {
        args.add(ft);
    }

    public void addParameter(FieldTemplate ft, String tag)
    {
        ft.setTag(tag);
        args.add(ft);
    }

    public String getArgs()
    {
        return args.toString();
    }


    public ArrayList getParameters()
    {
        return this.args;
    }

    /************ Field ************/
    public void addField(FieldTemplate ft)
    {
        fields.add(ft);
    }

    public void addField(FieldTemplate ft, String tag)
    {
        ft.setTag(tag);
        fields.add(ft);
    }


    /************ CODE ************/
    public String getBegin()
    {
        return begin.toString();
    }

    public String getBody()
    {
        return body.toString();
    }

    public String getEnd()
    {
        return end.toString();
    }

    public void addBegin(TaggedCode cs)
    {
        cs.setTag("begin");
        begin.add(cs);
    }

    public void addBegin(String code)
    {
        TaggedCode cs = new TaggedCode(code);
        this.addBegin(cs);
    }

    public void addBegin(String code, String tag)
    {
        TaggedCode cs = new TaggedCode(code);
        cs.setTag(tag);

        this.addBegin(cs);
    }

    public void addBody(TaggedCode cs)
    {
        cs.setTag("body");
        body.add(cs);
    }

    public void addBody(String code)
    {
        TaggedCode cs = new TaggedCode(code);
        this.addBody(cs);
    }

    public void addBody(String code, String tag)
    {
        TaggedCode cs = new TaggedCode(code);
        cs.setTag(tag);

        this.addBody(cs);
    }

    public void addEnd(TaggedCode cs)
    {
        cs.setTag("end");
        end.add(cs);
    }

    public void addEnd(String code)
    {
        TaggedCode cs = new TaggedCode(code);
        this.addEnd(cs);
    }

    public void addEnd(String code, String tag)
    {
        TaggedCode cs = new TaggedCode(code);
        cs.setTag(tag);

        this.addEnd(cs);
    }


    /************************ Snippet Stuff ************************/
    // returns Vector with all the TaggedCodes from this Method
    public Vector getAllSnippets()
    {
        Vector out = new Vector();

        if(begin.size() > 0)
        {
            for (Iterator iter = begin.iterator(); iter.hasNext() ;)
            {
                out.add(iter.next());
            }
        }

        if(body.size() > 0)
        {
            for (Iterator iter = body.iterator(); iter.hasNext() ;)
            {
                out.add(iter.next());
            }
        }

        if(end.size() > 0)
        {
            for (Iterator iter = end.iterator(); iter.hasNext() ;)
            {
                out.add(iter.next());
            }
        }

        return out;
    }

    public Vector getSnippets(String[] inTags)
    {
        int i;

        Vector allSnippets = this.getAllSnippets();
        Vector out = new Vector();
        TaggedCode tmp;

        for(i=0; i < inTags.length; i++)
        {
            if(allSnippets.size() > 0)
            {
                for (Iterator iter = allSnippets.iterator(); iter.hasNext() ;)
                {
                    tmp = (TaggedCode)iter.next();
                    if(tmp.getTag(inTags[i])) out.add(tmp);
                }
            }
        }

        return out;
    }

    public Vector getSnippets(String tag)
    {
        String[] inTags = new String[1];
        inTags[0] = tag;

        return this.getSnippets(inTags);
    }


    /************************ toString() ************************/
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getAccess()).append(" ").append(getType()).append(" ").append(getName()).append("(");


        if(args.size() > 0)
        {
            for (Iterator iter = args.iterator(); iter.hasNext() ;)
            {
                sb.append(((FieldTemplate)iter.next()).toString2());
                if(iter.hasNext())
                {
                    sb.append(", ");
                }
            }

        }
        sb.append(")");

        if(exceptions.size() > 0)
        {
            sb.append("throws ");
            for (Iterator iter = exceptions.values().iterator(); iter.hasNext() ;)
            {
                sb.append(iter.next());
                if(iter.hasNext())
                {
                    sb.append(",");
                }
            }

        }

        if(!abstractMethod)
        {
            sb.append(System.getProperty("line.separator")).append("{").append(System.getProperty("line.separator"));

            if(fields.size() > 0)
            {
                for (Iterator iter = fields.iterator(); iter.hasNext() ;)
                {
                    sb.append(((FieldTemplate)iter.next()).toString2()).append(";").append(System.getProperty("line.separator"));
                }

            }

            if(begin.size() > 0)
            {
                for (Iterator iter = begin.iterator(); iter.hasNext() ;)
                {
                    sb.append(iter.next().toString()).append(System.getProperty("line.separator"));
                }
            }
            if(body.size() > 0)
            {
                for (Iterator iter = body.iterator(); iter.hasNext() ;)
                {
                    sb.append(iter.next().toString()).append(System.getProperty("line.separator"));
                }
            }
            if(end.size() > 0)
            {
                for (Iterator iter = end.iterator(); iter.hasNext() ;)
                {
                    sb.append(iter.next().toString()).append(System.getProperty("line.separator"));
                }
            }

            sb.append("}").append(System.getProperty("line.separator"));
        } else {
            sb.append(";");
        }
        return sb.toString();
    }

    /** Needed from TaggedElement **/
    public String stringRepresentation()
    {
        return this.toString();
    }

    public MethodTemplate cloneMethod()
    {
        MethodTemplate m = new MethodTemplate();

        m.setAccess(this.access);
        m.setName(getName());
        m.setType(getType());
        m.setAbstract(this.abstractMethod);
        m.tags = this.tags;

        if(args.size() > 0)
        {
            for (Iterator iter = args.iterator(); iter.hasNext() ;)
            {
                m.addParameter((FieldTemplate)iter.next());
            }

        }

        if(exceptions.size() > 0)
        {
            for (Iterator iter = exceptions.values().iterator(); iter.hasNext() ;)
            {
                m.addException((String)iter.next());
            }
        }

        if(fields.size() > 0)
        {
            for (Iterator iter = fields.iterator(); iter.hasNext() ;)
            {
                m.addField((FieldTemplate)iter.next());
            }
        }

        if(begin.size() > 0)
        {
            for (Iterator iter = begin.iterator(); iter.hasNext() ;)
            {
                m.addBegin((TaggedCode)(iter.next()));
            }
        }
        if(body.size() > 0)
        {
            for (Iterator iter = body.iterator(); iter.hasNext() ;)
            {
                m.addBody((TaggedCode)(iter.next()));
            }
        }
        if(end.size() > 0)
        {
            for (Iterator iter = end.iterator(); iter.hasNext() ;)
            {
                m.addEnd((TaggedCode)(iter.next()));
            }
        }

        return m;
    }
} //EOC
