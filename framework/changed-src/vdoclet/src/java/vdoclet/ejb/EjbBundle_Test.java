package vdoclet.ejb;

import vdoclet.docinfo.*;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;

public class EjbBundle_Test extends TestCase {

    //--- Constants ---

    public static final String SRC_PATH = 
        DocInfo_Test.BASEDIR + "/examples/ejbtest/java";
    
    //---( Setup )---
    
    public EjbBundle_Test( String name ) {
        super( name );
    }

    EjbBundle _testBundle;
    
    public void setUp() {
        DocInfo docInfo = new DocInfo();
        docInfo.addClass( makeEntityBean() );
        docInfo.addClass( makeSessionBean() );
        docInfo.addClass( makeAbstractBean() );
        docInfo.addClass( new ClassInfo( "Dummy" ) );
        _testBundle = new EjbBundle( docInfo );
    }
    
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

    ClassInfo makeAbstractBean() {
        ClassInfo srcClass = new ClassInfo( "my.AbstractBean" );
        srcClass.addTag( EjbTags.ABSTRACT, "y" );
        return srcClass;
    }

    //---( Tests )---

    public void testGetEjbs() {
        assertEquals( 2, _testBundle.getEjbs().size() );
        assertTrue( _testBundle.getEjbs().iterator().next() instanceof EjbInfo );
    }

    public void testGetAbstractEjbs() {
        assertEquals( 1, _testBundle.getAbstractEjbs().size() );
    }
    
    public void testGetEjb() {
        assertNotNull( _testBundle.getEjb( "my.EntityBean" ));
    }

    public void testGetBundle() {
        EjbInfo entityInfo = _testBundle.getEjb( "my.EntityBean" );
        assertEquals( _testBundle, entityInfo.getBundle() );
    }

    //---( setProperty/getProperty )---

    public void testGetProperty() {
        _testBundle.setProperty( "blah", "zzz" );
        assertEquals( "zzz", _testBundle.getProperty( "blah" ));
    }

    public void testGetPropertiesStartingWithEmpty() {
        Properties properties = _testBundle.getPropertiesStartingWith("startswith");
        assertTrue(properties.isEmpty());
    }

    public void testGetPropertiesStartingWithNotEmpty() {
        _testBundle.setProperty( "first.one", "1st-one" );
        _testBundle.setProperty( "second.one", "2nd-one" );
        _testBundle.setProperty( "second.two", "2nd-two" );
        Properties properties = _testBundle.getPropertiesStartingWith( "second" );
        assertEquals( 2, properties.size() );
        assertEquals( "2nd-one", properties.get("one") );
    }

    public void testGetPropertiesFromSourcePath() {
        DocInfo docInfo = new DocInfo();
        docInfo.setSourcePath( SRC_PATH );
        EjbBundle ejbBundle = new EjbBundle( docInfo ); 
        assertEquals( "DONT-REMOVE", ejbBundle.getProperty( "test.property" ));
    }

    //---( getEjbVersion )---

    public void testGetEjbVersionDefault() {  
        assertEquals( 1.1f, _testBundle.getEjbVersion(), 0 );
    }

    public void testGetEjbVersion20() {  
        _testBundle.setProperty( EjbBundle.EJB_VERSION_PROP, "2.0" );
        assertEquals( 2.0f, _testBundle.getEjbVersion(), 0 );
    }

    //---( getSecurityRoles )---

    public void testGetSecurityRoles() {
        _testBundle.setProperty( EjbBundle.SECURITY_ROLES_PROP, 
                               "guest,admin" );
        Collection roles = _testBundle.getSecurityRoles();
        assertTrue( roles.contains("admin") );
        assertTrue( roles.contains("guest") );
        assertEquals( 2, roles.size() );
    }

    //---( getRelations )---

    /**
     * Default to an empty collection
     */
    public void testGetRelationsEmpty() {
        assertEquals( 0, _testBundle.getRelations().size() );
    }

    /**
     * Try a one-to-many uni-directional relationship, "Note-Task.notes"
     */
    public void testGetRelationBidirectional() {
        DocInfo docInfo = new DocInfo();

        ClassInfo taskSrc = new ClassInfo( "demo.TaskBean" );
        taskSrc.addTag( EjbTags.ENTITY, "y" );
        MethodInfo getNotesMethod = 
            new MethodInfo( "java.util.Collection", "getNotes" );
        getNotesMethod.addTag( EjbTags.CMR_TARGET, "demo.NoteBean task" );
        getNotesMethod.addTag( "testTag", "task.getNotes()" );
        taskSrc.addMethod( getNotesMethod );
        docInfo.addClass( taskSrc );

        ClassInfo noteSrc = new ClassInfo( "demo.NoteBean" );
        noteSrc.addTag( EjbTags.ENTITY, "y" );  
        MethodInfo getTaskMethod = 
            new MethodInfo( "demo.TaskLocal", "getTask" );
        getTaskMethod.addTag( EjbTags.CMR_TARGET, "demo.TaskBean notes" );
        getTaskMethod.addTag( "testTag", "note.getTask()" );
        noteSrc.addMethod( getTaskMethod );

        docInfo.addClass( noteSrc );

        EjbBundle ejbBundle = new EjbBundle( docInfo );
        assertEquals( 1, ejbBundle.getRelations().size() );

        EjbRelation relation = 
            (EjbRelation) ejbBundle.getRelations().iterator().next();
        assertEquals( "Note.task-Task.notes", relation.getName() );
        assertEquals( "task.getNotes()", 
                      relation.getRole("Task.notes").getTagValue("testTag") );
        assertEquals( "note.getTask()", 
                      relation.getRole("Note.task").getTagValue("testTag") );
    }

}
