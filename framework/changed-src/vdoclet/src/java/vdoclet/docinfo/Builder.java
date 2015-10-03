package vdoclet.docinfo;

/**
 * Utility to build-up DocInfo gradually 
 */
public class Builder {

    //---( Member variables )---

    DocInfo docInfo = new DocInfo();
    String currPackage;
    BaseInfo currInfo;
    BaseElementInfo currElement;
    ClassInfo currClass;
    MethodInfo currMethod;
    
    //---( Accessors )---
    
    public DocInfo getDocInfo() {
        return docInfo;
    }

    //---( DocInfo )---

    public void setPackage( String pkg ) {
        currPackage = pkg;
    }

    public ClassInfo addClass( String name ) {
        ClassInfo clazz = new ClassInfo( name );
        docInfo.addClass( clazz );
        currInfo = currElement = currClass = clazz;
        currMethod = null;
        return clazz;
    }
    
    //---( BaseInfo )---
    
    public void setComment( String comment ) {
        if (currInfo == null) {
            throw new IllegalStateException( "no BaseInfo" );
        }
        if (comment == null || comment.length() == 0) return;
        currInfo.setComment( comment );
    }

    public void addTag( String name, String value ) {
        if (currInfo == null) {
            throw new IllegalStateException( "no BaseInfo" );
        }
        currInfo.addTag( name, value );
    }
    
    //---( BaseElementInfo )---

    public void setPublic( boolean isPublic ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setPublic( isPublic );
    }

    public void setPrivate( boolean isPrivate ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setPrivate( isPrivate );
    }
    
    public void setProtected( boolean isProtected ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setProtected( isProtected );
    }
    
    public void setStatic( boolean isStatic ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setStatic( isStatic );
    }
    
    public void setFinal( boolean isFinal ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setFinal( isFinal );
    }
    
    public void setAbstract( boolean isAbstract ) {
        if (currElement == null) {
            throw new IllegalStateException( "no BaseElementInfo" );
        }
        currElement.setAbstract( isAbstract );
    }

    //---( ClassInfo )---

    public MethodInfo addMethod( String type, String name) {
        if (currClass == null) {
            throw new IllegalStateException( "no ClassInfo" );
        }
        MethodInfo method = new MethodInfo( qualifyType(type), name );
        currClass.addMethod( method );
        currInfo = currElement = currMethod = method;
        return method;
    }
    
    //CHANGED FOR MOBCON
    public MethodInfo addMethod( String type, String name, String methodBody ) {
        if (currClass == null) {
            throw new IllegalStateException( "no ClassInfo" );
        }
        MethodInfo method = new MethodInfo( qualifyType(type), name );
        method.addMethodBody(methodBody);
        currClass.addMethod( method );
        currInfo = currElement = currMethod = method;
        return method;
    }
    
    public void setSuperClass( String superClass ) {
        if (currClass == null) {
            throw new IllegalStateException( "no ClassInfo" );
        }
        currClass.setSuperClass( superClass );
    }
    
    public void addInterface( String iface ) {
        if (currClass == null) {
            throw new IllegalStateException( "no ClassInfo" );
        }
        currClass.addInterface( iface );
    }

    public FieldInfo addField( String type, String name ) {
        if (currClass == null) {
            throw new IllegalStateException( "no ClassInfo" );
        }
        FieldInfo field = new FieldInfo( qualifyType(type), name );
        currClass.addField( field );
        currInfo = currElement = field;
        return field;
    }
    
    //---( MethodInfo )---
    
    public void addParameter( String type, String name ) {
        if (currMethod == null) {
            throw new IllegalStateException( "no MethodInfo" );
        }
        currMethod.addParameter( qualifyType(type), name );
    }
    
    public void addException( String exception ) {
        if (currMethod == null) {
            throw new IllegalStateException( "no MethodInfo" );
        }
        currMethod.addException( exception );
    }

    //---( Type shortcut )---

    /**
     * Prepend unqualified class-names with the current package.
     */
    private String qualifyType( String typeName ) {
        
        // Do nothing for primitives and qualified types
        if (typeName.indexOf(".") != -1 ||
            Character.getType( typeName.charAt(0) ) == 
            Character.LOWERCASE_LETTER)
        {
            return typeName;
        }
        
        // It's an unqualified class-name; prepend the current package.
        if (currPackage != null) {
            typeName = currPackage + '.' + typeName;
        }
        
        return typeName;
    }

}

