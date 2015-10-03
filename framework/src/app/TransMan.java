package mobcon.app;

import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileReader;

import mobcon.ct.*;
//TODO: Processing Transformers in parallel
public class TransMan
{
    private static boolean debug = false;

    private File transDir;              //dir where transformers are held
    private File outCT;                 //dir where transformed classes are put in
    private Hashtable transClasses;     //key = name of transformed class, value = ClassTemplate of this class
    private ArrayList transformerGroups;//List of TransformerGroups
    private TransformerGroup currentTransGroup;   //The current transformer, which is working now
    private Iterator transGroupIt;

    private final static String depFileName = "depend.xml";

    public TransMan()
    {
        transformerGroups = new ArrayList();
    }

    public void setTransDir(String dir)
    {
        transDir = new File(dir);
    }

    public File getTransDir()
    {
        return this.transDir;
    }

    private File[] getJars()
    {
        //gets only the files with extension *.jar
        return this.transDir.listFiles(new JarFilter());
    }

    public void initTransformers()
    {
        File depFile = new File(transDir.toString()+File.separator+depFileName);

        if(depFile.exists())
        {
            System.out.println(this.getClass().getName()+": READING WORKFLOW from "+depFileName+"...");
            System.out.println();
            this.readDepFile(depFile);

        } else {
            System.out.println(this.getClass().getName()+": BUILDING WORKFLOW from Manifest...");
            System.out.println();
            this.buildDependencies();
        }

        //Set currentTrans to the first used transformer
        transGroupIt = transformerGroups.iterator();
    }

    //reads the extern workflow file
    public void readDepFile(File depFile)
    {
        File[] transJars = this.getJars();

        TransformerGroup transGroup;
        Transformer t;
        String TID;
        String IID;

        DepFileReader depFR = new DepFileReader(transDir.toString()+File.separator+depFileName);
        while(depFR.nextGroup())
        {
            transGroup = new TransformerGroup();

            while(depFR.nextTransformer())
            {
                TID = depFR.getTID();
                IID = depFR.getIID();
                t = Transformer.getTransformerFromFiles(TID, IID, this.getJars());
                if(depFR.getTransformerMergerClass() != null)
                {
                    System.out.println(this.getClass().getName()+": TemplateMixClass = "+depFR.getTransformerMergerClass());
                    try
                    {
                        MixTemplate mt = (MixTemplate) Class.forName(depFR.getTransformerMergerClass()).newInstance();
                        t.setMixTemplate(mt);
                    } catch(Exception e) { System.out.println(this.getClass().getName()+": Mixing error "+e); }
                }
                transGroup.addTransformer(t);
            }

            if(depFR.getGroupMergerClass() != null)
            {
                System.out.println(this.getClass().getName()+": TemplateMixClass = "+depFR.getGroupMergerClass());
                try
                {
                    MixTemplate mt = (MixTemplate) Class.forName(depFR.getGroupMergerClass()).newInstance();
                    transGroup.setMixTemplate(mt);
                } catch(Exception e) { System.out.println(this.getClass().getName()+": Mixing error "+e); }
            }
            transformerGroups.add(transGroup);
        }
    }

    //Builds the dependencies and inserts the transformers in the right order into the transformer-list
    //which are executed one after another
    public void buildDependencies()
    {
        int i;
        File[] transJars = this.getJars();

        DepBuilder db = new DepBuilder();
        DepElement de;
        Transformer t;
        TransformerGroup transGroup;

        // Creating TreeSets for the All-Quantor
        TreeSet ALL_TRANSFORMERS_UB = new TreeSet();
        TreeSet ALL_TRANSFORMERS_UA = new TreeSet();
        for(i=0; i < transJars.length; i++)
        {
            t = new Transformer(transJars[i]);
            ALL_TRANSFORMERS_UB.add(t.getID());
            ALL_TRANSFORMERS_UA.add(t.getID());
        }

        //Insert all containers to the DepBuilder
        TreeSet tmp;
        for(i=0; i < transJars.length; i++)
        {
            t = new Transformer(transJars[i]);

            if(t.useAllBefore())
            {
                ALL_TRANSFORMERS_UB.remove(t.getID());
                t.setUB((TreeSet)ALL_TRANSFORMERS_UB.clone());
            }

            if(t.useAllAfter())
            {
                ALL_TRANSFORMERS_UA.remove(t.getID());
                t.setUA((TreeSet)ALL_TRANSFORMERS_UA.clone());
            }

            de = new DepElement(t, t.getID(), t.getUB(), t.getUA());

            db.insertElement(de);
        }

        if(debug) System.out.println(this.getClass().getName()+": Before ordering "+db.toString());
        db.orderElements();

        if(debug) System.out.println(this.getClass().getName()+": Before grouping "+db.toString());
        //Grouping Elements for executing them in parallel
        db.groupElements();
        System.out.println(this.getClass().getName()+": CHECKING DEPENDENCIES...");
        //if(debug)
        System.out.println(this.getClass().getName()+": "+db.toString());
        System.out.println(this.getClass().getName()+": DEPENDENCY-CHECK = "+db.checkDepend()+System.getProperty("line.separator"));

        Iterator it;
        DepGroup dg;

        //Adding the transformers in the right order to the transformer-list
        for (Iterator iter = db.getGroups().iterator(); iter.hasNext() ;)
        {
            transGroup = new TransformerGroup();

            dg = (DepGroup)iter.next();
            for (it = dg.getElementIterator(); it.hasNext() ;)
            {
                t = (Transformer)((DepElement)it.next()).getElement();
                transGroup.addTransformer(t);
            }

            transformerGroups.add(transGroup);
        }

    }

    public void setOutCT(String outFile)
    {
        outCT = new File(outFile);
    }

    public File getOutCT()
    {
        return this.outCT;
    }

    public void writeOutCT(String out)
    {
        try
        {
            FileWriter fOut = new FileWriter(outCT);
            fOut.write(out);
            fOut.close();
            System.out.println(this.getClass().getName()+": WRITING CT = "+outCT);
        } catch(Exception e){System.out.println(this.getClass().getName()+": Error in writing out "+e);}
    }

    //Dont know if we need this methods (with fields) following here...
    public void setTransClass(String className, ClassTemplate classTemplate)
    {
        transClasses.put(className, classTemplate);
    }

    public ClassTemplate getTransClass(String className)
    {
        return (ClassTemplate)transClasses.get(className);
    }

    public void resetTransformerGroupIterator()
    {
        transGroupIt = transformerGroups.iterator();
    }

    public TransformerGroup nextTransformerGroup()
    {
        if(transGroupIt.hasNext())
        {
            currentTransGroup = (TransformerGroup)transGroupIt.next();
        }else{
            currentTransGroup = null;
        }

        return currentTransGroup;
    }

    public TransformerGroup getCurrentTransformerGroup()
    {
        return currentTransGroup;
    }

    public static void main(String[] args)
    {

    }
}//EOC

//class for filtering the jar-files from a directory
class JarFilter implements java.io.FilenameFilter
{
    public boolean accept(File dir, String name)
    {
        if(name.endsWith(".jar")) return true;
        else return false;
    }

}//EOC