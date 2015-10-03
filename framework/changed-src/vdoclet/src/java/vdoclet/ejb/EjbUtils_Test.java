package vdoclet.ejb;

import vdoclet.docinfo.ClassInfo;
import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.util.*;

public class EjbUtils_Test extends Ejb_BaseTest {

    //---( Setup )---

    public EjbUtils_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    ClassInfo makeEntityBean() {
        ClassInfo srcClass = new ClassInfo( "my.EntityBean" );
        srcClass.addTag( EjbTags.ENTITY, "y" );
        return srcClass;
    }

    ClassInfo makeSessionBean() {
        ClassInfo srcClass = new ClassInfo( "my.SessionBean" );
        srcClass.addTag( EjbTags.SESSION, "y" );
        return srcClass;
    }

    public void testGetEjbBundle() {
        DocInfo docInfo = new DocInfo();
        docInfo.addClass( makeEntityBean() );
        docInfo.addClass( makeSessionBean() );
        docInfo.addClass( new ClassInfo( "Dummy" ) );
        EjbBundle bundle = EjbUtils.getEjbBundle( docInfo );
        assertEquals( 2, bundle.getEjbs().size() );
    }
    
}
