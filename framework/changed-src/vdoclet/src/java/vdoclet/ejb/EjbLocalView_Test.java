package vdoclet.ejb;

import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.util.*;

/**
 * Test derivation of local-interfaces
 */
public class EjbLocalView_Test extends Ejb_BaseTest {

    //---( Setup )---

    public EjbLocalView_Test( String name ) {
        super( name );
    }

    //---( Fixtures and utilities )---

    public void setUp() {
        super.setUp();
        fubarSrc.addTag( EjbTags.LOCAL_FLAG, "y" );
    }

    EjbLocalView getView( EjbInfo ejbInfo ) {
        return ejbInfo.getLocalView();
    }
    
    ClassInfo getViewInterface( EjbInfo ejbInfo ) {
        return getView( ejbInfo ).getInterface();
    }
    
    RegisteredClassInfo getViewHome( EjbInfo ejbInfo ) {
        return getView( ejbInfo ).getHome();
    }
    
    //---( getInterfaceClassName )---
    
    public void testGetInterfaceClassNameDerived() {
        assertEquals( "demo.FubarLocal", 
                      getView( fubarEjb ).getInterfaceClassName() );
    }

    public void testGetInterfaceClassNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_CLASS, "Blah" );
        assertEquals( "Blah", 
                      getView( fubarEjb ).getInterfaceClassName() );
    }
    
    //---( getInterface )---

    public void testGetInterface() {
        assertNotNull( getView( fubarEjb ).getInterface() );
        assertEquals( "demo.FubarLocal", 
                      getView( fubarEjb ).getInterface().getName() );
    }
    
    private void checkInterfaceMethod( MethodInfo method,
                                       MethodInfo interfaceMethod )
    {
        assertEquals( method.getName(), interfaceMethod.getName() );
        assertEquals( method.getType(), interfaceMethod.getType() );
        assertEquals( method.getParameters(), interfaceMethod.getParameters() );
        assertEquals( method.getExceptions().size(),
                      interfaceMethod.getExceptions().size() );
    }
    
    public void testGetInterfaceMethods() {
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_CLASS, 
                                        "demo.FubarLocal" );
        MethodInfo method = new MethodInfo( "int", "getBalance" );
        method.addTag( EjbTags.LOCAL_FLAG, "" );
        fubarEjb.getSrcClass().addMethod(method);
        List interfaceMethods = getViewInterface( fubarEjb ).getMethods();
        assertEquals( 1, interfaceMethods.size() );
        MethodInfo interfaceMethod = (MethodInfo)interfaceMethods.get(0);
        checkInterfaceMethod( method, interfaceMethod );
    }

    public void testGetInterfaceMethodsStartsWithEjb() {
        MethodInfo method = new MethodInfo( "int", "ejbHomeGetBalance" );
        method.addTag( EjbTags.LOCAL_FLAG, "" );
        fubarEjb.getSrcClass().addMethod(method);
        assertEquals( 0, getViewInterface( fubarEjb ).getMethods().size() );
    }

    public void testGetInterfaceExtendsDefault() {
        assertTrue( getViewInterface( fubarEjb )
                    .hasInterface( "javax.ejb.EJBLocalObject" ));
    }

    public void testGetInterfaceExtendsSuper() {  
        ClassInfo shapeBean = new ClassInfo( "demo.ShapeBean" );
        shapeBean.addTag( EjbTags.LOCAL_FLAG, "y" );
        shapeBean.addTag( EjbTags.ABSTRACT, "y" );
        ClassInfo circleBean = new ClassInfo( "demo.CircleBean" );
        circleBean.setSuperClass( "demo.ShapeBean" );
        circleBean.addTag( EjbTags.LOCAL_FLAG, "y" );
        circleBean.addTag( EjbTags.ENTITY, "y" );
        DocInfo docInfo = new DocInfo();
        docInfo.addClass( shapeBean );
        docInfo.addClass( circleBean );
        EjbBundle ejbBundle = new EjbBundle( docInfo );
        
        EjbInfo circleEjb = ejbBundle.getEjb( "demo.CircleBean" );
        assertNotNull( circleEjb );
        ClassInfo circleInterface = getViewInterface( circleEjb );
        assertTrue( "Circle doesn't extend Shape", // '
                    circleInterface.hasInterface( "demo.ShapeLocal" ));
    }

    public void testGetInterfaceExtendsExplicit() { 
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_EXTENDS, "demo.Frob" );
        assertTrue( getViewInterface( fubarEjb ).hasInterface( "demo.Frob" ));
    }

    //---( getHomeClassName )---

    public void testGetLocalHomeClassNameDerived() {
        assertEquals( "demo.FubarLocalHome", 
                      getView( fubarEjb ).getHomeClassName() );
    }

    public void testGetHomeClassNameDerivedFromInterface() {
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_CLASS, "Blah" );
        assertEquals( "BlahHome", getView( fubarEjb ).getHomeClassName() );
    }

    public void testGetHomeClassNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_HOME_CLASS, "BlahHome" );
        assertEquals( "BlahHome", getView( fubarEjb ).getHomeClassName() );
    }

    //---( getHomeJndiName )---

    public void testGetHomeJndiNameDerived() {
        assertEquals( "demo.FubarLocalHome", 
                      getView( fubarEjb ).getHomeJndiName() );
    }

    public void testGetHomeJndiNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.LOCAL_HOME_JNDI, 
                                      "beans.Fubar" );
        assertEquals( "beans.Fubar", 
                      getView( fubarEjb ).getHomeJndiName() );
    }

    //---( getHome )---

    public void testGetHomeMethod() {
        MethodInfo ejbHomeMethod =
            new MethodInfo( "int", "ejbHomeCountAll" );
        ejbHomeMethod.addTag( EjbTags.LOCAL_FLAG, "yup" );
        fubarEjb.getSrcClass().addMethod( ejbHomeMethod );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertNotNull( viewHome.getMethod( "countAll",
                                           Collections.EMPTY_LIST ));
    }

}
