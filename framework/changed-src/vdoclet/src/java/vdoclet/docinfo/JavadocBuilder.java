package vdoclet.docinfo;

import com.sun.javadoc.*;
import java.util.*;

/**
 * Bridge from Sun's Javadoc model to docinfo.
 */
public class JavadocBuilder extends Builder {

    /**
     * @return List(ClassInfo)
     */
    public static DocInfo build( RootDoc rootDoc ) {
        JavadocBuilder builder = new JavadocBuilder();
        builder.processRoot( rootDoc );
        return builder.getDocInfo();
    }

    /**
     * Process RootDoc
     */
    public void processRoot( RootDoc rootDoc ) {
        ClassDoc[] classes = rootDoc.classes();
        for (int i = 0; i < classes.length; i++) {
            processClass( classes[i] );
        }  
    }

    /**
     * Process Doc
     */
    private void processDoc( Doc doc ) {
        setComment( doc.commentText() );
        Tag[] tags = doc.tags();
        for (int i = 0; i < tags.length; i++) {
            addTag( tags[i].name(), tags[i].text() );
        }
    }
    
    /**
     * Process ProgramElementDoc
     */
    private void processProgramElement( ProgramElementDoc progElt ) {
        processDoc( progElt );
        setPublic( progElt.isPublic() );
        setProtected( progElt.isProtected() );
        setPrivate( progElt.isPrivate() );
        setStatic( progElt.isStatic() );
        setFinal( progElt.isFinal() );
    }
    
    /**
     * Process ClassDoc
     */
    public void processClass( ClassDoc classDoc ) {
        addClass( classDoc.qualifiedName() );
	processProgramElement( classDoc );
        setAbstract( classDoc.isAbstract() );
        if (classDoc.superclass() != null) {
            setSuperClass( classDoc.superclass().qualifiedName() );
        }    
        FieldDoc[] fieldDocs = classDoc.fields();
        for (int i = 0; i < fieldDocs.length; i++) {
            processField( fieldDocs[i], classDoc );
        }
        MethodDoc[] methodDocs = classDoc.methods();
        for (int i = 0; i < methodDocs.length; i++) {
            processMethod( methodDocs[i], classDoc );
        }
        ClassDoc[] interfaces = classDoc.interfaces();
        for (int i = 0; i < interfaces.length; i++) {
            addInterface( interfaces[i].qualifiedName() );
        }
    }

    /**
     * @return the package of this class, or null if undefined
     */
    private static String getPackageName( ClassDoc classDoc ) {
        while (classDoc.containingClass() != null) {
            classDoc = classDoc.containingClass();
        }      
        String className = classDoc.qualifiedName();
        int lastDot = className.lastIndexOf( '.' );
        if (lastDot == -1) return null;
        return className.substring(0,lastDot);
    }

    /**
     * Resolve the type-name.  This is required to work-around a problem in
     * JDK 1.4 Javadoc.
     */
    private static String getTypeName( Type type, ClassDoc contextClassDoc ) {
        String typeName = type.qualifiedTypeName();
        
        // Do nothing for primitives and qualified types
        if (typeName.indexOf(".") != -1 ||
            Character.getType( typeName.charAt(0) ) == 
            Character.LOWERCASE_LETTER)
        {
            return typeName;
        }
        
        // Whoops, looks like it's an unqualified class-name; prepend the
        // current package.
        String packageName = getPackageName( contextClassDoc );
        if (packageName != null) {
            typeName = packageName + '.' + typeName;
        }
        
        return typeName;
    }

    /**
     * Process MethodDoc
     */
    public void processMethod( MethodDoc methodDoc, ClassDoc classDoc ) {
        addMethod( getTypeName( methodDoc.returnType(), classDoc ),
                   methodDoc.name() );	
        processProgramElement( methodDoc );
        Parameter[] parameters = methodDoc.parameters();
        for (int i = 0; i < parameters.length; i++) {
            addParameter( getTypeName( parameters[i].type(), classDoc ),
                          parameters[i].name() );
        }
        ClassDoc[] exceptions = methodDoc.thrownExceptions();
        for (int i = 0; i < exceptions.length; i++) {
            addException( exceptions[i].qualifiedName() );
        }
    }

    /**
     * Process FieldDoc
     */
    public void processField( FieldDoc fieldDoc, ClassDoc classDoc ) {
        addField( getTypeName( fieldDoc.type(), classDoc ),
                  fieldDoc.name() );	
        processProgramElement( fieldDoc );
   }

}

    
