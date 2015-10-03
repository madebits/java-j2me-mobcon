package vdoclet.ejb;

import vdoclet.docinfo.*;
import java.util.*;

/**
 * Represents an EJB local client-view
 */
public class EjbLocalView extends EjbView {

    //---( Constructor )---

    /**
     * Construct a new EjbLocalView
     */
    public EjbLocalView( EjbInfo ejbInfo ) {
        super( ejbInfo );
    }
    
    //---( Tags )---

    String getViewSuffix()		{ return "Local"; }
    String getFlagTag()			{ return EjbTags.LOCAL_FLAG; }
    String getInterfaceClassTag() 	{ return EjbTags.LOCAL_CLASS; }
    String getInterfaceExtendsTag() 	{ return EjbTags.LOCAL_EXTENDS; }
    String getHomeClassTag() 		{ return EjbTags.LOCAL_HOME_CLASS; }
    String getHomeJndiTag() 		{ return EjbTags.LOCAL_HOME_JNDI; }
    String getDefaultSuperInterface()   { return "javax.ejb.EJBLocalObject"; }

    //---( Local-view specifics )---

    EjbView getSuperView( EjbInfo superEjb ) {
        return superEjb.getLocalView();
    }

}

