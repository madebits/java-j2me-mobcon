package vdoclet.ejb;

import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.util.*;

public class EjbInfoCmp11_Test extends Ejb_BaseTest {

    //---( Setup )---

    public EjbInfoCmp11_Test( String name ) {
        super( name );
    }

    //---( getCmp11ClassName )---

    public void testCmp11ClassNameSimple() {
        assertEquals( "demo.FubarCmp11", fubarEjb.getCmp11ClassName() );
    }

    public void testCmp11ClassNameExplicit() {
        fubarSrc.addTag( EjbTags.CMP11_CLASS, "demo.FubarBlahBlah" );
        assertEquals( "demo.FubarBlahBlah", fubarEjb.getCmp11ClassName() );
    }

    //---( getCmp11Class )---

    public void testGetCmp11ClassNotRequired() {
        assertNull( fubarEjb.getCmp11Class() );
    }

    public void testGetCmp11ClassEntity() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        ClassInfo cmp11Class = fubarEjb.getCmp11Class();
        assertNotNull( cmp11Class );
        assertEquals( "demo.FubarCmp11", cmp11Class.getName() );
    }

}
