package mobcon.app;

import java.util.ArrayList;
import java.util.Iterator;

import mobcon.ct.*;
// Group of transformers, that are proccessed in parallel
public class TransformerGroup
{
    private int size;                   // size of the ArrayList
    private ArrayList transformers;     // transformers of the group

    private ClassTemplate myClassT;
    private ClassTemplate myStoreClassT;    // only used to keep the store
    private MixTemplate myMixT;

    public TransformerGroup()
    {
        size = 0;
        transformers = new ArrayList();

        myClassT = null;
        myMixT = new DefaultMixTemplate();
    }

    public TransformerGroup(Transformer t)
    {
        size = 0;
        transformers = new ArrayList();
        this.addTransformer(t);

        myClassT = null;
        myMixT = new DefaultMixTemplate();
    }

    public void addTransformer(Transformer t)
    {
        transformers.add(t);
        size++;
    }

    public int getSize()
    {
        return size;
    }

    public Iterator getIterator()
    {
        return transformers.iterator();
    }

    public ClassTemplate getClassTemplate()
    {
        return myClassT;
    }

    public void setClassTemplate(ClassTemplate ct)
    {
        myClassT = ct;

        //Setting the MixTemplate of the ClassTemplate
        myClassT.setMixTemplate(myMixT);
    }

    public ClassTemplate getStoreClassTemplate()
    {
        return myStoreClassT;
    }

    public void setStoreClassTemplate(ClassTemplate ct)
    {
        myStoreClassT = ct;

        //Setting the MixTemplate of the ClassTemplate
        myStoreClassT.setMixTemplate(myMixT);
    }

    public MixTemplate getMixTemplate()
    {
        return myMixT;
    }

    public void setMixTemplate(MixTemplate mt)
    {
        myMixT = mt;
    }

}