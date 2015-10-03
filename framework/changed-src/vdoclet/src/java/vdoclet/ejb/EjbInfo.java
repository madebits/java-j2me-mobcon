package vdoclet.ejb;

import java.util.*;
import vdoclet.beaninfo.*;
import vdoclet.docinfo.*;
import vdoclet.util.*;

/**
 * Represents an EJB derived from Javadoc information
 */
public class EjbInfo {
    
    //---( Instance variables )---

    private ClassInfo _srcClass;
    private EjbBundle _bundle = EjbBundle.NULL_BUNDLE;

    //---( Constructors )---

    /**
     * Construct a new EjbInfo.
     *
     * @param srcClass the bean implementation class
     */
    public EjbInfo( ClassInfo srcClass ) {
        _srcClass = srcClass;
    }

    //---( Bundle association )---

    /**
     * Associate this EjbInfo with an EjbBundle
     */
    public void setBundle( EjbBundle bundle ) {
        _bundle = bundle;
    }

    /**
     * Get the associated EjbBundle
     */
    public EjbBundle getBundle() {
        return _bundle;
    }

    //---( Access source-class )---

    /**
     * Get ClassInfo for the bean implementation class.
     */
    public ClassInfo getSrcClass() {
        return _srcClass;
    }

    /**
     * @return the inner-class called "CMP", or null if there isn't one
     */
    public ClassInfo getSrcCmpInnerClass() {
        String innerClassName = getSrcClass().getName() + ".CMP";
        return getBundle().getDocInfo().getClass( innerClassName );
    }

    /**
     * Get methods from the srcClass and any inner "CMP" class
     */
    public List getAllSrcMethods() {
        List methods = new ArrayList();
        methods.addAll( getSrcClass().getMethods() );
        if (getSrcCmpInnerClass() != null) {
            methods.addAll( getSrcCmpInnerClass().getMethods() );
        }
        return methods;
    }

    //---( Sub-classing )---

    /**
     * @return EjbInfo for the EJB that this one extends, or null if there
     * isn't one
     */
    public EjbInfo getSuperEjb() {
        ClassInfo superSrcClass = getSrcClass().resolveSuperClass();
        if (superSrcClass == null) return null;
        return getBundle().getEjb( superSrcClass.getName() );
    }

    //---( Tag handling )---

    /**
     * Return the named tag from the srcClass.  Return the specified
     * defaultValue if the tag is not present.
     */
    public String getTagValue( String name, String defaultValue ) {
        defaultValue = getBundle().getProperty( name, defaultValue );
        return getSrcClass().getTagValue( name, defaultValue );
    }

    /**
     * Return the named tag from the srcClass. Return null if the tag is
     * not present.
     */
    public String getTagValue( String name ) {
        return getTagValue( name, null );
    }

    /**
     * Return all the values of the named tag from the srcClass.
     */
    public List getTags( String name ) {
        return getSrcClass().getTags( name );
    }

    //---( EJB properties )---

    /**
     * True if the srcClass represents an EJB.
     */
    public boolean isEjb() {
        return (getType() != null);
    }

    /**
     * True if the srcClass was tagged with "@ejb-entity".
     */
    public boolean isEntity() {
        return getTagValue( EjbTags.ENTITY ) != null;
    }

    /**
     * True if the srcClass was tagged with "@ejb-session".
     */
    public boolean isSession() {
        return getTagValue( EjbTags.SESSION ) != null;
    }

    /**
     * True if the srcClass was tagged with "@ejb-mdb".
     */
    public boolean isMessageDriven() {
        return getTagValue( EjbTags.MESSAGE_DRIVEN ) != null;
    }

    /**
     * True if this represents a container-managed entity.
     */
    public boolean isCmpEntity() {
        String pType =
            getTagValue( EjbTags.PERSISTENCE_TYPE, "Container" );
        return isEntity() && pType.equals( "Container" );
    }

    /**
     * True if the srcClass was tagged with "@ejb-abstract".
     */
    public boolean isAbstract() {
        return getTagValue( EjbTags.ABSTRACT ) != null;
    }

    /**
     * @return the type of this bean as a string, eg, "entity".
     */
    public String getType() {
        if ( isEntity() ) return "entity";
        if ( isSession() ) return "session";
        if ( isMessageDriven() ) return "message-driven";
        return null;
    }

    /**
     * Get the base-name for derived classes.
     *
     * The default is the name of the srcClass, minus any "Bean" suffix.
     * This can be overridden with an "@ejb-base-name" tag.
     */
    public String getBaseName() {
        String prefix = getTagValue( EjbTags.BASE_NAME );
        if (prefix == null) {
            prefix = getSrcClass().getName();
            if (prefix.endsWith("Bean")) {
                prefix = prefix.substring( 0, prefix.length() - 4 );
            }
        }
        return prefix;
    }

    /**
     * Get the EJB-name of the EJB.
     *
     * The default is the value of getBaseName(), minus any package prefix.
     * This can be overridden with an "@ejb-name" tag.
     */
    public String getName() {
        return getTagValue( EjbTags.NAME, shortName( getBaseName() ));
    }

    //---( Derive key-class )---

    /**
     * Get the name of the primary-key class.
     *
     * The default is based on getBaseName().  This can be
     * overridden with an "@ejb-key-class" tag.
     */
    String getPrimaryKeyClassName() {
        return getTagValue( EjbTags.KEY_CLASS,
                            getBaseName() + "Key" );
    }

    /**
     * Check whether we need to generate the primary-key
     */
    public boolean isPrimaryKeyRequired() {
        return getTagValue( EjbTags.KEY_GENERATE ) != null;
    }

    /**
     * Derive a list of primary-key fields.
     */
    public List getPrimaryKeyFields() {
        List keyFields = new ArrayList();

        // Look for "@ejb-key-field <type> <name>" tags on the class
        for (Iterator i = getTags( EjbTags.KEY_FIELD ).iterator();
             i.hasNext();)
        {
            TagInfo tag = (TagInfo)i.next();
            keyFields.add( PropertyInfo.fromField( tag.getWord(0), 
                                                   tag.getWord(1) ));
        }

        // Look for @ejb-key-field tags on methods
        for (Iterator i = getSrcClass().getMethods().iterator();
             i.hasNext();)
        {
            MethodInfo method = (MethodInfo)i.next();
            if (method.getTagValue( EjbTags.KEY_FIELD ) != null) {
                PropertyInfo keyProperty = 
                    PropertyInfo.fromGetter( method );
                keyFields.add( keyProperty );
            }
        }

        return new DelimitedList( keyFields, ", " );
    }

    /**
     * Get a representation of the primary-key class.
     */
    public ClassInfo getPrimaryKey() {
        ClassInfo key = new ClassInfo( getPrimaryKeyClassName() );
        if (isPrimaryKeyRequired()) {
            Collection keyFields = getPrimaryKeyFields();
            if (keyFields.isEmpty()) {
                throw new EjbInfoException( "there are no @ejb-key-field tags in " + getSrcClass().getName() );
            }
            key.addFields( keyFields );
        }
        return key;
    }

    //---( Queries )---

    /**
     * Get a representation of the view-home interface.
     */
    public Collection getQueryMethods() {
        Collection queryMethods = new ArrayList();
        for (Iterator i = getAllSrcMethods().iterator(); i.hasNext();) {
            MethodInfo queryMethod = (MethodInfo) i.next();
            if (queryMethod.getName().startsWith("ejbFind")) {
                queryMethod = (MethodInfo) queryMethod.clone();
                queryMethod.setName( StringUtils.stripPrefix
                                     ( "ejb", queryMethod.getName() ));
            } else if (queryMethod.getName().startsWith("ejbSelect")) {
                // use as-is
            } else {
                // not a query-method
                continue;
            }
            queryMethods.add( queryMethod );
        }
        return queryMethods;
    }

    //---( Derive remote view )---

    /**
     * Get the remote client view
     */
    public EjbRemoteView getRemoteView() {
        if (getBundle().getEjbVersion() >= 2.0f &&
            getTagValue( EjbTags.REMOTE_FLAG ) == null) 
        {
            return null;
        }
        return new EjbRemoteView( this );
    }

    //---( Derive local view )---

    /**
     * Get the local client view
     */
    public EjbLocalView getLocalView() {     
        if (getTagValue( EjbTags.LOCAL_FLAG ) == null) {
            return null;
        }
        return new EjbLocalView( this );
    }

    //---( Derive cmp11Class )---

    /**
     * Get the name of the bean cmp11Class.
     *
     * <p>The default is getBaseName() plus a "Cmp11" suffix.  This can be
     * overridden with an "@ejb-cmp11-class" tag.
     */
    public String getCmp11ClassName() {
        return getTagValue( EjbTags.CMP11_CLASS,
                            getBaseName() + "Cmp11" );
    }

    /**
     * Get a representation of the generated cmp11Class.
     */
    public ClassInfo getCmp11Class() {
        if (! isCmpEntity()) return null;  
        if (getBundle().getEjbVersion() >= 2.0f) return null;
        return new ClassInfo( getCmp11ClassName() );
    }

    //---( Persistent fields )---

    /**
     * Derive a set of persistent fields based on the CMP_COLUMN tag.
     */
    public List getCmpFields() {
        List fields = new ArrayList();
        Iterator i = getSrcClass().getMethods().iterator();
        while (i.hasNext()) {
            MethodInfo method = (MethodInfo)i.next();
            if (method.getTagValue( EjbTags.CMP_COLUMN ) != null) {
                fields.add( PropertyInfo.fromGetter( method ));
            }
        }
        return fields;
    }

    /**
     * Derive a set of relationship fields based on the CMR_TARGET tag.
     */
    public List getCmrFields() {
        List fields = new ArrayList();
        Iterator i = getSrcClass().getMethods().iterator();
        while (i.hasNext()) {
            MethodInfo method = (MethodInfo)i.next();
            if (method.getTagValue( EjbTags.CMR_TARGET ) != null) {
                fields.add( PropertyInfo.fromGetter( method ));
            }
        }
        return fields;
    }
    
    //---( Persistent abstract-schema )---

    /**
     * Get the name of the abstract persistence schema
     *
     * <p>The default is the last word of getName().  This can be
     * overridden with an "@ejb-cmp-schema-name" tag.
     */
    public String getCmpSchemaName() {
        return getTagValue( EjbTags.CMP_SCHEMA_NAME, shortName( getBaseName() ));
    }

    //---( Determine EJB implementation class )---

    /**
     * Return a representation of the ejb-class.  This will typically be
     * the srcClass, but may in some cases be a generated sub-class.
     */
    public ClassInfo getEjbClass() {
        ClassInfo ejbClass = getCmp11Class();
        if (ejbClass == null) {
            ejbClass = getSrcClass();
        }
        return ejbClass;
    }

    //---( Utils )---

    /**
     * Get the last part of the argument, removing any package prefix
     * @param fullName a fully-qualified name
     */
    public static String shortName( String fullName ) {
        if (fullName == null) return null;
        int lastDot = fullName.lastIndexOf('.');
        if (lastDot != -1) {
            return fullName.substring( lastDot+1 );
        } else {
            return fullName;
        }
    }

}
