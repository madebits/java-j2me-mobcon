package vdoclet.ejb;

import vdoclet.docinfo.ClassInfo;
import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.io.*;
import java.util.*;

public class EjbInfo_Test extends Ejb_BaseTest {

    //---( Setup )---

    public EjbInfo_Test( String name ) {
        super( name );
    }

    //---( getSrcClass )---

    public void testGetSrcClass() {
        ClassInfo srcClass = new ClassInfo( "demo.FubarBean" );
        EjbInfo ejb = new EjbInfo( srcClass );
        assertEquals( srcClass, ejb.getSrcClass() );
    }

    //---( getBaseName )---

    public void testBaseNameSimple() {
        assertEquals( "demo.Fubar", fubarEjb.getBaseName() );
    }

    public void testBaseNameWithoutBean() {
        EjbInfo ejb = new EjbInfo( new ClassInfo( "demo.Fubar" ));
        assertEquals( "demo.Fubar", ejb.getBaseName() );
    }

    public void testBaseNameExplicit() {
        fubarSrc.addTag( EjbTags.BASE_NAME, "SumfinElts" );
        assertEquals( "SumfinElts", fubarEjb.getBaseName() );
    }

    //---( getName )---

    public void testGetNameDefault() {
        assertEquals( "Fubar", fubarEjb.getName() );
    }

    public void testGetNameExplicit() {
        fubarSrc.addTag( EjbTags.NAME, "Other" );
        assertEquals( "Other", fubarEjb.getName() );
    }

    public void testGetNameExplicitPrefix() {
        fubarSrc.addTag( EjbTags.BASE_NAME, "Other" );
        assertEquals( "Other", fubarEjb.getName() );
    }

    //---( isEntity )---
    
    public void testIsEntityBean() {
        assertTrue( !fubarEjb.isEntity() );
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        assertTrue( fubarEjb.isEntity() );
        assertEquals( "entity", fubarEjb.getType() );
    }
    
    //---( isSession )---
    
    public void testIsSessionBean() {
        assertTrue( !fubarEjb.isSession() );
        fubarSrc.addTag( EjbTags.SESSION, "y" );
        assertTrue( fubarEjb.isSession() );
        assertEquals( "session", fubarEjb.getType() );
    }

    //---( isMessageDriven )---
    
    public void testIsMessageDrivenBean() {
        assertTrue( !fubarEjb.isMessageDriven() );
        fubarSrc.addTag( EjbTags.MESSAGE_DRIVEN, "y" );
        assertTrue( fubarEjb.isMessageDriven() );
        assertEquals( "message-driven", fubarEjb.getType() );
    }

    //---( isAbstract )---
    
    public void testIsAbstractBean() {
        assertTrue( !fubarEjb.isAbstract() );
        fubarSrc.addTag( EjbTags.ABSTRACT, "y" );
        assertTrue( fubarEjb.isAbstract() );
    }

    //---( isEjb )---

    public void testIsEjbEntity() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        assertTrue( fubarEjb.isEjb() );
    }
    
    public void testIsEjbSession() {
        fubarSrc.addTag( EjbTags.SESSION, "y" );
        assertTrue( fubarEjb.isEjb() );
    }

    public void testIsEjbMessageDriven() {
        fubarSrc.addTag( EjbTags.MESSAGE_DRIVEN, "y" );
        assertTrue( fubarEjb.isEjb() );
    }

    public void testIsEjbAbstract() {
        fubarSrc.addTag( EjbTags.ABSTRACT, "y" );
        assertTrue( ! fubarEjb.isEjb() );
    }

    //---( isCmp )---

    public void testIsCmpDefault() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        assertTrue( fubarEjb.isCmpEntity() );
    }

    public void testCmpSession() {
        fubarSrc.addTag( EjbTags.SESSION, "y" );
        assertTrue( "isCmpEntity() should always be false for session beans",
                    !fubarEjb.isCmpEntity() );
    }

    public void testIsCmpExplicit() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        fubarSrc.addTag( EjbTags.PERSISTENCE_TYPE, "Container" );
        assertTrue( fubarEjb.isCmpEntity() );
    }

    public void testIsCmpExplicitlyNot() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        fubarSrc.addTag( EjbTags.PERSISTENCE_TYPE, "Bean" );
        assertTrue( !fubarEjb.isCmpEntity() );
    }

    //---( getCmpFields )---

    public void testGetCmpFields() {
        MethodInfo getIdMethod = new MethodInfo( "java.lang.String", "getId" );
        MethodInfo getNameMethod = new MethodInfo( "java.lang.String", "getName" );
        getIdMethod.addTag( "@ejb-cmp-column", "ID" );
        fubarSrc.addMethod( getIdMethod );
        fubarSrc.addMethod( getNameMethod );
        assertEquals(1, fubarEjb.getCmpFields().size());

        FieldInfo idField =
            (FieldInfo) fubarEjb.getCmpFields().iterator().next();
        assertEquals( "id", idField.getName() );
        assertEquals( "java.lang.String", idField.getType() );
    }

    //---( getCmrFields )---

    public void testGetCmrFieldsEmpty() {
        assertEquals( 0, fubarEjb.getCmrFields().size() );
    }

    public void testGetCmrFieldsOne() {
        MethodInfo getIdMethod = new MethodInfo( "java.lang.String", "getId" );
        MethodInfo getParentMethod = 
            new MethodInfo( "demo.Blarg", "getParent" );
        getParentMethod.addTag( EjbTags.CMR_TARGET, "demo.BlargBean" );
        fubarSrc.addMethod( getIdMethod );
        fubarSrc.addMethod( getParentMethod );
        assertEquals( 1, fubarEjb.getCmrFields().size() );

        FieldInfo parentField =
            (FieldInfo) fubarEjb.getCmrFields().iterator().next();
        assertEquals( "parent", parentField.getName() );
        assertEquals( "demo.Blarg", parentField.getType() );
    }

    //---( getCmpSchemaName )---

    public void testGetCmpSchemaNameDefault() {   
        assertEquals( "Fubar", fubarEjb.getCmpSchemaName() );
    }

    public void testGetCmpSchemaNameFromBaseName() {
        fubarSrc.addTag( EjbTags.BASE_NAME, "SumfinElts" );
        assertEquals( "SumfinElts", fubarEjb.getCmpSchemaName() );
    }

    public void testGetCmpSchemaNameExplicit() {
        fubarSrc.addTag( EjbTags.CMP_SCHEMA_NAME, "Fnord" );
        assertEquals( "Fnord", fubarEjb.getCmpSchemaName() );
    }

    //---( getEjbClass )---

    public void testGetEjbClassDefault() {
        assertEquals( fubarSrc, fubarEjb.getEjbClass() );
    }

    public void testGetEjbClassNormalCmp11() {
        fubarSrc.addTag( EjbTags.ENTITY, "y" );
        fubarSrc.addTag( EjbTags.PERSISTENCE_TYPE, "Container" );
        assertEquals( "demo.FubarCmp11", 
                      fubarEjb.getEjbClass().getName() );
    }

    //---( getTag )---

    public void testGetTagValue() {
        fubarSrc.addTag( "over", "there" );
        assertEquals( "there", fubarEjb.getTagValue( "over" ));
    }

    public void testGetTags() {
        fubarSrc.addTag( "over", "there" );
        List tags = fubarEjb.getTags( "over" );
        assertEquals( 1, tags.size() );
        assertEquals( "there", tags.get(0).toString() );
    }

    public void testGetTagValueInheritDefault() {
        fubarEjb.getBundle().setProperty( "some.property",
                                           "defaultValue" );
        assertEquals( "defaultValue", 
                      fubarEjb.getTagValue( "some.property" ));
    }

    public void testGetTagValueOverrideDefault() {
        fubarEjb.getBundle().setProperty( "some.property",
                                           "defaultValue" );
        fubarSrc.addTag( "some.property", "customValue" );
        assertEquals( "customValue", 
                      fubarEjb.getTagValue( "some.property" ));
    }

    //---( getSrcCmpInnerClass )---

    public void testGetSrcCmpInnerClassNone() {
        assertNull( fubarEjb.getSrcCmpInnerClass() );
    }

    public void testGetSrcCmpInnerClassExists() {
        ClassInfo inner = new ClassInfo( "demo.FubarBean.CMP" );
        fubarEjb.getBundle().getDocInfo().addClass( inner );
        assertEquals( inner, fubarEjb.getSrcCmpInnerClass() );
    }

    //---( getQueryMethods )---

    public void testGetQueryMethodsEmpty() {
        assertEquals( 0, fubarEjb.getQueryMethods().size() );
    }

    public void testGetQueryMethodsOneFinder() {  
        MethodInfo ejbFinder =
            new MethodInfo( "FubarKey", "ejbFindByName" );
        ejbFinder.addParameter( "java.lang.String", "name" );
        fubarEjb.getSrcClass().addMethod( ejbFinder );
        assertEquals( 1, fubarEjb.getQueryMethods().size() );
        MethodInfo finder = 
            (MethodInfo) fubarEjb.getQueryMethods().iterator().next();
        assertEquals( "findByName", finder.getName() );
        assertEquals( 1, finder.getParameters().size() );
        assertEquals( "name", finder.getParameter(0).getName() );
    }

    public void testGetQueryMethodsOneSelect() {  
        MethodInfo ejbSelect =
            new MethodInfo( "FubarKey", "ejbSelectAll" );
        fubarEjb.getSrcClass().addMethod( ejbSelect );
        assertEquals( 1, fubarEjb.getQueryMethods().size() );
        MethodInfo finder = 
            (MethodInfo) fubarEjb.getQueryMethods().iterator().next();
        assertEquals( "ejbSelectAll", finder.getName() );
    }

    //---( getRemoteView )---
    
    public void testGetRemoteView11() {
        assertNotNull( fubarEjb.getRemoteView() );
    }
    
    public void testGetRemoteView20Marked() {   
        fubarSrc.addTag( EjbTags.REMOTE_FLAG, "yup" );
        fubarEjb.getBundle()
            .setProperty( EjbBundle.EJB_VERSION_PROP, "2.0" );
        assertNotNull( fubarEjb.getRemoteView() );
    }
    
    public void testGetRemoteView20Unmarked() {   
        fubarEjb.getBundle()
            .setProperty( EjbBundle.EJB_VERSION_PROP, "2.0" );
        assertNull( fubarEjb.getRemoteView() );
    }
    
    //---( getLocalView )---
    
    public void testGetLocalViewMarked() {   
        fubarSrc.addTag( EjbTags.LOCAL_FLAG, "yup" );
        assertNotNull( fubarEjb.getLocalView() );
    }
    
    public void testGetLocalViewUnmarked() {   
        assertNull( fubarEjb.getLocalView() );
    }
    
}
