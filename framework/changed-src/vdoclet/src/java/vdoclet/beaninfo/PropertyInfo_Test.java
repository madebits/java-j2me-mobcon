package vdoclet.beaninfo;

import junit.framework.TestCase;
import vdoclet.docinfo.*;

public class PropertyInfo_Test extends TestCase {

    //---( Setup )---

    public PropertyInfo_Test(String name) {
        super(name);
    }
    
    //---( Tests )---
    
    public void testCreate() {
        MethodInfo getFoo = new MethodInfo( "int", "getFoo" );
        MethodInfo setFoo = new MethodInfo( "void", "setFoo" );
        setFoo.addParameter( "int", "foo" );
        PropertyInfo property = 
            new PropertyInfo( "int", "foo", getFoo, setFoo );
        assertEquals( "foo", property.getName() );
        assertEquals( "int", property.getType() );
        assertEquals( getFoo, property.getGetter() );
        assertEquals( setFoo, property.getSetter() );
    }

    public void testCreateFromGetter() {
        MethodInfo getFoo = new MethodInfo( "int", "getFoo" );
        PropertyInfo property = PropertyInfo.fromGetter( getFoo );
        assertEquals( "foo", property.getName() );
        assertEquals( "int", property.getType() );
        assertEquals( getFoo, property.getGetter() );
        assertEquals( "setFoo", property.getSetter().getName() );
        assertEquals( "void", property.getSetter().getType() );
        assertEquals( 1, property.getSetter().getParameters().size() );
        assertEquals( "int", property.getSetter().getParameter(0).getType() );
    }

    public void testCreateFromField() {
        PropertyInfo property = 
            PropertyInfo.fromField( "int", "foo" );
        assertEquals( "foo", property.getName() );
        assertEquals( "int", property.getType() );
        assertEquals( "getFoo", property.getGetter().getName() );
        assertEquals( "int", property.getGetter().getType() );
        assertEquals( "setFoo", property.getSetter().getName() );
        assertEquals( "void", property.getSetter().getType() );
        assertEquals( 1, property.getSetter().getParameters().size() );
        assertEquals( "int", property.getSetter().getParameter(0).getType() );
    }

    public void testCreateFromBooleanField() {
        PropertyInfo property = 
            PropertyInfo.fromField( "boolean", "bar" );
        assertEquals( "isBar", property.getGetter().getName() );
    }

    /**
     * Get tags from getter 
     */
    public void testGetTag() {
        MethodInfo getFoo = new MethodInfo( "int", "getFoo" );
        getFoo.addTag( "p", "5" );
        PropertyInfo property = 
            new PropertyInfo( "int", "foo", getFoo, null );
        assertEquals( "5", property.getTagValue("p") );
    }
    
}

