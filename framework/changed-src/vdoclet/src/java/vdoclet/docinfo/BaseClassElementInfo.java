package vdoclet.docinfo;

import java.util.*;
import vdoclet.util.DelimitedList;
import vdoclet.util.DelimitedSet;

/**
 * Info about a class sub-element.
 */
public class BaseClassElementInfo extends BaseElementInfo {

    //---( Instance variables )---

    private ClassInfo containingClass;

    //---( Constructors )---

    public BaseClassElementInfo( String name ) {
        super( name );
    }
    
    //---( Accessors )---

    /**
     * Set the containing ClassInfo
     */
    public void setContainingClass( ClassInfo containingClass ) {
        this.containingClass = containingClass;
    }

    /**
     * Get the containing ClassInfo
     */
    public ClassInfo getContainingClass() {
        return containingClass;
    }

}
