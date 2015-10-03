package vdoclet.ejb;

import java.util.*;
import vdoclet.docinfo.*;
import vdoclet.util.*;

/**
 * Represents an EJB client-view
 */
public abstract class EjbView {

    //---( Instance variables )---

    private EjbInfo _ejbInfo;

    //---( Constructor )---

    /**
     * Construct a new EjbView
     */
    public EjbView( EjbInfo ejbInfo ) {
        _ejbInfo = ejbInfo;
    }
    
    //---( Accessors )---

    /**
     * Get the parent EJB
     */
    EjbInfo getEjb() {
        return _ejbInfo;
    }
    
    //---( View-specific things )---

    /** @return name of the view-specific suffix */
    abstract String getViewSuffix();

    /** @return name of the tag used to flag a method */
    abstract String getFlagTag();

    /** @return name of the tag used to set Interface class-name */
    abstract String getInterfaceClassTag();

    /** @return name of the tag used to set Interface super-class */
    abstract String getInterfaceExtendsTag();

    /** @return name of the tag used to set Home class-name */
    abstract String getHomeClassTag();

    /** @return name of the tag used to set Home JNDI-name */
    abstract String getHomeJndiTag();

    /** @return the name of the default Interface super-class */
    abstract String getDefaultSuperInterface();

    /**
     * Extract the appropriate view of the EJB super-class
     */
    abstract EjbView getSuperView( EjbInfo superEjb );

    /**
     * Add exceptions specific to this view to a method
     * @param viewMethod an interface method exposed by this view
     */
    void addViewExceptions( MethodInfo viewMethod ) {
    }

    //---( Useful utils )---

    /**
     * Check whether a something is tagged as being available thru this
     * view
     */
    public boolean isFlagged( BaseElementInfo element ) {
        return element.getTagValue( getFlagTag() ) != null;
    }

    //---( Interface class )---

    /**
     * Get the name of the view interface.
     *
     * The default is based on getBaseName().  This can be
     * overridden with an "@ejb-<i>view</i>-class" tag.
     */
    public String getInterfaceClassName() {
        return getEjb().getTagValue( getInterfaceClassTag(),
                                     getEjb().getBaseName() +
                                     getViewSuffix() );
    }

    /**
     * Convert a method from the srcClass into one suitable for a
     * view interface.
     */
    MethodInfo makeInterfaceMethod( MethodInfo method ) {
        MethodInfo interfaceMethod = (MethodInfo)method.clone();
        addViewExceptions( interfaceMethod );
        return interfaceMethod;
    }

    /**
     * Determine what the view interface should extend
     */
    String getSuperInterface() {
        String explicitValue = getEjb().getTagValue( getInterfaceExtendsTag() );
        if (explicitValue != null) {
            return explicitValue;
        }
        EjbInfo superEjb = getEjb().getSuperEjb();
        if (superEjb != null) {
            return getSuperView( superEjb ).getInterface().getName();
        }
        return getDefaultSuperInterface();
    }

    /**
     * Get a representation of the view interface
     */
    public ClassInfo getInterface() {
        String ifaceName = getInterfaceClassName();
        ClassInfo iface = new ClassInfo( ifaceName );
        Iterator iter = getEjb().getSrcClass().getMethods().iterator();
        while (iter.hasNext()) {
            MethodInfo method = (MethodInfo)iter.next();
            if (!isFlagged( method )) continue;
            if (method.getName().startsWith("ejb")) continue;
            iface.addMethod( makeInterfaceMethod( method ));
        }
        iface.addInterface( getSuperInterface() );
        return iface;
    }

    //---( Home interface )---

    /**
     * Get the name of the view-home interface.
     *
     * The default is based on getInterfaceClassName().  This can be
     * overridden with an "@ejb-<i>view</i>-home-class" tag.
     */
    String getHomeClassName() {
        return getEjb().getTagValue( getHomeClassTag(),
                                     getInterfaceClassName() + "Home" );
    }

    /**
     * Get the name of the view-home interface.
     *
     * The default is the name of the view-home interface.  This can be
     * overridden with an "@ejb-<i>view</i>-home-jndi" tag.
     */
    String getHomeJndiName() {
        return getEjb().getTagValue( getHomeJndiTag(),
                                     getHomeClassName() );
    }
    
    /**
     * Get a representation of the view-home interface.
     */
    public RegisteredClassInfo getHome() {
        RegisteredClassInfo home =
            new RegisteredClassInfo( getHomeClassName(),
                                     getHomeJndiName() );
        for (Iterator i = getEjb().getAllSrcMethods().iterator(); 
             i.hasNext();) 
        {
            MethodInfo method = (MethodInfo) i.next();
            if (! isFlagged( method )) continue;
            if (method.getName().startsWith("ejbCreate")) {
                home.addMethod( makeCreateMethod( method ));
            } else if (method.getName().startsWith("ejbFind")) {
                home.addMethod( makeFinderMethod( method ));
            } else if (method.getName().startsWith("ejbHome")) {
                home.addMethod( makeHomeMethod( method ));
            }
        }
        return home;
    }

    /**
     * Derive the signature of a create() method.
     * @param ejbCreate an ejbCreate() method
     */
    MethodInfo makeCreateMethod( MethodInfo ejbCreate ) {
        MethodInfo create = (MethodInfo)ejbCreate.clone();
        create.setName( StringUtils.stripPrefix
                        ( "ejb", ejbCreate.getName() ));
        addViewExceptions( create );
        create.setType( getInterfaceClassName() );
        return create;
    }

    /**
     * Derive the signature of a finder() method.
     * @param ejbFinder an ejbFindXYZ() method
     */
    MethodInfo makeFinderMethod( MethodInfo ejbFinder ) {
        MethodInfo finder = (MethodInfo)ejbFinder.clone();
        finder.setName( StringUtils.stripPrefix
                        ( "ejb", ejbFinder.getName() ));
        addViewExceptions( finder );
        if (finder.getType().equals( getEjb().getPrimaryKeyClassName() )) {
            finder.setType( getInterfaceClassName() );
        }
        return finder;
    }

    /**
     * Derive the signature of a home-interface method.
     * @param ejbHomeMethod an ejbHomeXYZ() method
     */
    MethodInfo makeHomeMethod( MethodInfo ejbHomeMethod ) {
        MethodInfo homeMethod = (MethodInfo)ejbHomeMethod.clone();
        homeMethod.setName( StringUtils.stripPrefix
                            ( "ejbHome", ejbHomeMethod.getName() ));
        addViewExceptions( homeMethod );
        return homeMethod;
    }

}

