package vdoclet.util;

import junit.framework.TestCase;
import java.util.*;

public class DelimitedList_Test extends TestCase
{
    //---( Setup )---

    public DelimitedList_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    public void testDelimitedListEmptyList() {
        List c = new DelimitedList( Collections.EMPTY_LIST, "," );
        assertEquals( "", c.toString() );
    }

    public void testDelimitedListOneElement() {
        ArrayList list = new ArrayList();
        list.add( "one" );
        List c = new DelimitedList( list, "," );
        assertEquals( "one", c.toString() );
    }

    public void testDelimitedListTwoElements() {
        ArrayList list = new ArrayList();
        list.add( "one" );
        list.add( "two" );
        List c = new DelimitedList( list, "," );
        assertEquals( "one,two", c.toString() );
    }

}
