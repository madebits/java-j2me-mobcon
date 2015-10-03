package vdoclet.docinfo;

import java.util.*;

/**
 * Info about a Parameter.
 */
public class ParameterInfo {
    
    //---( Instance variables )---

    private final String type;
    private final String name;
    
    //---( Constructors )---
    
    public ParameterInfo( String type, String name ) {
        this.name = name;
        this.type = type;
    }

    //---( Accessors )---

    /**
     * Get the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Get the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the canonical representation of the parameter as a String
     */
    public String toString() {
        return (this.type + ' ' + this.name);
    }

    //---( Equality )---

    public int hashCode() {
        return (this.name.hashCode() + this.type.hashCode());
    }

    public boolean equals( Object obj ) {
        if (super.equals( obj )) return true;
        if (!(obj instanceof ParameterInfo)) return false;
        ParameterInfo otherParameter = (ParameterInfo) obj;
        return (this.name.equals( otherParameter.name ) &&
                this.type.equals( otherParameter.type ));
    }
    
}

