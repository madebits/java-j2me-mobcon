package mobcon.app;

import java.util.jar.Manifest;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TreeSet;
import java.util.Vector;
import java.io.File;
import java.util.StringTokenizer;

import mobcon.ct.*;
public class Transformer
{
    private static boolean debug = false;

    private static final String ALL_QUANTOR = "ALL";
    private String jarFileName;
    private String transformerFileName;
    private String transformerName;
    private String tagFileName;
    private String serverFileName;
    private TagDic tagDic;      //The TagDic which belongs to the transformer

    private String TID;            //Container-Type ID
    private String IID;            //Container-Type-Instance ID

    private TreeSet UB;         //Use-before
    private TreeSet UA;         //Use-after

    private ClassTemplate myClassT;
    private Vector mobileClassT;    // used to keep helper-classes for mobile device, that are generated (like the stores for dp-concern)
    private Vector serverClassT;    // used to keep helper-classes for server-side, that are generated

    private MixTemplate myMixT;

    private boolean serverFileUsed = false;

    public Transformer()
    {
        UB = new TreeSet();
        UA = new TreeSet();
        mobileClassT = new Vector();
        serverClassT = new Vector();

        myClassT = null;
        myMixT = new DefaultMixTemplate();
    }

    public Transformer(String jarName)
    {
        UB = new TreeSet();
        UA = new TreeSet();
        mobileClassT = new Vector();
        serverClassT = new Vector();

        myClassT = null;
        myMixT = new DefaultMixTemplate();

        JarFile jf;

        try{
            jf = new JarFile(jarName);
            this.init(jf);
        }catch(Exception e){System.out.println(this.getClass().getName()+": Error in Opening Jar-File: "+e); }
    }

    public Transformer(File jarFile)
    {
        UB = new TreeSet();
        UA = new TreeSet();
        mobileClassT = new Vector();
        serverClassT = new Vector();

        myClassT = null;
        myMixT = new DefaultMixTemplate();

        JarFile jf;

        try{
            jf = new JarFile(jarFile);
            this.init(jf);
        }catch(Exception e){System.out.println(this.getClass().getName()+": Error in Opening Jar-File: "+e); }
    }

    //opens the jar-file and stores all the information from the manifest in the class-fields
    public void init(JarFile jf)
    {
        if(debug) System.out.println(this.getClass().getName()+": Opening JAR-File "+jf.getName()+" ...");

        try{
            Manifest m = jf.getManifest();
            if(m==null)
            {
                System.out.println(this.getClass().getName()+": No Manifest-File found");
                return;
            }

            this.getUBValues(m.getAttributes("Dependencies").getValue("Use-Before"));
            this.getUAValues(m.getAttributes("Dependencies").getValue("Use-After"));

            jarFileName = jf.getName();
            transformerName = m.getAttributes("Transformer").getValue("Transformer-Name");
            transformerFileName = m.getAttributes("Transformer").getValue("File-Name");
            tagFileName = m.getAttributes("Transformer").getValue("Tag-File");
            this.setTID(m.getAttributes("Transformer").getValue("TID"));
            this.setIID(m.getAttributes("Transformer").getValue("IID"));
            serverFileName = m.getAttributes("Transformer").getValue("Server-File");

            this.setUpTagDic();
        }catch(Exception e){ System.out.println(this.getClass().getName()+": Error in Reading Jar-File: "+e); }
    }

    public void getUBValues(String line)
    {
        this.getDepValues(UB, line);
    }

    public void getUAValues(String line)
    {
        this.getDepValues(UA, line);
    }

    //Inits the TagDic that belongs to the transformer
    public void setUpTagDic()
    {
        tagDic = new TagDic();
        tagDic.init(this.getTagFile());
    }

    public TagDic getTagDic()
    {
        return tagDic;
    }

    //Reads the dependencies-values speparated by "/" from the manifest
    private void getDepValues(TreeSet in, String line)
    {
        StringTokenizer st = new StringTokenizer(line, "/");
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken("/");
            if(token.length() == 1) {
                token = "0"+token;
            }
            in.add(token);
        }

    }

    public String getTransName()
    {
        return transformerName;
    }

    public String getTransFileName()
    {
        return transformerFileName;
    }

    public String getTagFileName()
    {
        return tagFileName;
    }

    public boolean useAllBefore()
    {
        if(this.UB.size() > 0)
        {
            return ((String)this.UB.first()).equals(ALL_QUANTOR);
        }else{
            return false;
        }

    }

    public boolean useAllAfter()
    {
        if(this.UA.size() > 0)
        {
            return ((String)this.UA.first()).equals(ALL_QUANTOR);
        }else{
            return false;
        }
    }

    public TreeSet getUB()
    {
        return UB;
    }

    public TreeSet getUA()
    {
        return UA;
    }

    public void setUB(TreeSet set)
    {
        this.UB = set;
    }

    public void setUA(TreeSet set)
    {
        this.UA = set;
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

    public Vector getMobileClassTemplates()
    {
        return mobileClassT;
    }

    public void addMobileClassTemplate(ClassTemplate ct)
    {
        //Setting the MixTemplate of the ClassTemplate
        ct.setMixTemplate(myMixT);
        mobileClassT.add(ct);
    }

    public Vector getServerClassTemplates()
    {
        return serverClassT;
    }

    public void addServerClassTemplate(ClassTemplate ct)
    {
        //Setting the MixTemplate of the ClassTemplate
        ct.setMixTemplate(myMixT);
        serverClassT.add(ct);
    }

    public MixTemplate getMixTemplate()
    {
        return myMixT;
    }

    public void setMixTemplate(MixTemplate mt)
    {
        myMixT = mt;
    }

    //Gets the InputStream to the server-file in the Jar-file
    public InputStream getServerFile()
    {
        if(this.getServerFileName() == null) return null;

        try{
            JarFile jf = new JarFile(jarFileName);
            return (jf.getInputStream(jf.getJarEntry(serverFileName+".java")));

        }catch(Exception e){ System.out.println(this.getClass().getName()+": Error reading Server-File from jar = "+e); }

        return null;
    }


    //Gets the InputStream to the transformer.vm-file in the Jar-file
    public InputStream getTransFile()
    {
        try{
            JarFile jf = new JarFile(jarFileName);
            return (jf.getInputStream(jf.getJarEntry(transformerFileName)));

        }catch(Exception e){ System.out.println(this.getClass().getName()+": Error in opening transformer-file"+e); }

        return null;
    }

    //Gets the InputStream to the tag-file in the Jar-file
    public InputStream getTagFile()
    {
        try{
            JarFile jf = new JarFile(jarFileName);
            return (jf.getInputStream(jf.getJarEntry(tagFileName)));

        }catch(Exception e){ System.out.println(this.getClass().getName()+": Error in opening tag-file"+e); }

        return null;
    }

    public String getServerFileName()
    {
        return this.serverFileName;
    }

    //ID-String
    public String getID()
    {
        return this.getTID()+"."+this.getIID();
    }

    public String getTID()
    {
        return this.TID;
    }

    public String getIID()
    {
        return this.IID;
    }

    public void setTID(String set)
    {
        if(set.length() == 1) {
            set = "0"+set;
        }
        this.TID = set;
    }

    public void setIID(String set)
    {
        if(set.length() == 1) {
            set = "0"+set;
        }
        this.IID = set;
    }

    //gets a specific transformer from an array of files. uses the TID and IID
    public static Transformer getTransformerFromFiles(String tmpTID, String tmpIID, File[] jarFiles)
    {
        int i;
        Transformer t;

        for(i=0; i < jarFiles.length; i++)
        {
            t = new Transformer(jarFiles[i]);
            if(t.compareIDs(tmpTID, tmpIID))
            {
                return t;
            }
        }
        return null;
    }

    public boolean compareIDs(String tmpTID, String tmpIID)
    {
        return ((tmpTID.equals(this.getTID())) & (tmpIID.equals(this.getIID())));
    }


    public void setServerFileUsed(boolean set)
    {
        serverFileUsed = set;
    }

    public boolean getServerFileUsed()
    {
        return serverFileUsed;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        String NL = System.getProperty("line.separator");
        sb.append("Transformer-Name: ").append(transformerName).append(NL);
        sb.append("TID: ").append(TID).append(NL);
        sb.append("IID: ").append(IID).append(NL);
        sb.append("Transformer-File-Name: ").append(transformerFileName).append(NL);
        sb.append("Server-File-Name: ").append(serverFileName).append(NL);
        sb.append("Tag-File-Name: ").append(tagFileName).append(NL);
        sb.append("Use-Before: ").append(this.UB).append(NL);
        sb.append("Use-After: ").append(this.UA).append(NL);

        return sb.toString();

    }

}