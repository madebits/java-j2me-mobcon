package vdoclet.docinfo;

import java.util.*;
import vdoclet.util.DelimitedList;

/**
 * Info about a Class.
 */
public class ClassInfo extends BaseElementInfo {

    //---( Instance variables )---

    private DocInfo docInfo;
    private final List methods = new ArrayList();
    private final List fields = new ArrayList();
    private String superClassName = "java.lang.Object";
    private final List interfaces = new ArrayList();
    
    //---( Constructors )---

    /**
     * Create a new ClassInfo with the specified name
     * @param name fully-qualified class-name
     */
    public ClassInfo( String name ) {
        super( name );
    }

    //---( Accessors )---

    /**
     * Set the containing DocInfo
     */
    public void setContainingDocInfo( DocInfo docInfo ) {
        this.docInfo = docInfo;
    }

    /**
     * Get the containing DocInfo
     */
    public DocInfo getContainingDocInfo() {
        return docInfo;
    }

    /**
     * Get the fully-qualified class-name
     */
    public String getName() {
        return super.getName();
    }

    /**
     * Get the name of the enclosing package
     */
    public String getPackage() {
        /// Bad assumption for inner-classes
        int lastDot = getName().lastIndexOf( '.' );
        if (lastDot == -1) return null;
        return getName().substring( 0, lastDot );
    }

    /**
     * Get the un-qualified class-name
     */
    public String getShortName() {
        int lastDot = getName().lastIndexOf( '.' );
        if (lastDot == -1) return getName();
        return getName().substring( lastDot+1 );
    }

    //---( Fields )---

    public void addField( FieldInfo field ) {
        fields.add( field );
        field.setContainingClass( this );
    }

    public void addFields( Collection fields ) {
        Iterator i = fields.iterator();
        while (i.hasNext()) {
            addField( (FieldInfo) i.next() );
        }
    }

    public List getFields() {
        return new DelimitedList( fields, ", " );
    }

    public FieldInfo getField( int index ) {
        return (FieldInfo) getFields().get( index );
    }

    //---( Methods )---

    public void addMethod( MethodInfo method ) {
        methods.add( method );
        method.setContainingClass( this );
    }

    public void addMethods( Collection methods ) {
        Iterator i = methods.iterator();
        while (i.hasNext()) {
            addMethod( (MethodInfo) i.next() );
        }
    }

    public List getMethods() {
        return Collections.unmodifiableList( methods );
    }

    public MethodInfo getMethod( int index ) {
        return (MethodInfo) getMethods().get( index );
    }

    public MethodInfo getMethod( String name, List types ) {
        Iterator i = getMethods().iterator();
        while (i.hasNext()) {
            MethodInfo method = (MethodInfo) i.next();
            if (method.getName().equals( name ) &&
                method.getParameterTypes().equals( types )) 
            {
                return method;
            }
        }
        return null;
    }

    //---( SuperClass )---

    public void setSuperClass( String superClassName ) {
        this.superClassName = superClassName;
    }

    public String getSuperClass() {
        return this.superClassName;
    }

    public ClassInfo resolveSuperClass() {
        if (getSuperClass() == null) return null;
        if (getContainingDocInfo() == null) return null;
        return getContainingDocInfo().getClass( getSuperClass() );
    }

    //---( Interfaces )---

    public void addInterface( String iface ) {
        interfaces.add( iface );
    }

    public void addInterfaces( Collection interfaces ) {
        Iterator i = interfaces.iterator();
        while (i.hasNext()) {
            addInterface( (String) i.next() );
        }
    }

    public Collection getInterfaces() {
        return new DelimitedList( interfaces, ", " );
    }

    /**
     * Return true if this class implements the interface, either
     * directly or thru it's super-class.
     */
    public boolean hasInterface( String name ) {
        ClassInfo superClass = resolveSuperClass();
        if (interfaces.contains( name )) {
            return true;
        } else if (superClass != null) {
            return superClass.hasInterface( name );
        }
        return false;
    }

}
