package vdoclet.util;

import java.util.*;

public class DelimitedCollection extends AbstractCollection {
        
    //---( Instance variables )---

    private String delimiter;
    private Collection collection;
    
    //---( Constructor )---

    public DelimitedCollection( Collection collection, String delimiter ) {
        this.collection = collection;
        this.delimiter = delimiter;
    }
    
    //---( Accessors )---

    public int size() {
        return collection.size();
    }
    
    public Iterator iterator() {
        return collection.iterator();
    }
    
    public String toString() {
        return join( collection, delimiter );
    }
        
    //---( Utils )---

    /**
     * Create a String representation of a Collection, using the specified
     * delimiter.
     */
    public static String join( Collection c, String delimiter ) {
        if (c.isEmpty()) return "";
        StringBuffer buff = new StringBuffer();
        Iterator i = c.iterator();
        buff.append( toString( i.next() ));
        while (i.hasNext()) {
            buff.append( delimiter );
            buff.append( toString( i.next() ));
        }
        return buff.toString();
    }

    private static String toString( Object o ) {
        if (o == null) return "";
        return o.toString();
    }

}
