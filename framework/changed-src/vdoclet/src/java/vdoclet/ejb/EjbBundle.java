package vdoclet.ejb;

import vdoclet.docinfo.*;
import java.io.*;
import java.util.*;

/**
 * Represents a collection of EJBs
 */
public class EjbBundle {

    //---( Constants )---

    public static final String EJB_PROP_FILE = "vdoclet-ejb.properties";
    public static final String SECURITY_ROLES_PROP = "security.roles";
    public static final String EJB_VERSION_PROP = "ejb.version";

    protected static final EjbBundle NULL_BUNDLE = 
        new EjbBundle( new DocInfo() );
    
    //---( Instance variables )---

    private DocInfo _docInfo;
    private Map _ejbMap;
    private Map _abstractEjbMap;
    private Map _relationMap;
    private Properties _props = new Properties();

    //---( Constructor )---

    /**
     * Construct an EjbBundle from the specified javadoc info.
     */
    public EjbBundle( DocInfo docInfo ) {
        _docInfo = docInfo;
        loadProperties();
    }

    //---( Property handling )---

    /**
     * Load bundle properties from the "ejb.properties" file in the
     * source-path
     */
    private void loadProperties() {
        File propFile = _docInfo.getSourceFile( EJB_PROP_FILE );
        if (propFile != null) {
            try {
                InputStream propStream = new FileInputStream( propFile );
                _props.load( new BufferedInputStream( propStream ));
            } catch (IOException e) {
                throw new RuntimeException( e.toString() );
            }
        }
    }

    /**
     * Set a bundle property
     * @param name property name
     * @param value property value
     */
    void setProperty( String name, String value ) {
        _props.setProperty( name, value );
    }

    /**
     * Fetch a bundle property
     * @param name property name
     * @return the property value, or null if undefined
     */
    public String getProperty( String name ) {
        return _props.getProperty( name );
    }

    /**
     * Fetch a bundle property
     * @param name property name
     * @param defaultValue a default value
     * @return the property value, or defaultValue if undefined
     */
    public String getProperty( String name, String defaultValue ) {
        return _props.getProperty( name, defaultValue );
    }

    /**
     * Get all properties starting with the given prefix.
     * @return the matching properties, with the prefix removed
     * from the key.
     */
    public Properties getPropertiesStartingWith(String prefix) {
        prefix = prefix + '.';
        Iterator iter = _props.keySet().iterator();
        Properties matching = new Properties();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (key.startsWith(prefix)) {
                String newKey = key.substring(prefix.length());
                matching.setProperty(newKey, _props.getProperty(key));
            }
        }
        return matching;
    }

    //---( Version )---

    /**
     * Determine which version of the EJB spec we're targeting
     */
    public float getEjbVersion() {
        return Float.parseFloat( getProperty( EJB_VERSION_PROP, "1.1" ));
    }

    //---( Security )---

    /**
     * Get the security-roles defined for this bundle
     * @return Set(String) of security-role names
     */
    public Set getSecurityRoles() {
        Set roles = new TreeSet();
        String rolesString = _props.getProperty( SECURITY_ROLES_PROP, "" );
        StringTokenizer tokens =
            new StringTokenizer( rolesString, ", " );
        while (tokens.hasMoreTokens()) {
            roles.add( tokens.nextToken() );
        }
        return roles;
    }

    //---( Access to source )---

    public DocInfo getDocInfo() {
        return _docInfo;
    }

    //---( EJB Factory )---

    /**
     * Scan the input classes, generating a Map of EjbInfo objects
     */
    private void generateEjbs() {
        _ejbMap = new TreeMap();
        _abstractEjbMap = new TreeMap();
        Iterator i = _docInfo.getClasses().iterator();
        while (i.hasNext()) {
            ClassInfo srcClass = (ClassInfo) i.next();
            EjbInfo ejb = new EjbInfo( srcClass );
            ejb.setBundle( this );
            if (ejb.isEjb()) {
                _ejbMap.put( srcClass.getName(), ejb );
            } else if (ejb.isAbstract()) {
                _abstractEjbMap.put( srcClass.getName(), ejb );
            }
        }      
    }
    
    /**
     * Call generateEjbs() unless already done
     */
    private void checkGenerateEjbs() {
        if (_ejbMap == null) {
            generateEjbs();
        }
    }

    /**
     * Return all the Enterprise Beans
     * @return Collection(EjbInfo)
     */
    public Collection getEjbs() {
        checkGenerateEjbs();
        return Collections.unmodifiableCollection( _ejbMap.values() );
    }

    /**
     * Return all the abstract Enterprise Beans
     * @return Collection(EjbInfo)
     */
    public Collection getAbstractEjbs() {
        checkGenerateEjbs();
        return Collections.unmodifiableCollection( _abstractEjbMap.values() );
    }

    /**
     * Return a named EJB
     * @param name the name of the srcClass
     */
    public EjbInfo getEjb( String name ) {
        checkGenerateEjbs();
        EjbInfo ejb = (EjbInfo) _ejbMap.get( name );
        if (ejb == null) {
            ejb= (EjbInfo) _abstractEjbMap.get( name );
        }
        return ejb;
    }

    //---( Relationships )---

    /**
     * Scan EJBs, generating relationships
     */
    private void generateRelations() {
        _relationMap = new TreeMap();
        for (Iterator ejbIterator = getEjbs().iterator(); 
             ejbIterator.hasNext();) 
        {
            EjbInfo ejb = (EjbInfo) ejbIterator.next();
            if (! ejb.isCmpEntity()) continue;
            for (Iterator cmrIterator = ejb.getCmrFields().iterator(); 
                 cmrIterator.hasNext();) 
            {
                FieldInfo cmrField = (FieldInfo) cmrIterator.next();
                EjbRelationRole ejbRole = 
                    new EjbRelationRole( ejb, cmrField );
                String targetName =
                    cmrField.getTag( EjbTags.CMR_TARGET ).getWord(0);
                EjbInfo targetEjb = getEjb( targetName );
                if (targetEjb == null) {
                    targetEjb = new EjbInfo( new ClassInfo( targetName ));
                }
                EjbRelationRole targetRole = 
                    new EjbRelationRole( targetEjb );
                String targetFieldName =
                    cmrField.getTag( EjbTags.CMR_TARGET ).getWord(1);
                if (targetFieldName != null) {
                    FieldInfo targetField = 
                        new FieldInfo( "java.lang.Object", targetFieldName );
                    targetRole.setField( targetField );
                }
                EjbRelation relation = new EjbRelation( ejbRole, targetRole );
                if (_relationMap.containsKey( relation.getName() )) {
                    relation = (EjbRelation) 
                        _relationMap.get( relation.getName() );
                    relation.getRole( ejbRole.getName() ).setField( cmrField );
                } else {
                    _relationMap.put( relation.getName(), relation );
                }
            }
        }
    }

    /**
     * Call generateRelations() unless already done
     */
    private void checkGenerateRelations() {
        if (_relationMap == null) {
            generateRelations();
        }
    }

    /**
     * Return all EjbRelations
     */
    public Collection getRelations() {
        checkGenerateRelations();
        return Collections.unmodifiableCollection( _relationMap.values() );
    }

}
