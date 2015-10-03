package vdoclet.ejb;

import junit.framework.TestCase;
import vdoclet.docinfo.*;

/**
 * Base-test for EjbView derivatives
 */
public abstract class Ejb_BaseTest extends TestCase {

    //---( Setup )---

    public Ejb_BaseTest( String name ) {
        super( name );
    }
    
    public ClassInfo fubarSrc;
    public EjbInfo fubarEjb;
    
    public void setUp() {
        fubarSrc = new ClassInfo( "demo.FubarBean" );
        fubarEjb = new EjbInfo( fubarSrc );
        fubarEjb.setBundle( new EjbBundle( new DocInfo() ));
    }

}
