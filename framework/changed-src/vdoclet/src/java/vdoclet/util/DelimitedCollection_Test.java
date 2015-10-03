package vdoclet.util;

import junit.framework.TestCase;
import java.util.*;

public class DelimitedCollection_Test extends TestCase
{
    //---( Setup )---

    public DelimitedCollection_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    public String join( Collection c, String d ) {
        return DelimitedCollection.join( c, d );
    }
    
    public void testEmpty() {
        ArrayList list = new ArrayList();
        assertEquals( "", join( list, "," ));
    }

    public void testNulls() {
        ArrayList list = new ArrayList();
        list.add( null );
        list.add( null );
        assertEquals( ",", join( list, "," ));
    }

    public void testOneString() {
        ArrayList list = new ArrayList();
        list.add( "blah" );
        assertEquals( "blah", join( list, "," ));
    }

    public void testTwoStrings() {
        ArrayList list = new ArrayList();
        list.add( "blah" );
        list.add( "" );
        assertEquals( "blah,", DelimitedCollection.join( list, "," ));
    }

    public void testSomeObjects() {
        ArrayList list = new ArrayList();
        list.add( new Integer(3) );
        list.add( new Boolean(true) );
        list.add( new Long(1) );
        assertEquals( "3, true, 1", 
                      DelimitedCollection.join( list, ", " ));
    }
    
}
