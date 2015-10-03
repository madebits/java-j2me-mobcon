package vdoclet.docinfo;

import junit.framework.TestCase;

public class ParameterInfo_Test extends TestCase 
{
    //---( Setup )---

    public ParameterInfo_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    public void testCreate() {
        ParameterInfo parameterInfo = 
            new ParameterInfo( "int", "doSomething" );
        assertEquals( "doSomething", parameterInfo.getName() );
    }

    public void testReturnType() {
        ParameterInfo parameterInfo = 
            new ParameterInfo( "int", "doSomething" );
        assertEquals( "int", parameterInfo.getType() );
    }

    public void testToString() {
        ParameterInfo parameterInfo = 
            new ParameterInfo( "int", "doSomething" );
        assertEquals( "int doSomething", parameterInfo.toString() );
    }

    public void testEquals() {
        ParameterInfo p1 = new ParameterInfo( "int", "a" );
        ParameterInfo p2 = new ParameterInfo( "int", "a" );
        assertEquals( p1, p2 );
    }

    public void testNotEquals() {
        ParameterInfo p1 = new ParameterInfo( "int", "a" );
        ParameterInfo p2 = new ParameterInfo( "int", "b" );
        ParameterInfo p3 = new ParameterInfo( "java.lang.String", "b" );
        assertTrue( !p1.equals(p2) );
        assertTrue( !p1.equals(p3) );
        assertTrue( !p2.equals(p3) );
    }

}
