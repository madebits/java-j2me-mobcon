package vdoclet.util;

import junit.framework.TestCase;

public class StringUtils_Test extends TestCase {

    //---( Setup )---

    public StringUtils_Test( String name ) {
        super( name );
    }

    //---( capitalise )---

    public void testCapitalise( String after, String before ) {
        assertEquals( null, StringUtils.capitalise( null ));
        assertEquals( "", StringUtils.capitalise( "" ));
        assertEquals( "Foo", StringUtils.capitalise( "foo" ));
        assertEquals( "Foo", StringUtils.capitalise( "Foo" ));
        assertEquals( "FOO", StringUtils.capitalise( "fOO" ));
    }
    
    //---( stripPrefix )---

    public void testStripPrefix( String after, String prefix, String before ) {
        assertEquals( after, StringUtils.stripPrefix( prefix, before ));
    }

    public void testStripPrefix() {
        testStripPrefix( "blah", "ejb", "ejbBlah" );
        testStripPrefix( "blah", "ejbHome", "ejbHomeBlah" );
        try {
            StringUtils.stripPrefix( "foo", "bar" );
            fail( "expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
    
}
