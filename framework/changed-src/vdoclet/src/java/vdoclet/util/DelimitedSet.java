package vdoclet.util;

import java.util.*;

public class DelimitedSet extends AbstractSet {
    
    //---( Instance variables )---

    private String delimiter;
    private Set set;
    
    //---( Constructor )---

    public DelimitedSet( Set set, String delimiter ) {
        this.set = set;
        this.delimiter = delimiter;
    }
    
    //---( Accessors )---

    public int size() {
        return set.size();
    }
    
    public Iterator iterator() {
        return set.iterator();
    }
    
    public String toString() {
        return DelimitedCollection.join( set, delimiter );
    }
        
}
