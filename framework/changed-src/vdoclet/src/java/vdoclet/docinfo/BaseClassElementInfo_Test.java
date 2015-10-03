package vdoclet.docinfo;

import junit.framework.TestCase;

public class BaseClassElementInfo_Test extends TestCase
{
    //---( Setup )---

    public BaseClassElementInfo_Test( String name ) {
        super( name );
    }
    
    //---( Tests )---
    
    public void testSetClass() {
        BaseClassElementInfo classElementInfo = 
            new BaseClassElementInfo( "zzob" ) {};
        assertEquals( null, classElementInfo.getContainingClass() );
        ClassInfo classInfo = new ClassInfo( "xxx" );
        classElementInfo.setContainingClass( classInfo );
        assertEquals( classInfo, classElementInfo.getContainingClass() );
    }

}
