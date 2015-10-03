package vdoclet.docinfo;

import java.util.*;

/**
 * Info about a Field.
 */
public class FieldInfo extends BaseClassElementInfo {

    //---( Instance variables )---

    private final String type;

    //---( Constructors )---
    
    public FieldInfo( String type, String name ) { 
        super( name );
        this.type = type;
    }

    //---( Accessors )---

    /**
     * Get the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the canonical representation of the field as a String
     */
    public String toString() {
        return (this.type + ' ' + getName());
    }

    //---( Equality )---

    public int hashCode() {
        return (getName().hashCode() + this.type.hashCode());
    }

    public boolean equals( Object obj ) {
        if (super.equals( obj )) return true;
        if (!(obj instanceof FieldInfo)) return false;
        FieldInfo otherField = (FieldInfo) obj;
        return (getName().equals( otherField.getName() ) &&
                this.type.equals( otherField.type ));
    }
    
}

