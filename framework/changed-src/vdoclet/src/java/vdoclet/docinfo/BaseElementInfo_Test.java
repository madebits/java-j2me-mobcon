package vdoclet.docinfo;

import junit.framework.TestCase;

public class BaseElementInfo_Test extends TestCase
{
    //---( Setup )---

    public BaseElementInfo_Test( String name ) {
        super( name );
    }

    BaseElementInfo getInstance() {
        return new BaseElementInfo( "zzob" ) {};
    }
    
    //---( Tests )---
    
    public void testSetPublic() {
        BaseElementInfo info = getInstance();
        assertTrue( !info.isPublic() );
        info.setPublic( true );
        assertTrue( info.isPublic() );
        assertEquals( "public", info.getModifiers().toString() );
    }

    public void testSetProtected() {
        BaseElementInfo info = getInstance();
        assertTrue( !info.isProtected() );
        info.setProtected( true );
        assertTrue( info.isProtected() );
        assertEquals( "protected", info.getModifiers().toString() );
    }

    public void testSetPrivate() {
        BaseElementInfo info = getInstance();
        assertTrue( !info.isPrivate() );
        info.setPrivate( true );
        assertTrue( info.isPrivate() );
        assertEquals( "private", info.getModifiers().toString() );
    }

    public void testSetStatic() {
        BaseElementInfo info = getInstance();
        assertTrue( !info.isStatic() );
        info.setStatic( true );
        assertTrue( info.isStatic() );
        assertEquals( "static", info.getModifiers().toString() );
    }

    public void testSetFinal() {
        BaseElementInfo info = getInstance();
        assertTrue( !info.isFinal() );
        info.setFinal( true );
        assertTrue( info.isFinal() );
        assertEquals( "final", info.getModifiers().toString() );
    }

    public void testSetPublicFinal() {
        BaseElementInfo info = getInstance();
        info.setPublic( true );
        info.setFinal( true );
        assertTrue( info.getModifiers().indexOf("public") != 1 );
        assertTrue( info.getModifiers().indexOf("final") != 1 );
    }

}
