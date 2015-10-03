package mobcon.ct;

import java.util.Vector;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Collection;

public class ClassTemplate{
    public String name = "";
    public String access = "";
    public String baseClass = "";

    public TreeMap imports;
    public TreeMap interfaces;
    public TreeMap methods;
    public TreeMap fields;

    public MixTemplate mixTemplate;

    public ClassTemplate()
    {
        imports = new TreeMap();
        interfaces = new TreeMap();
        methods = new TreeMap();
        fields = new TreeMap();
        mixTemplate = new DefaultMixTemplate();

    }

    public ClassTemplate(String tmpAccess, String tmpName)
    {
        imports = new TreeMap();
        interfaces = new TreeMap();
        methods = new TreeMap();
        fields = new TreeMap();

        mixTemplate = new DefaultMixTemplate();

        this.name = tmpName;
        this.access = tmpAccess;
    }

    public String getID()
    {
        return name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String tmpName)
    {
        this.name = tmpName;
    }

    public String getBaseClass()
    {
        return baseClass;
    }

    public void setBaseClass(String tmpBaseClass)
    {
        this.baseClass = tmpBaseClass;
    }

    public String getAccess()
    {
        return access;
    }

    public void setAccess(String tmpAccess)
    {
        this.access = tmpAccess;
    }

    public void addInterface(String iName)
    {
        interfaces.put(iName, iName);
    }

    public void addImport(String iName)
    {
        imports.put(iName, iName);
    }

    public void setMixTemplate(MixTemplate mt)
    {
        mixTemplate = mt;
    }

    public MixTemplate getMixTemplate()
    {
        return mixTemplate;
    }

    public ClassTemplate mixClassTemplates(ClassTemplate ct2)
    {
        return this.mixTemplate.mixClassTemplates(this, ct2);
    }

    public ClassTemplate mixClassTemplates(ClassTemplate ct1, ClassTemplate ct2)
    {
        return this.mixTemplate.mixClassTemplates(ct1, ct2);
    }


    /************************ Method ************************/
    public void addMethod(MethodTemplate mt)
    {
        MethodTemplate checkMethod = (MethodTemplate)methods.get(mt.getID());
        if(checkMethod == null)
        {
            methods.put(mt.getID(), mt);
        } else {
            mt.mixMethodTemplates(checkMethod);
            mt.getTags().addAll(checkMethod.getTags());
            methods.put(mt.getID(), mt);
        }
    }

    public void addMethod(MethodTemplate m, String tag)
    {
        MethodTemplate mt = m;
        mt.setTag(tag);

        this.addMethod(mt);
    }

    /************************ Field ************************/
    public void addField(FieldTemplate ft)
    {
        if(fields.get(ft.getID()) == null)
        {
            fields.put(ft.getID(), ft);
        } else {
            // fail silently
            System.out.println(this.getClass().getName()+": Field exists in "+this.getName()+"("+ft.getType()+" "+ft.getName()+")");
        }

    }

    public void addField(FieldTemplate ft, String tag)
    {
        ft.setTag(tag);
        this.addField(ft);
    }

    /************************ getInternals ************************/
    public Collection getMethods()
    {
        return this.methods.values();
    }

    public Collection getFields()
    {
        return this.fields.values();
    }

    public Collection getImports()
    {
        return this.imports.values();
    }

    public Collection getInterfaces()
    {
        return this.interfaces.values();
    }


    /************************ toString() ************************/
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if(imports.size() > 0)
        {

            for (Iterator iter = imports.values().iterator(); iter.hasNext() ;)
            {
                sb.append("import ");
                sb.append(iter.next()).append(";").append(System.getProperty("line.separator"));
            }

        }
        sb.append(System.getProperty("line.separator"));

        sb.append(getAccess()).append(" class ").append(getName());
        if(!baseClass.equals(""))
        {
            sb.append(" extends "+getBaseClass());
        }

        if(interfaces.size() > 0)
        {
            sb.append(" implements ");

            for (Iterator iter = interfaces.values().iterator(); iter.hasNext() ;)
            {
                sb.append(iter.next());
                if(iter.hasNext())
                {
                    sb.append(", ");
                }
            }

        }

        sb.append(System.getProperty("line.separator")).append("{").append(System.getProperty("line.separator"));

        if(fields.size() > 0)
        {
            for (Iterator iter = fields.values().iterator(); iter.hasNext() ;)
            {

                sb.append(((FieldTemplate)iter.next()).toString2()).append(";").append(System.getProperty("line.separator"));
            }

        }

        sb.append(System.getProperty("line.separator"));

        if(methods.size() > 0)
        {
            for (Iterator iter = methods.values().iterator(); iter.hasNext() ;)
            {
                sb.append(((MethodTemplate)iter.next()).toString()).append(System.getProperty("line.separator"));
                sb.append(System.getProperty("line.separator"));
            }

        }

        sb.append(System.getProperty("line.separator")).append("} //EOC").append(System.getProperty("line.separator"));


        return sb.toString();
    }

} //EOC