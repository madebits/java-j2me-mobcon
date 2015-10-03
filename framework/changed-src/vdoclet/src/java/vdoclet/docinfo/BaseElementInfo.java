package vdoclet.docinfo;

import java.util.*;
import vdoclet.util.DelimitedCollection;

/**
 * Base class for stuff that has tags.
 */
public abstract class BaseElementInfo extends BaseInfo {

    //---( Instance variables )---

    private boolean isPublic;
    private boolean isAbstract;
    private boolean isProtected;
    private boolean isPrivate;
    private boolean isStatic;
    private boolean isFinal;

    //---( Constructors )---

    public BaseElementInfo( String name ) {
        super( name );
    }

    //---( Modifiers )---
    
    public void setPublic( boolean isPublic ) {
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setProtected( boolean isProtected ) {
        this.isProtected = isProtected;
    }

    public boolean isProtected() {
        return this.isProtected;
    }

    public void setPrivate( boolean isPrivate ) {
        this.isPrivate = isPrivate;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public void setStatic( boolean isStatic ) {
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public void setFinal( boolean isFinal ) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public void setAbstract( boolean isAbstract ) {
        this.isAbstract = isAbstract;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    //---( String representation )---
    
    /**
     * Get a String representation of the modifiers for this element
     */
    public String getModifiers() {
        List modList = new ArrayList();
        if (isPublic()) modList.add( "public" );
        if (isProtected()) modList.add( "protected" );
        if (isPrivate()) modList.add( "private" );
        if (isStatic()) modList.add( "static" );
        if (isFinal()) modList.add( "final" );
        if (isAbstract()) modList.add( "abstract" );
        return DelimitedCollection.join( modList, " " );
    }
    
}

