package vdoclet.ejb;

import vdoclet.docinfo.ClassInfo;
import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.util.*;

public class RegisteredClassInfo_Test extends TestCase {

    //---( Setup )---

    public RegisteredClassInfo_Test( String name ) {
        super( name );
    }
    
    //---( Tests )---

    public void testCreateDefault() {
        RegisteredClassInfo info = new RegisteredClassInfo( "demo.Blah" );
        assertEquals( "demo.Blah", info.getJndiName() );
    }

    public void testCreateExplicit() {
        RegisteredClassInfo info = new RegisteredClassInfo( "demo.Blah",
                                                            "beans.Blah" );
        assertEquals( "beans.Blah", info.getJndiName() );
    }

}
