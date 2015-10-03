package vdoclet.util;

import java.util.*;

public class DelimitedList extends AbstractList {
    
    //---( Instance variables )---

    private String delimiter;
    private List list;
    
    //---( Constructor )---

    public DelimitedList( List list, String delimiter ) {
        this.list = list;
        this.delimiter = delimiter;
    }
    
    //---( Accessors )---

    public int size() {
        return list.size();
    }
    
    public Object get( int i ) {
        return list.get(i);
    }
    
    public String toString() {
        return DelimitedCollection.join( list, delimiter );
    }
    
}
    
