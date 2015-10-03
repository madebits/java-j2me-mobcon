package mobcon.app;

import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;

import com.thoughtworks.qdox.model.JavaSource;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.security.SecureRandom;
import vdoclet.docinfo.DocInfo;
import vdoclet.docinfo.ClassInfo;
import vdoclet.docinfo.MethodInfo;
import vdoclet.docinfo.ParameterInfo;
import vdoclet.docinfo.FieldInfo;
import vdoclet.docinfo.QDoxBuilder;
import vdoclet.Generator;

import mobcon.ct.*;
import mobcon.util.RandomGUID;
import mobcon.util.ByteArray;

public class Control
{
    protected static boolean debug = false;
    protected static boolean allowParallel = true;

    //---( State )---

    /** output directory */
    File destDir;

    /** input files/directories */
    String[] sourceFiles;

    /** input path for mobile sources*/
    String mobileSourcePath;

    /** input path for server sources*/
    String serverSourcePath;

    /** path for generated classes for mobile device */
    String mobileOutPath;

    /** path for generated classes for server */
    String serverOutPath;

    /** The Applications CID */
    String CID;

   /** verbose flag */
    boolean verbose = false;

    /** docinfo */
    DocInfo docInfo;

    /** Generator */
    Generator generator;

    /** Transformer Manager */
    TransMan transMan;

    /** transformer Path */
    String transDir;

    /** Descriptor Path */
    String depDescriptorPath;

    /** Descriptor File-Name */
    String depDescriptorFileName;

    /** path for preverify.dat */
    String preverifyOutPath;

    /** path to find additional sources */
    String addSources;

    /** path to find additional files, that will be included to the jar-files */
    String addFiles;

    /** Files that will be preferified */
    ArrayList preverifyNames;

    /** ClassTemplate for MobConMain */
    ClassTemplate mcm;

    /** ClassTemplate for Context CTX */
    ClassTemplate CTX;
    /** Pairs of Transformer.transformerName,  Transformer.TID+Transformer.IID*/
    Hashtable allTransformers;

    /** Secure key for the encryption */
    byte[] randomKey;

    void log( String msg ) {
        if (verbose) System.err.println( msg );
    }

    /**
     * Parse the source-files
     */
    void parseSources() throws IOException {
        com.thoughtworks.qdox.JavaDocBuilder builder =
            new com.thoughtworks.qdox.JavaDocBuilder();
        ArrayList sourceDirList = new ArrayList();
        for (int i = 0; i < sourceFiles.length; i++) {
            log( "reading " + sourceFiles[i] );
            File f = new File(sourceFiles[i]);
            if (f.isDirectory()) {
                builder.addSourceTree( f );
                sourceDirList.add( f );
            } else {
                builder.addSource( new FileReader(f) );
            }
        }

        JavaSource[] javaSources = builder.getSources();
        log( javaSources.length + " source-files" );
        docInfo = QDoxBuilder.build( javaSources );

        if (mobileSourcePath != null) {
            docInfo.setSourcePath( mobileSourcePath );
        } else {
            String[] srcPath = (String[]) sourceDirList.toArray( new String[0] );
            docInfo.setSourcePath( srcPath );
        }
    }

    /**
     * Evales all files from the source-dir
     */
    void generate() throws Exception {
        this.parseSources();

        generator.setAttribute( "docInfo", docInfo );

        //files are all the files in the src-dir
        Collection files = docInfo.getClasses();

        ClassInfo ci;
        if(files.size() > 0)
        {
            for (Iterator iter = files.iterator(); iter.hasNext() ;)
            {
                ci = (ClassInfo)iter.next();
                // Generate the code
                this.evalFile(ci);
            }
        }

        this.generatePreverify();

        /** Finally write out the MobConMain **/
        MethodTemplate mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("MobConMain");
        mt.addEnd("cr.addContainerApp(ca);");
        mcm.addMethod(mt, "@main");
        transMan.setOutCT(serverOutPath+File.separator+"MobConMain.java");
        transMan.writeOutCT(mcm.toString());

        transMan.setOutCT(mobileOutPath+File.separator+"CTX.java");
        transMan.writeOutCT(CTX.toString());
        preverifyNames.add("CTX");
    }

    /** Copies File to server-dir **/
    public void writeServerFile(InputStream in, String fileName, String TID, String IID)
    {
        try
        {
            FileOutputStream out;
            File outputFile;

            outputFile = new File(this.serverOutPath+File.separator+fileName+".java");
            out = new FileOutputStream(outputFile);

            byte buffer[] = new byte[0xffff];
            int nbytes;

            while ( (nbytes = in.read(buffer)) != -1 )
            {
               out.write( buffer, 0, nbytes );
            }

            in.close();
            out.close();

            MethodTemplate mt = new MethodTemplate();
            mt.setAccess("public");
            mt.setName("MobConMain");
            mt.addBody("ca.addTransformerApp(new "+fileName+"(\""+TID+"\", \""+IID+"\"));");
            mcm.addMethod(mt, "@main");
        }catch(Exception e){
            System.err.println(this.getClass().getName()+": Error in reading/writing server-files = "+e);
        }

    }

    /**
     * Eval one file from the source-dir
     */
    public void evalFile(ClassInfo ci) throws Exception
    {
        String inputName = ci.getName();
        Iterator iter;

        System.out.println();
        System.out.println(this.getClass().getName()+": READING MobCon-Code "+inputName);
        System.out.println();

        generator.setAttribute("class", ci);
        generator.setAttribute("PREVERIFY", preverifyNames);
        generator.setAttribute("MCM", mcm);


        /** Eval all transformerGroups*/
        Transformer currTrans;

        transMan.resetTransformerGroupIterator();
        TransformerGroup transGroup = transMan.nextTransformerGroup();

        ClassTemplate mainCT = new ClassTemplate();
        mainCT.setName("Abstract"+ci.getShortName());
        mainCT.setAccess("public abstract");

        addGeneralObjectsToCT(mainCT, ci);
        generator.setAttribute("MAINCT", mainCT);
        generator.setAttribute("CTX", CTX);

        while(transGroup != null)
        {
            /** Eval all transformers in the current TransformerGroup*/
            if(transGroup.getSize() > 1)
            {
                for (iter = transGroup.getIterator(); iter.hasNext() ;)
                {
                    currTrans = (Transformer)iter.next();
                    System.out.println();
                    System.out.println(this.getClass().getName()+": PARSING with transformer: "+currTrans.getTransName()+"("+currTrans.getTransFileName()+")");
                    evalTransformer(currTrans, ci);
                }

                /** Mix the transformers in one transformerGroup together
                 *  and save the resulting ClassTemplate in the transformerGroup
                 */
                ClassTemplate ct = new ClassTemplate();
                ct.setName("Abstract"+ci.getShortName());
                iter = transGroup.getIterator();

                while(iter.hasNext())
                {
                    currTrans = (Transformer)iter.next();
                    ct.mixClassTemplates(currTrans.getClassTemplate());
                    ct.setMixTemplate(currTrans.getMixTemplate());
                }
                transGroup.setClassTemplate(ct);
            /** Eval ONE transformers in the current TransformerGroup */
            }else{
                currTrans = (Transformer)(transGroup.getIterator()).next();
                System.out.println();
                System.out.println(this.getClass().getName()+": PARSING with transformer: "+currTrans.getTransName()+"("+currTrans.getTransFileName()+")");
                evalTransformer(currTrans, ci);

                transGroup.setClassTemplate(currTrans.getClassTemplate());
            }

            mainCT.mixClassTemplates(transGroup.getClassTemplate());
            mainCT.setMixTemplate(transGroup.getMixTemplate());
            generator.setAttribute("MAINCT", mainCT);

            transGroup = transMan.nextTransformerGroup();
        }

        generateApplicationLogic(mainCT, ci);

        /** Write out the abstract main-ClassTemplate */
        transMan.setOutCT(mobileOutPath+File.separator+"Abstract"+ci.getShortName()+".java");
        transMan.writeOutCT(mainCT.toString());
        preverifyNames.add(mainCT.getName());
    }

    public void evalTransformer(Transformer currTrans, ClassInfo ci)
    {
        ClassTemplate ct = new ClassTemplate();
        ct.setName("Abstract"+ci.getShortName());

        generator.setAttribute( "CT", ct);
        generator.setAttribute( "transformer", currTrans);
        generator.setAttribute( "PID", ""+currTrans.getTID());
        generator.setAttribute( "IID", ""+currTrans.getIID());

        allTransformers.put(currTrans.getTransName(), currTrans.getTID()+currTrans.getIID());
        generator.setAttribute( "ALLTRANSFORMERS", allTransformers);

        try
        {
            generator.eval(currTrans.getTransFileName(), "log.txt");
        } catch(Exception e){
            System.err.println(this.getClass().getName()+": Error in evaluating with Transformer "+currTrans.getTransFileName()+" = "+e);
        }

        //Writing Server-File to server-output-dir, if its used.
        if(currTrans.getServerFileUsed())
        {
            writeServerFile(currTrans.getServerFile(), currTrans.getServerFileName(), currTrans.getTID(), currTrans.getIID());
        }

        currTrans.setClassTemplate(ct);
        outputOtherCT(currTrans);
    }

    /** Add all the information provided in the input file to the application class-template */
    public void generateApplicationLogic(ClassTemplate mCT, ClassInfo ci)
    {
        ClassTemplate ct = new ClassTemplate();

        FieldTemplate ft;
        MethodTemplate mt;
        Iterator iter;
        Iterator iter2;

        ct.setName(ci.getShortName());
        ct.setAccess(ci.getModifiers());
        ct.setBaseClass("Abstract"+ci.getShortName());
        ct.imports.putAll(mCT.imports);


        /** add Constructor(s) */
        MethodTemplate constructor;
        Collection mainMethods = mCT.getMethods();
        for (iter = mainMethods.iterator(); iter.hasNext() ;)
        {
            mt = (MethodTemplate)iter.next();
            if(mt.getName().equals("Abstract"+ci.getShortName()))
            {
                constructor = new MethodTemplate();
                constructor.setAccess("public");
                constructor.setName(ci.getShortName());

                //parameters = mi.getParameters();
                // Signature for an eventuell call
                String constructorCall = "";

                for (iter2 = mt.getParameters().iterator(); iter2.hasNext() ;)
                {
                    ft = (FieldTemplate)iter2.next();
                    constructor.addParameter(ft);
                    constructorCall = constructorCall+ft.getName();
                    if(iter2.hasNext()) constructorCall = constructorCall+",";
                }

                constructor.addBody("super("+constructorCall+");");
                ct.addMethod(constructor);
            }
        }
/*
        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName(ci.getShortName());
        mt.addBody("super();");
        ct.addMethod(mt, "@app");
*/

        Collection interfaces = ci.getInterfaces();
        String inter;
        for (iter = interfaces.iterator(); iter.hasNext() ;)
        {
            inter = (String)iter.next();
            ct.addInterface(inter);
        }

        List methods = ci.getMethods();
        List parameters;
        Set exceptions;

        MethodInfo mi;
        ParameterInfo pi;
        String exception;
        String shortClassName;

        for (iter = methods.iterator(); iter.hasNext() ;)
        {
            mi = (MethodInfo)iter.next();
            mt = new MethodTemplate();
            mt.setAccess(mi.getModifiers());
            mt.setName(mi.getName());
            shortClassName = mi.getType();
            shortClassName = shortClassName.substring(shortClassName.lastIndexOf('.')+1);
            mt.setType(shortClassName);

            parameters = mi.getParameters();
            // Signature for an eventuell call
            String parameterCall = "";
            for (iter2 = parameters.iterator(); iter2.hasNext() ;)
            {
                pi = (ParameterInfo)iter2.next();
                ft = new FieldTemplate();
                ft.setName(pi.getName());
                shortClassName = pi.getType();
                shortClassName = shortClassName.substring(shortClassName.lastIndexOf('.')+1);
                ft.setType(shortClassName);
                mt.addParameter(ft);

                parameterCall = parameterCall+ft.getName();
                if(iter2.hasNext()) parameterCall = parameterCall+", ";
            }

            exceptions = mi.getExceptions();
            for (iter2 = exceptions.iterator(); iter2.hasNext() ;)
            {
                exception = (String)iter2.next();
                mt.addException(exception);
            }

            /** Add abstract method-call to the abstract class
                and copy all the method to the application class.
                If exists and is abstract, then it is a method that must be
                implemented in the appication class, no super-call needed.
             */
            boolean abstractMethod = false;
            if(mCT.methods.get(mt.getID()) == null)
            {
                mt.setAbstract(true);
                mCT.addMethod(mt, "@app");
                abstractMethod = true;
            }else{
                abstractMethod = ((MethodTemplate)mCT.methods.get(mt.getID())).getAbstract();
                mt.setAbstract(abstractMethod);
            }

            MethodTemplate mt2 = mt.cloneMethod();
            mt2.setAbstract(false);

            if(mi.getTag("@begin") != null)
            {
                mt2.addBegin(mi.getMethodBody());
                if(!abstractMethod)
                {
                    mt2.addBegin("super."+mt.getName()+"("+parameterCall+");");
                }
            }else{
                if(!abstractMethod)
                {
                    mt2.addBegin("super."+mt.getName()+"("+parameterCall+");");
                }
                mt2.addEnd(mi.getMethodBody());
            }

            ct.addMethod(mt2, "@app");
        }

        List fields = ci.getFields();
        FieldInfo fi;

        for (iter = fields.iterator(); iter.hasNext() ;)
        {
            fi = (FieldInfo)iter.next();
            ft = new FieldTemplate();
            //Add all untaged fields in the template
            if(fi.getTags().isEmpty()){

                shortClassName = fi.getType();
                shortClassName = shortClassName.substring(shortClassName.lastIndexOf('.')+1);
                ft.setType(shortClassName);
                ft.setName(fi.getName());
                ft.setAccess(fi.getModifiers());
                mCT.addField(ft, "@app");
            }
        }
        transMan.setOutCT(mobileOutPath+File.separator+ct.getName()+".java");
        transMan.writeOutCT(ct.toString());
        preverifyNames.add(ct.getName());
    }

    /** Output the classes that are generated also in the process
        - mobileClassTemplates, like the store in dp-concern)
        - serverClassTemplates ***/
    public void outputOtherCT(Transformer currTrans)
    {
        Vector mobileCT;
        Vector serverCT;
        ClassTemplate oCT;
        Iterator iter;

        mobileCT = currTrans.getMobileClassTemplates();
        for (iter = mobileCT.iterator(); iter.hasNext() ;)
        {
            oCT = (ClassTemplate)iter.next();

            if(debug) System.out.println(this.getClass().getName()+": Adding mobile File = "+oCT.getName());

            transMan.setOutCT(mobileOutPath+File.separator+oCT.getName()+".java");
            transMan.writeOutCT(oCT.toString());
            preverifyNames.add(oCT.getName());
        }

        serverCT = currTrans.getServerClassTemplates();
        for (iter = serverCT.iterator(); iter.hasNext() ;)
        {
            oCT = (ClassTemplate)iter.next();

            if(debug) System.out.println(this.getClass().getName()+": Adding server File = "+oCT.getName());

            transMan.setOutCT(serverOutPath+File.separator+oCT.getName()+".java");
            transMan.writeOutCT(oCT.toString());
        }
    }


    /** Add the general CT things **/
    public void addGeneralObjectsToCT(ClassTemplate ct, ClassInfo ci)
    {
/*
        FieldTemplate ft;
        MethodTemplate mt;

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("String");
        ft.setName("CID");
        ft.setValue("\""+CID+"\"");
        ct.addField(ft, "@main");

        ct.addImport("mobcon.message.*");
        ct.addImport("mobcon.storeables.*");
        ct.addImport("java.util.Hashtable");

        ft = new FieldTemplate();
        ft.setAccess("protected");
        ft.setName("display");
        ft.setType("Display");
        ct.addField(ft, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("startApp");
        mt.setType("void");
        ct.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("pauseApp");
        mt.setType("void");
        ct.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("destroyApp");
        mt.setType("void");
        ft = new FieldTemplate();
        ft.setName("unconditional");
        ft.setType("boolean");
        mt.addParameter(ft);
        ct.addMethod(mt, "@main");
*/
        // Add all the imports
        String imports;
        for (Iterator iter = ci.getImports().iterator(); iter.hasNext() ;)
        {
            imports = (String)iter.next();
            ct.addImport(imports);

            // Add to preverify if its not a class from midp-package
            if( !(imports.startsWith("java.io")) &&
                !(imports.startsWith("java.lang")) &&
                !(imports.startsWith("java.util")) &&
                !(imports.startsWith("javax.microedition")) )
            {
                preverifyNames.add(imports);
            }
        }

    }

    /** Generates the MU-Class **/
    public void createMUClass(String serverIP)
    {

        ClassTemplate mu = new ClassTemplate();
        FieldTemplate ft = new FieldTemplate();
        MethodTemplate mt = new MethodTemplate();

        mu = new ClassTemplate();
        mu.setName("MU");
        mu.setAccess("public");
        mu.addImport("mobcon.message.*");
        mu.addImport("javax.microedition.io.*");
        mu.addImport("java.io.*");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("String");
        ft.setName("CONNECTOR_URL");
        ft.setValue('"'+serverIP+'"');
        mu.addField(ft, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("sendNetMessage");
        mt.setType("void");
        ft = new FieldTemplate();
        ft.setName("o");
        ft.setType("NetMessage");
        mt.addParameter(ft);
        mt.addBody("MessageHandler mh = new MessageHandler(true, CONNECTOR_URL, o);");
        mt.addBody("mh.start();");
        mu.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("getNetMessageResult");
        mt.setType("NetMessage");
        ft = new FieldTemplate();
        ft.setName("o");
        ft.setType("NetMessage");
        mt.addParameter(ft);
        mt.addBody("MessageHandler mh = new MessageHandler(false, CONNECTOR_URL, o);");
        mt.addBody("NetMessage result = null;");
        mt.addBody("mh.start();");
        mt.addBody("result = mh.getResult();");
        mt.addBody("return result;");
        mu.addMethod(mt, "@main");

        transMan.setOutCT(mobileOutPath+File.separator+"MU.java");
        transMan.writeOutCT(mu.toString());
        preverifyNames.add("MU");

    }

    /** Generates the CTX-Class **/
    public void createCTXClass()
    {
        CTX = new ClassTemplate();
        FieldTemplate ft = new FieldTemplate();
        MethodTemplate mt = new MethodTemplate();

        CTX = new ClassTemplate();
        CTX.setName("CTX");
        CTX.setAccess("public");
        CTX.addImport("java.util.*");
        CTX.addImport("javax.microedition.io.*");
        CTX.addImport("java.io.*");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("String");
        ft.setName("STR_EMPTY");
        ft.setValue("");
        CTX.addField(ft, "@main");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("int");
        ft.setName("REMOTE_NEVER");
        ft.setValue("1");
        CTX.addField(ft, "@main");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("int");
        ft.setName("REMOTE_ALWAYS");
        ft.setValue("2");
        CTX.addField(ft, "@main");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("int");
        ft.setName("REMOTE_AS_NEEDED");
        ft.setValue("3");
        CTX.addField(ft, "@main");

        ft = new FieldTemplate();
        ft.setAccess("public static");
        ft.setType("int");
        ft.setName("REMOTE");
        ft.setValue("3");
        CTX.addField(ft, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("CTX");
        CTX.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("validate");
        mt.setType("boolean");
        ft = new FieldTemplate();
        ft.setName("value");
        ft.setType("int");
        mt.addParameter(ft);
        ft = new FieldTemplate();
        ft.setName("min");
        ft.setType("int");
        mt.addParameter(ft);
        ft = new FieldTemplate();
        ft.setName("max");
        ft.setType("int");
        mt.addParameter(ft);
        mt.addBegin("if((value < min) || (value > max)) return false;");
        mt.addBegin("return true;");
        CTX.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("validate");
        mt.setType("boolean");
        ft = new FieldTemplate();
        ft.setName("value");
        ft.setType("String");
        mt.addParameter(ft);
        ft = new FieldTemplate();
        ft.setName("min");
        ft.setType("int");
        mt.addParameter(ft);
        ft = new FieldTemplate();
        ft.setName("max");
        ft.setType("int");
        mt.addParameter(ft);
        mt.addBegin("value = noNull(value);");
        mt.addBegin("return validate(value.length(), min, max);");
        CTX.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("noNull");
        mt.setType("String");
        ft = new FieldTemplate();
        ft.setName("s");
        ft.setType("String");
        mt.addParameter(ft);
        mt.addBegin("return (s == null) ? STR_EMPTY : s;");
        CTX.addMethod(mt, "@main");

/*
        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("sendNetMessage");
        mt.setType("void");
        mt.addException("Exception");
        ft = new FieldTemplate();
        ft.setName("o");
        ft.setType("NetMessage");
        mt.addParameter(ft);
        mt.addBegin("MU.sendNetMessage(o);");
        CTX.addMethod(mt, "@main");
*/
/*
        mt = new MethodTemplate();
        mt.setAccess("public static");
        mt.setName("getNetMessageResult");
        mt.setType("NetMessage");
        mt.addException("Exception");
        ft = new FieldTemplate();
        ft.setName("o");
        ft.setType("NetMessage");
        mt.addParameter(ft);
        mt.addBegin("return MU.getNetMessageResult(o);");
        CTX.addMethod(mt, "@main");
*/
        preverifyNames.add("CTX");
    }

    public void createMobConMainClass()
    {
        mcm = new ClassTemplate();
        mcm.setName("MobConMain");
        mcm.setAccess("public");
        mcm.addImport("mobcon.server.*");
        mcm.addImport("java.net.Socket");

        FieldTemplate ft = new FieldTemplate();
        ft.setAccess("protected");
        ft.setType("CR");
        ft.setName("cr");
        mcm.addField(ft, "@main");

        MethodTemplate mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setType("void");
        mt.setName("process");
        mt.addException("Exception");
        ft = new FieldTemplate();
        ft.setType("Socket");
        ft.setName("socket");
        mt.addParameter(ft);
        mt.addBegin("cr.process(socket);");
        mcm.addMethod(mt, "@main");

        mt = new MethodTemplate();
        mt.setAccess("public");
        mt.setName("MobConMain");
        mt.addBegin("cr = new CR();");
        mt.addBegin("ContainerApp ca = new ContainerApp(\""+this.CID+"\");");
        mcm.addMethod(mt, "@main");
    }

    /** Generates the file preverify.dat */
    public void generatePreverify()
    {
        File preverify = new File(preverifyOutPath+File.separator+"preverify.dat");

        StringBuffer sb = new StringBuffer();

        sb.append("-cldc ");

        if(preverifyNames.size() > 0)
        {
            for (Iterator iter = preverifyNames.iterator(); iter.hasNext() ;)
            {
                sb.append(iter.next()+" ");
            }
        }

        try
        {
            FileWriter fOut = new FileWriter(preverify);
            fOut.write(sb.toString());
            fOut.close();
        } catch(Exception e){System.out.println(this.getClass().getName()+": Error in writing out preverify-file "+e);}

    }

    public void copyDir(String inputDir, String outputDir)
    {
        if(debug) System.out.println(this.getClass().getName()+": Copy additional files from \""+inputDir+"\" to \""+outputDir+"\"");

        try
        {
            FileInputStream in;
            FileOutputStream out;
            File inputFile;
            File outputFile;

            File dir = new File(inputDir);

            String[] dirList = dir.list();
            int i;

            if(dirList != null){
                for(i=0; i < dirList.length; i++)
                {
                    inputFile = new File(inputDir+File.separator+dirList[i]);
                    outputFile = new File(outputDir+inputFile.getName());
                    in = new FileInputStream(inputFile);
                    out = new FileOutputStream(outputFile);

                    byte buffer[] = new byte[0xffff];
                    int nbytes;

                    while ( (nbytes = in.read(buffer)) != -1 )
                    {
                       out.write( buffer, 0, nbytes );
                    }

                    in.close();
                    out.close();
                }
            }
        } catch(Exception e){
            System.err.println(this.getClass().getName()+": Error in reading additional mobile-files "+e);
        }
    }

    public void copyDirPreverify(String inputDir, String outputDir)
    {
        if(debug) System.out.println(this.getClass().getName()+": Copy mobile sources from \""+inputDir+"\" to \""+outputDir+"\"");

        try
        {
            FileInputStream in;
            FileOutputStream out;
            File inputFile;
            File outputFile;

            File dir = new File(inputDir);

            String[] dirList = dir.list();
            int i;

            if(dirList != null){
                for(i=0; i < dirList.length; i++)
                {
                    inputFile = new File(inputDir+File.separator+dirList[i]);
                    outputFile = new File(outputDir+inputFile.getName());
                    in = new FileInputStream(inputFile);
                    out = new FileOutputStream(outputFile);

                    byte buffer[] = new byte[0xffff];
                    int nbytes;

                    while ( (nbytes = in.read(buffer)) != -1 )
                    {
                       out.write( buffer, 0, nbytes );
                    }

                    in.close();
                    out.close();

                    preverifyNames.add(dirList[i].substring(0,dirList[i].indexOf('.')));
                }
            }
        } catch(Exception e){
            System.err.println(this.getClass().getName()+": Error in reading additional mobile-files "+e);
        }
    }

    public void copyServerLogic(String inputDir, String outputDir)
    {
        if(debug) System.out.println(this.getClass().getName()+": Copy server logic from \""+inputDir+"\" to \""+outputDir+"\"");

        try
        {
            FileInputStream in;
            FileOutputStream out;
            File inputFile;
            File outputFile;

            File dir = new File(inputDir);

            String[] dirList = dir.list();
            int i;

            for(i=0; i < dirList.length; i++)
            {
                inputFile = new File(inputDir+File.separator+dirList[i]);
                outputFile = new File(outputDir+inputFile.getName());
                in = new FileInputStream(inputFile);
                out = new FileOutputStream(outputFile);

                byte buffer[] = new byte[0xffff];
                int nbytes;

                while ( (nbytes = in.read(buffer)) != -1 )
                {
                   out.write( buffer, 0, nbytes );
                }

                in.close();
                out.close();

                String tid = "00";
                String iid = "00";
                MethodTemplate mt = new MethodTemplate();
                mt.setAccess("public");
                mt.setName("MobConMain");
                mt.addBody("ca.addTransformerApp(new "+dirList[i].substring(0,dirList[i].indexOf('.'))+"(\""+tid+"\", \""+iid+"\"));");
                mcm.addMethod(mt, "@main");
            }
        } catch(Exception e){
            System.err.println(this.getClass().getName()+": Error in reading additional mobile-files "+e);
        }
    }


    /**
     * Init the environment
     */
    public void init() throws Exception
    {
        generator = new Generator( destDir );

        RandomGUID myGUID = new RandomGUID(false);
        this.CID = myGUID.toString().toLowerCase();
        generator.setAttribute("CID", CID);
        System.out.println(this.getClass().getName()+": BUILDING APPLICATION CID = "+this.CID);

        this.allTransformers = new Hashtable();

        /** Disable logging of null-set in Velocity **/
        Context context = generator.getContext();
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(new VelocityNullLogEvent(false));
        ec.attachToContext(context);

        /** Init TransMan **/
        this.transMan = new TransMan();
        this.transMan.setTransDir(transDir);
        this.transMan.initTransformers();

        generator.setAttribute("transMan", transMan);

        /** Setting the attributes from Deployment Descriptor**/
        DepDescriptorReader depDR = new DepDescriptorReader(depDescriptorPath+File.separator+depDescriptorFileName);
        //this.CID = depDR.getCID();
        generator.setAttribute("SERVERIP", depDR.getIP());

        /** Generate random key */
        if(depDR.getGenerateKey())
        {
            randomKey = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(randomKey);
            ByteArray ba = new ByteArray(randomKey);
            generator.setAttribute("KEY", ba.toString());
        }

        /** Copy additional sources **/
        copyDirPreverify(addSources+File.separator+"mobile", this.mobileOutPath+File.separator);
        copyDir(addSources+File.separator+"server", this.serverOutPath+File.separator);

        /** Copy additional files **/
        copyDir(addFiles+File.separator+"mobile", this.mobileOutPath+File.separator+"bin"+File.separator+"output"+File.separator);
        copyDir(addFiles+File.separator+"server", this.serverOutPath+File.separator+"output"+File.separator);

        /** Create additional classes **/
        this.createCTXClass();
        //this.createMUClass(depDR.getIP());
        this.createMobConMainClass();

        /** Copy additional server-side container-logic **/
        copyServerLogic(serverSourcePath, this.serverOutPath+File.separator);
    }

    public static void main(String[] args) throws Exception
    {
        Control ctrl = new Control();

        ctrl.transDir = "lib/plugins";
        ctrl.mobileOutPath = "classes"+File.separator+"mobile";
        ctrl.serverOutPath = "classes"+File.separator+"server";
        ctrl.preverifyOutPath = "classes";
        ctrl.destDir = new File(ctrl.mobileOutPath);
        ctrl.mobileSourcePath = "src"+File.separator+"mobile";
        ctrl.serverSourcePath = "src"+File.separator+"server";
        ctrl.verbose = true;
        ctrl.sourceFiles = new String[1];
        ctrl.sourceFiles[0] = ctrl.mobileSourcePath;
        ctrl.depDescriptorPath = "descriptor";
        ctrl.depDescriptorFileName = "descriptor.xml";
        ctrl.preverifyNames = new ArrayList();
        ctrl.addSources = "src"+File.separator+"addsources";
        ctrl.addFiles = "resources";


        ctrl.init();
        ctrl.generate();
/*
        int i = 0, j;
        String arg;
        char flag;
        boolean vflag = false;
        String outputfile = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            if (arg.equals("-output")) {
                if (i < args.length)
                    outputfile = args[i++];
                else
                    System.err.println("-output requires a filename");
                if (vflag)
                    System.out.println("output file = " + outputfile);
            }

        }
        if (i == args.length)
            System.err.println("Usage: ParseCmdLine [-verbose] [-xn] [-output afile] filename");
*/
    }

}