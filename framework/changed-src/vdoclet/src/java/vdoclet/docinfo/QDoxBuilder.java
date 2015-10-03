package vdoclet.docinfo;

import com.thoughtworks.qdox.model.*;
import java.io.*;
import java.util.*;

/**
 * Bridge from QDox to docinfo.
 */
public class QDoxBuilder extends Builder {

    /**
     * @return List(ClassInfo)
     */
    public static DocInfo build( JavaSource[] javaSources ) {
        QDoxBuilder builder = new QDoxBuilder();
        for (int i = 0; i < javaSources.length; i++) {
            builder.processJavaSource( javaSources[i] );
        }
        return builder.getDocInfo();
    }

    /**
     * Process JavaSource
     */
    public void processJavaSource( JavaSource javaSource ) {
        JavaClass[] classes = javaSource.getClasses();
        for (int i = 0; i < classes.length; i++) {
            setPackage( javaSource.getPackage() );
            processJavaClass( classes[i] );
        }
    }

    /**
     * Process ProgramElementDoc
     */
    void processJavaEntity( AbstractJavaEntity javaEntity ) {
        setComment( javaEntity.getComment() );
        DocletTag[] tags = javaEntity.getTags();
        for (int i = 0; i < tags.length; i++) {
            addTag( "@" + tags[i].getName(), tags[i].getValue() );
        }
        setPublic( javaEntity.isPublic() );
        setProtected( javaEntity.isProtected() );
        setPrivate( javaEntity.isPrivate() );
        setStatic( javaEntity.isStatic() );
        setFinal( javaEntity.isFinal() );
    }

    /**
     * Process JavaClass
     */
    void processJavaClass( JavaClass javaClass ) {
        addClass( javaClass.getFullyQualifiedName() );
    processJavaEntity( javaClass );
        setAbstract( javaClass.isAbstract() );
        if (javaClass.getSuperClass() != null) {
            setSuperClass( javaClass.getSuperClass().getValue() );
        }
        Type[] interfaces = javaClass.getImplements();
        for (int i = 0; i < interfaces.length; i++) {
            addInterface( interfaces[i].getValue() );
        }
        JavaField[] fields = javaClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            processJavaField( fields[i] );
        }
        JavaMethod[] methods = javaClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            processJavaMethod( methods[i] );
        }
        JavaClass[] classes = javaClass.getClasses();
        for (int i = 0; i < classes.length; i++) {
            processJavaClass( classes[i] );
        }
    }

    /**
     * Process JavaMethod
     */
    void processJavaMethod( JavaMethod javaMethod ) {
        if (javaMethod.isConstructor())
        {
            //return;
            System.out.println("isConst: "+javaMethod.getName());
        }
        /// TODO: support for constructors

        //System.out.println("Meth: "+javaMethod.getName());

        //CHANGED FOR MOBCON
        addMethod( javaMethod.getReturns().getValue(),
                   javaMethod.getName(), javaMethod.getMethodBody() );

        processJavaEntity( javaMethod );
        JavaParameter[] parameters = javaMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            addParameter( parameters[i].getType().getValue(),
                          parameters[i].getName() );
        }
        Type[] exceptions = javaMethod.getExceptions();
        for (int i = 0; i < exceptions.length; i++) {
            addException( exceptions[i].getValue() );
        }
    }

    /**
     * Process JavaField
     */
    void processJavaField( JavaField javaField ) {
        addField( javaField.getType().getValue(),
                  javaField.getName() );
        processJavaEntity( javaField );
    }

    //---( testing )---

    public static void main( String[] args ) throws Exception {

        long start = System.currentTimeMillis();

        com.thoughtworks.qdox.JavaDocBuilder builder =
            new com.thoughtworks.qdox.JavaDocBuilder();
        builder.addSourceTree( new File(args[0]) );

        long end = System.currentTimeMillis();
        System.out.println( "parsing took " + (end-start) + "ms" );

        JavaSource[] javaSources = builder.getSources();
        for (int s = 0; s < javaSources.length; s++) {
            JavaClass[] javaClasses = javaSources[s].getClasses();
            for (int c = 0; c < javaClasses.length; c++) {
                System.out.println( "CLASS " + javaClasses[c].getFullyQualifiedName() );
                String[] mods = javaClasses[c].getModifiers();
                for (int m = 0; m < mods.length; m++) {
                    System.out.println( "  MOD " + mods[m] );
                }
                DocletTag[] tags = javaClasses[c].getTags();
                for (int t = 0; t < tags.length; t++) {
                    System.out.println( "  TAG " + tags[t].getName() +
                                        " " + tags[t].getValue() );
                }
            }
        }

        start = System.currentTimeMillis();

        build( javaSources );
        end = System.currentTimeMillis();
        System.out.println( "bridging " + javaSources.length +
                            " sources took " + (end-start) + "ms" );

    }

}
