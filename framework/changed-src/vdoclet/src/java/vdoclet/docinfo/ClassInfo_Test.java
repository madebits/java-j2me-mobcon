package vdoclet.docinfo;

import junit.framework.TestCase;
import java.util.*;

public class ClassInfo_Test extends TestCase {

    //---( Setup )---

    public ClassInfo_Test( String name ) {
        super( name );
    }

    DocInfo docInfo = new DocInfo();
    
    //---( Tests )---

    public void testCreate() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertEquals( "demo.CustomerRemote", clazz.getName() );
        assertEquals( "demo", clazz.getPackage() );
        assertEquals( "CustomerRemote", clazz.getShortName() );
        assertNull( clazz.getComment() );
        assertNull( clazz.getContainingDocInfo() );
    }

    public void testNullCreate() {
        try {
            new ClassInfo( null );
            fail( "null classname allowed" );
        } catch (IllegalArgumentException e) {}
    }

    public void testCreateNoPackage() {
        ClassInfo clazz = new ClassInfo( "NoPackage" );
        assertEquals( "NoPackage", clazz.getName() );
        assertEquals( null, clazz.getPackage() );
        assertEquals( "NoPackage", clazz.getShortName() );
    }
    
    public void testCreateTwoDots() {
        ClassInfo clazz = new ClassInfo( "one.two.Three" );
        assertEquals( "one.two.Three", clazz.getName() );
        assertEquals( "one.two", clazz.getPackage() );
        assertEquals( "Three", clazz.getShortName() );
    }
    
    public void testDocInfo() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertNull( clazz.getContainingDocInfo() );
        DocInfo docInfo = new DocInfo();
        clazz.setContainingDocInfo( docInfo );
        assertEquals( docInfo, clazz.getContainingDocInfo() );
    }

    public void testSetAbstract() {   
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertTrue( !clazz.isAbstract() );
        clazz.setAbstract( true );
        assertTrue( clazz.isAbstract() );
        assertEquals( "abstract", clazz.getModifiers().toString() );
    }

    public void testSetPublic() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertTrue( !clazz.isPublic() );
        clazz.setPublic( true );
        assertTrue( clazz.isPublic() );
        assertEquals( "public", clazz.getModifiers().toString() );
    }

    //---( Methods )---

    public void testNoMethods() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertEquals( 0, clazz.getMethods().size() );
    }

    public void testAddMethod() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        MethodInfo method = new MethodInfo( "int", "getSize" );
        clazz.addMethod( method );
        assertEquals( "getSize", clazz.getMethod(0).getName() );
        assertEquals( clazz, method.getContainingClass() );
    }

    public void testGetMethodBySignature() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );

        MethodInfo getSizeNoParams = new MethodInfo( "int", "getSize" );
        clazz.addMethod( getSizeNoParams );

        MethodInfo getSizeOneParam = new MethodInfo( "int", "getSize" );
        getSizeOneParam.addParameter( "java.lang.String", "name" );
        clazz.addMethod( getSizeOneParam );
        
        assertEquals( getSizeNoParams, 
                      clazz.getMethod( "getSize", 
                                           Collections.EMPTY_LIST ));

        ArrayList types = new ArrayList();
        types.add( "java.lang.String" );
        assertEquals( getSizeOneParam, clazz.getMethod( "getSize", types ));
    }

    /**
     * Methods aren't inherited from the superclass
     */
    public void testGetMethodsNoInherit() {
        ClassInfo baseClass = docInfo.makeClass( "demo.BaseFoo" );
        ClassInfo subClass = docInfo.makeClass( "demo.ConcreteFoo" );
        subClass.setSuperClass( "demo.BaseFoo" );
        baseClass.addMethod( new MethodInfo( "int", "getSize" ));
        assertEquals( 0, subClass.getMethods().size() );
    }

    //---( Fields )---

    public void testNoFields() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertEquals( 0, clazz.getFields().size() );
    }

    public void testAddField() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        FieldInfo field = new FieldInfo( "int", "size" );
        clazz.addField( field );
        assertEquals( "size", clazz.getField(0).getName() );
        assertEquals( clazz, field.getContainingClass() );
    }

    //---( SuperClass )---

    public void testNoSuperClass() {
        ClassInfo clazz = new ClassInfo( "demo.Foo" );
        assertEquals( "java.lang.Object", clazz.getSuperClass() );
        assertEquals( null, clazz.resolveSuperClass() );
    }
    
    public void testSuperClassOkay() {
        ClassInfo fooClass = docInfo.makeClass( "demo.Foo" );
        ClassInfo barClass = docInfo.makeClass( "demo.Bar" );
        barClass.setSuperClass( "demo.Foo" );
        assertEquals( "demo.Foo", barClass.getSuperClass() );
        assertEquals( fooClass, barClass.resolveSuperClass() );
    }
    
    public void testSuperClassNoDocInfo() {
        ClassInfo fooClass = new ClassInfo( "demo.Foo" );
        ClassInfo barClass = new ClassInfo( "demo.Bar" );
        barClass.setSuperClass( "demo.Foo" );
        assertEquals( "demo.Foo", barClass.getSuperClass() );
        assertEquals( null, barClass.resolveSuperClass() );
    }
    
    public void testSuperClassNotFound() {
        ClassInfo barClass = docInfo.makeClass( "demo.Bar" );
        barClass.setSuperClass( "demo.Foo" );
        assertEquals( "demo.Foo", barClass.getSuperClass() );
        assertEquals( null, barClass.resolveSuperClass() );
    }
    
    //---( Interfaces )---

    public void testNoInterfaces() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertEquals( 0, clazz.getInterfaces().size() );
    }

    public void testAddInterfaces() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        clazz.addInterface( "demo.Demoable" );
        assertEquals( 1, clazz.getInterfaces().size() );
    }

    public void testDoesntImplement() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        assertTrue( !clazz.hasInterface( "demo.Demoable" ));
    }

    public void testImplementsDirect() {
        ClassInfo clazz = new ClassInfo( "demo.CustomerRemote" );
        clazz.addInterface( "demo.Demoable" );
        assertTrue( clazz.hasInterface( "demo.Demoable" ));
    }

    public void testImplementsIndirect() {
        ClassInfo baseClass = docInfo.makeClass( "demo.BaseFoo" );
        docInfo.addClass( baseClass );
        baseClass.addInterface( "demo.Demoable" );
        ClassInfo subClass = docInfo.makeClass( "demo.ConcreteFoo" );
        docInfo.addClass( subClass );
        subClass.setSuperClass( "demo.BaseFoo" );
        assertTrue( subClass.hasInterface( "demo.Demoable" ));
    }

}
