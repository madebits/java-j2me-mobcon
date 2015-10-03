package vdoclet.docinfo;

import junit.framework.TestCase;

public class Builder_Test extends TestCase {

    //---( Setup )---

    public Builder_Test( String name ) {
        super( name );
    }

    Builder builder = new Builder();
    
    //---( Tests )---

    public void testStartingPoint() {
        assertEquals( 0, builder.getDocInfo().getClasses().size() );
    }

    //---( DocInfo )---

    public void testAddClassOnce() {
        builder.addClass( "foo.Bar" );
        assertEquals( 1, builder.getDocInfo().getClasses().size() );
        ClassInfo clazz = (ClassInfo)
            builder.getDocInfo().getClasses().iterator().next();
        assertEquals( "foo.Bar", clazz.getName() );
    }

    public void testAddClassTwice() {
        builder.addClass( "foo.Bar" );
        builder.addClass( "blah.Baz" );
        assertEquals( 2, builder.getDocInfo().getClasses().size() );
    }

    //---( BaseInfo )---

    public void testAddCommentToClass() {
        builder.addClass( "foo.Bar" );
        builder.setComment( "blah blah" );
        assertEquals( "blah blah", 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" )
                      .getComment() );
    }

    public void testAddCommentToMethod() {
        builder.addClass( "foo.Bar" );
        builder.addMethod( "void", "setUp" );
        builder.setComment( "blah blah" );
        assertEquals( null, 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" )
                      .getComment() );
        assertEquals( "blah blah", 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" ).getMethod(0)
                      .getComment() );
    }

    public void testAddTagsToClass() {
        builder.addClass( "foo.Bar" );
        builder.addTag( "x", "1" );
        assertEquals( "1", 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" )
                      .getTagValue("x") );
    }

    //---( BaseElementInfo )---
    
    public void testSetPublicClass() {
        builder.addClass( "foo.Bar" );
        ClassInfo clazz = builder.getDocInfo().getClass( "foo.Bar" );
        assertEquals( false, clazz.isPublic() );
        builder.setPublic( true );
        assertEquals( true, clazz.isPublic() );
    }
    
    //---( ClassInfo )---

    public void testAddMethod() {
        builder.addClass( "foo.Bar" );
        builder.addMethod( "void", "setUp" );
        assertEquals( 1, 
                      builder.getDocInfo().getClass( "foo.Bar" )
                      .getMethods().size() );
    }

    public void testAddMethodWithoutClass() {
        try {
            builder.addMethod( "void", "setUp" );
            fail( "IllegalStateException expected" );
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testSetSuperClass() {
        builder.addClass( "foo.Bar" );
        builder.setSuperClass( "foo.Snort" );
        assertEquals( "foo.Snort", 
                      builder.getDocInfo().getClass( "foo.Bar" ).getSuperClass() );
    }

    public void testAddField() {
        builder.addClass( "foo.Bar" );
        builder.addField( "int", "level" );
        assertEquals( 1, 
                      builder.getDocInfo().getClass( "foo.Bar" )
                      .getFields().size() );
    }

    //---( MethodInfo )---

    public void testAddParameter() {
        builder.addClass( "foo.Bar" );
        builder.addMethod( "void", "setUp" );
        builder.addParameter( "int", "x" );
        assertEquals( 1, 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" ).getMethod(0)
                      .getParameters().size() );
    }

    public void testAddException() {
        builder.addClass( "foo.Bar" );
        builder.addMethod( "void", "setUp" );
        builder.addException( "IllegalArgumentException" );
        assertEquals( 1, 
                      builder.getDocInfo()
                      .getClass( "foo.Bar" )
                      .getMethod(0)
                      .getExceptions().size() );
    }

}
