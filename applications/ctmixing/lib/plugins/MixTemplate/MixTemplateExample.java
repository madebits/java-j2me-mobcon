import java.util.Vector;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import mobcon.ct.*;

public class MixTemplateExample implements MixTemplate
{
    public MixTemplateExample()
    {
    }

    public ClassTemplate mixClassTemplates(ClassTemplate ct1, ClassTemplate ct2)
    {
        if(!ct1.getID().equals(ct2.getID()))
        {
            // throw new Exception("class templs do not match");
            System.out.println(this.getClass().getName()+": "+ct1.getClass().getName()+": "+ct1.getID()+" != "+ct2.getID()+": Not the same Class Name!");
        }

        ct1.imports.putAll(ct2.imports);
        ct1.interfaces.putAll(ct2.interfaces);
        if(ct1.getBaseClass().equals("")) ct1.setBaseClass(ct2.getBaseClass());

        //ct1.fields.putAll(ct2.fields);
        for (Iterator it = ct2.fields.values().iterator(); it.hasNext() ;)
        {
            FieldTemplate ft = (FieldTemplate)it.next();
            if(!(ft.getName().equals("disp")))
            {
                ct1.addField(ft);
                System.out.println("Field name: "+ft.getName());
            }
        }


        if(ct2.methods.size() > 0)
        {
            String methID="";
            String code;
            for (Iterator mn2 = ct2.methods.values().iterator(); mn2.hasNext() ;)
            {
                MethodTemplate m2 = (MethodTemplate) mn2.next();
                methID = m2.getID();
                MethodTemplate m = (MethodTemplate)ct1.methods.get(methID);

                for (Iterator it = m2.getAllSnippets().iterator(); it.hasNext() ;)
                {
                    TaggedCode tc = (TaggedCode)it.next();
                    code = tc.getCode();
                    tc.setCode(code.replaceAll("disp", "display"));
                }

                if(m != null)
                {
                    m.mixMethodTemplates(m2);

                    ct1.methods.put(methID, m);
                }else{
                    ct1.methods.put(methID, m2);
                }
            }
        }

        return ct1;
    }

    public MethodTemplate mixMethodTemplates(MethodTemplate mt1, MethodTemplate mt2)
    {
        if(!mt1.getID().equals(mt2.getID())) System.out.println(this.getClass().getName()+": "+mt1.getClass().getName()+": Not the same Method Name!");// throw new Exception("method templs do not match");
        mt1.exceptions.putAll(mt2.exceptions);
        mt1.fields.addAll(mt2.fields);
        mt1.begin.addAll(mt2.begin);
        mt1.body.addAll(mt2.body);
        mt1.end.addAll(mt2.end);

        for (Iterator it = mt2.getTags().iterator(); it.hasNext() ;)
        {
            mt1.setTag((String)it.next());
        }

        return mt1;
    }

}