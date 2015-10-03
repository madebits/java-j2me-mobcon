package vdoclet.ejb;

import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.util.*;

/**
 * Test derivation of remote-interfaces
 */
public class EjbRemoteView_Test extends Ejb_BaseTest {

    //---( Setup )---

    public EjbRemoteView_Test( String name ) {
        super( name );
    }

    //---( Fixtures and utilities )---

    EjbRemoteView getView( EjbInfo ejbInfo ) {
        return ejbInfo.getRemoteView();
    }
    
    ClassInfo getViewInterface( EjbInfo ejbInfo ) {
        return getView( ejbInfo ).getInterface();
    }
    
    RegisteredClassInfo getViewHome( EjbInfo ejbInfo ) {
        return getView( ejbInfo ).getHome();
    }
    
    //---( getInterfaceClassName )---
    
    public void testGetInterfaceClassNameDerived() {
        assertEquals( "demo.Fubar", 
                      getView( fubarEjb ).getInterfaceClassName() );
    }

    public void testGetInterfaceClassNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_CLASS, "Blah" );
        assertEquals( "Blah", 
                      getView( fubarEjb ).getInterfaceClassName() );
    }
    
    //---( getInterface )---

    public void testGetInterface() {
        assertNotNull( getView( fubarEjb ).getInterface() );
        assertEquals( "demo.Fubar", 
                      getView( fubarEjb ).getInterface().getName() );
    }
    
    private void checkInterfaceMethod( MethodInfo method,
                                       MethodInfo interfaceMethod )
    {
        assertEquals( method.getName(), interfaceMethod.getName() );
        assertEquals( method.getType(), interfaceMethod.getType() );
        assertEquals( method.getParameters(), interfaceMethod.getParameters() );
        assertEquals( method.getExceptions().size() + 1,
                      interfaceMethod.getExceptions().size() );
        assertTrue( interfaceMethod.getExceptions()
                    .contains( "java.rmi.RemoteException" ));
    }
    
    public void testGetInterfaceMethods() {
        MethodInfo method = new MethodInfo( "int", "getBalance" );
        method.addTag( EjbTags.REMOTE_FLAG, "" );
        fubarEjb.getSrcClass().addMethod(method);
        List interfaceMethods = getViewInterface( fubarEjb ).getMethods();
        assertEquals( 1, interfaceMethods.size() );
        MethodInfo interfaceMethod = (MethodInfo)interfaceMethods.get(0);
        checkInterfaceMethod( method, interfaceMethod );
    }

    public void testGetInterfaceMethodsStartsWithEjb() {
        MethodInfo method = new MethodInfo( "int", "ejbHomeGetBalance" );
        method.addTag( EjbTags.REMOTE_FLAG, "" );
        fubarEjb.getSrcClass().addMethod(method);
        assertEquals( 0, getViewInterface( fubarEjb ).getMethods().size() );
    }

    public void testGetInterfaceExtendsEJBObject() {
        assertTrue( getViewInterface( fubarEjb )
                    .hasInterface( "javax.ejb.EJBObject" ));
    }

    public void testGetInterfaceExtendsSuper() {  
        ClassInfo shapeBean = new ClassInfo( "demo.ShapeBean" );
        shapeBean.addTag( EjbTags.ABSTRACT, "y" );
        ClassInfo circleBean = new ClassInfo( "demo.CircleBean" );
        circleBean.setSuperClass( "demo.ShapeBean" );
        circleBean.addTag( EjbTags.ENTITY, "y" );
        DocInfo docInfo = new DocInfo();
        docInfo.addClass( shapeBean );
        docInfo.addClass( circleBean );
        EjbBundle ejbBundle = new EjbBundle( docInfo );
        
        ClassInfo circleInterface = 
            getViewInterface( ejbBundle.getEjb( "demo.CircleBean" ));
        ClassInfo shapeInterface = 
            getViewInterface( ejbBundle.getEjb( "demo.ShapeBean" ));
        assertTrue( "Circle doesn't extend Shape", // '
                    circleInterface.hasInterface( shapeInterface.getName() ));
    }

    public void testGetInterfaceExtendsExplicit() { 
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_EXTENDS, "demo.Frob" );
        assertTrue( getViewInterface( fubarEjb ).hasInterface( "demo.Frob" ));
    }

    //---( getHomeClassName )---

    public void testGetRemoteHomeClassNameDerived() {
        assertEquals( "demo.FubarHome", 
                      getView( fubarEjb ).getHomeClassName() );
    }

    public void testGetHomeClassNameDerivedFromRemote() {
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_CLASS, "Blah" );
        assertEquals( "BlahHome", getView( fubarEjb ).getHomeClassName() );
    }

    public void testGetHomeClassNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_HOME_CLASS, "BlahHome" );
        assertEquals( "BlahHome", getView( fubarEjb ).getHomeClassName() );
    }

    //---( getHomeJndiName )---

    public void testGetHomeJndiNameDerived() {
        assertEquals( "demo.FubarHome", 
                      getView( fubarEjb ).getHomeJndiName() );
    }

    public void testGetHomeJndiNameExplicit() {
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_HOME_JNDI, 
                                        "beans.Fubar" );
        assertEquals( "beans.Fubar", 
                      getView( fubarEjb ).getHomeJndiName() );
    }

    //---( makeCreateMethod )---

    public void testMakeCreateMethodSimple() {
        MethodInfo ejbCreate =
            new MethodInfo( "demo.FubarKey", "ejbCreate" );
        MethodInfo create = 
            getView( fubarEjb ).makeCreateMethod( ejbCreate );
        assertEquals( "create", create.getName() );
        assertEquals( "demo.Fubar", create.getType() );
        assertTrue( create.getExceptions().contains( "java.rmi.RemoteException" ));
    }

    public void testMakeCreateMethodParameters() {
        MethodInfo ejbCreate =
            new MethodInfo( "demo.FubarKey", "ejbCreate" );
        ParameterInfo sizeParam = new ParameterInfo( "int", "size" );
        ejbCreate.addParameter( sizeParam );
        MethodInfo create = 
            getView( fubarEjb ).makeCreateMethod( ejbCreate );
        assertEquals( sizeParam, create.getParameter(0) );
    }

    public void testMakeCreateMethodWeird() {
        MethodInfo ejbCreate =
            new MethodInfo( "demo.FubarKey", "ejbCreateWeird" );
        MethodInfo create = 
            getView( fubarEjb ).makeCreateMethod( ejbCreate );
        assertEquals( "createWeird", create.getName() );
    }

    //---( makeFinderMethod )---

    /**
     * If ejbFindXXX() returns a primary-key, findXXX() should return the
     * interface type
     */
    public void testMakeFinderMethodSimple() {
        MethodInfo ejbFinder =
            new MethodInfo( "demo.FubarKey", "ejbFindByName" );
        MethodInfo finder = getView( fubarEjb ).makeFinderMethod( ejbFinder );
        assertEquals( "findByName", finder.getName() );
        assertEquals( "demo.Fubar", finder.getType() );
        assertTrue( finder.getExceptions().contains( "java.rmi.RemoteException" ));
    }

    /**
     * If ejbFindXXX() returns a Collection, so should findXXX()
     */
    public void testMakeFinderMethodCollection() {
        MethodInfo ejbFinder =
            new MethodInfo( "java.util.Collection", "ejbFindAll" );
        MethodInfo finder = getView( fubarEjb ).makeFinderMethod( ejbFinder );
        assertEquals( "findAll", finder.getName() );
        assertEquals( "java.util.Collection", finder.getType() );
    }

    //---( makeHomeMethod )---

    /**
     * FubarBean.ejbHomeCountAll() should map to FubarHome.countAll()
     */
    public void testMakeHomeMethod() {
        MethodInfo ejbHomeCountAll =
            new MethodInfo( "int", "ejbHomeCountAll" );
        MethodInfo countAll = getView( fubarEjb ).makeHomeMethod( ejbHomeCountAll );
        assertEquals( "countAll", countAll.getName() );
        assertEquals( "int", countAll.getType() );
    }

    //---( getHome )---

    public void testGetHome() {
        fubarEjb.getSrcClass().addTag( EjbTags.REMOTE_CLASS, "demo.Fubar" );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertEquals( "demo.FubarHome", viewHome.getName() );
        assertEquals( "demo.FubarHome", viewHome.getJndiName() );
    }

    public void testGetHomeCreate() {
        MethodInfo ejbCreate =
            new MethodInfo( "demo.FubarKey", "ejbCreate" );
        ejbCreate.addTag( EjbTags.REMOTE_FLAG, "yup" );
        ejbCreate.addParameter( new ParameterInfo( "int", "size" ));
        fubarEjb.getSrcClass().addMethod( ejbCreate );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        List paramTypes = Arrays.asList( new String[] { "int" } );
        assertNotNull( viewHome.getMethod( "create", paramTypes ));
    }

    public void testGetHomeFinder() {
        MethodInfo ejbFinder =
            new MethodInfo( "java.util.Collection", "ejbFindAll" );
        ejbFinder.addTag( EjbTags.REMOTE_FLAG, "yup" );
        fubarEjb.getSrcClass().addMethod( ejbFinder );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertNotNull( viewHome.getMethod( "findAll",
                                             Collections.EMPTY_LIST ));
    }

    public void testGetHomeFinderFromInner() {
        DocInfo docInfo = new DocInfo();
        ClassInfo inner = new ClassInfo( "demo.FubarBean.CMP" );    
        MethodInfo ejbFinder =
            new MethodInfo( "java.util.Collection", "ejbFindAll" );
        ejbFinder.addTag( EjbTags.REMOTE_FLAG, "" );
        inner.addMethod( ejbFinder );
        fubarEjb.getBundle().getDocInfo().addClass( inner );

        assertEquals( inner, fubarEjb.getSrcCmpInnerClass() );
        
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertNotNull( viewHome.getMethod( "findAll",
                                             Collections.EMPTY_LIST ));
    }

    public void testGetHomeFinderUnmarked() {
        // Check that it doesn't come thru unless tagged remote
        MethodInfo ejbFinder =
            new MethodInfo( "java.util.Collection", "ejbFindAll" );
        fubarEjb.getSrcClass().addMethod( ejbFinder );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertNull( viewHome.getMethod( "findAll",
                                        Collections.EMPTY_LIST ));
    }

    public void testGetHomeMethod() {
        MethodInfo ejbHomeMethod =
            new MethodInfo( "int", "ejbHomeCountAll" );
        ejbHomeMethod.addTag( EjbTags.REMOTE_FLAG, "yup" );
        fubarEjb.getSrcClass().addMethod( ejbHomeMethod );
        RegisteredClassInfo viewHome = getViewHome( fubarEjb );
        assertNotNull( viewHome.getMethod( "countAll",
                                           Collections.EMPTY_LIST ));
    }

}
