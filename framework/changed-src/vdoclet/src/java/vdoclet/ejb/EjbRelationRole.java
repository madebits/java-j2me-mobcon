package vdoclet.ejb;

import java.util.*;
import vdoclet.beaninfo.*;
import vdoclet.docinfo.*;

/**
 * One end of an EjbRelation
 */
public class EjbRelationRole {
    
    //---( Instance variables )---
    
    private EjbInfo _ejb;
    private FieldInfo _cmrField;
    private EjbRelationRole _target;
    
    //---( Constructors )---
    
    public EjbRelationRole( EjbInfo ejb ) {
        _ejb = ejb;
    }
    
    public EjbRelationRole( EjbInfo ejb, FieldInfo cmrField ) 
    {
        this( ejb );
        setField( cmrField );
    }

    //---( Accessors )---
    
    /**
     * Get the source EJB
     */
    public EjbInfo getEjb() {
        return _ejb;
    }
    
    //---( Name )---

    /**
     * Get the canonical name of this role
     */
    public String getName() {
        String roleName = getEjb().getName();
        if (getField() != null) {
            roleName += '.' + getField().getName();
        }
        return roleName;
    }

    //---( CMR field )---

    /**
     * Register a CMR-field for this role
     * @param cmrField CMR-field info
     */
    public void setField( FieldInfo cmrField ) {
        _cmrField = cmrField;
    }

    /**
     * Register a get-method that defines the CMR-field for this role
     * @param getMethod a CMR-field get-method
     */
    public void setField( MethodInfo cmrMethod ) {
        setField( PropertyInfo.fromGetter( cmrMethod ));
    }
    
    /**
     * Get the CMR-field for this role
     * @return the CMR-field for this role, or null if undefined
     */
    FieldInfo getField() {
        return _cmrField;
    }

    /**
     * Get the name of the CMR-field for this role
     * @return the name of the role's CMR-field, or null if undefined
     */
    public String getFieldName() {
        return (_cmrField == null
                ? null
                : _cmrField.getName());
    }
    
    /**
     * Get the type of the CMR-field, if it's a collection
     */
    public String getFieldType() {
        if (getField() == null) return null;
        String type = getField().getType();
        if (type.equals( "java.util.Collection" ) ||
            type.equals( "java.util.Set" )) 
        {
            return type;
        }
        return null;
    }

    //---( Tags )---

    /**
     * Return the named tag from the CMR-field.  Return the specified
     * defaultValue if the tag is not present.
     */
    public String getTagValue( String name, String defaultValue ) {
        if (getField() == null) return null;
        return getField().getTagValue( name, defaultValue );
    }

    /**
     * Return the named tag from the CMR-field. Return null if the tag is
     * not present.
     */
    public String getTagValue( String name ) {
        return getTagValue( name, null );
    }

    
    //---( Target role )---

    /**
     * Get the target for this role. 
     */
    public EjbRelationRole getTarget() {
        return _target;
    }

    /**
     * Set the target for this role. 
     */
    public void setTarget( EjbRelationRole target ) {
        _target = target;
    }

    //---( Multiplicity )---

    /**
     * Enumeration of possible multiplicity values
     */
    public static class Multiplicity {

        public static final Multiplicity ONE = new Multiplicity("One");
        public static final Multiplicity MANY = new Multiplicity("Many");

        private final String _name;

        private Multiplicity( String name ) {
            _name = name;
        }

        public static Multiplicity forName( String name ) {
            return (name.toLowerCase().equals("many")
                    ? MANY
                    : ONE);
        }

        public String toString() {
            return _name;
        }
        
    }

    /**
     * Get the multiplicity for this role. 
     */
    public Multiplicity getMultiplicity() {
        String explicitMultiplicity =
            getTagValue( EjbTags.CMR_TARGET_MULTIPLICITY );
        if (explicitMultiplicity != null) {
            return Multiplicity.forName( explicitMultiplicity );
        } else if (getTarget() != null) {
            if (getTarget().getFieldType() != null) {
                return Multiplicity.MANY;
            }
        }
        return Multiplicity.ONE;
    }

}
