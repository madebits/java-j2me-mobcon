package vdoclet.ejb;

import vdoclet.docinfo.ClassInfo;
import junit.framework.TestCase;
import vdoclet.docinfo.*;
import java.io.*;
import java.util.*;

public class EjbRelation_Test extends TestCase {

    //---( Setup )---

    public EjbRelation_Test( String name ) {
        super( name );
    }
    
    ClassInfo taskSrc;
    MethodInfo getNotesMethod;
    EjbRelationRole taskRole;

    ClassInfo noteSrc;
    EjbRelationRole noteRole;
    
    EjbRelation relation;
    
    public void setUp() {

        taskSrc = new ClassInfo( "demo.TaskBean" );
        taskSrc.addTag( EjbTags.ENTITY, "y" );
        getNotesMethod = new MethodInfo( "java.util.Collection", "getNotes" );
        taskSrc.addMethod( getNotesMethod );
        taskRole = new EjbRelationRole( new EjbInfo( taskSrc ));
        taskRole.setField( getNotesMethod );
        
        noteSrc = new ClassInfo( "demo.NoteBean" );
        noteSrc.addTag( EjbTags.ENTITY, "y" );
        noteRole = new EjbRelationRole( new EjbInfo( noteSrc ));
        
        relation = new EjbRelation( taskRole, noteRole );
    }

    //---( Tests )---

    public void testSetup() {
        assertNotNull( taskRole );
        assertNotNull( noteRole );
        assertNotNull( relation );
    }
    
    /**
     * Relationship name is based on the two EJB-names, sorted
     * alphabetically, and separated by a dash.
     */
    public void testGetName() {
        assertEquals( "Note-Task.notes", relation.getName() );
    }

    /**
     * Creation of the relationship should associate the two roles
     */
    public void testTargets() {
        assertEquals( taskRole, noteRole.getTarget() );
        assertEquals( noteRole, taskRole.getTarget() );
    }

    /**
     * There should always be two roles.
     */
    public void testGetRoles() {
        Collection roles = relation.getRoles();
        assertEquals( 2, roles.size() );
        assertTrue( roles.contains( taskRole ));
        assertTrue( roles.contains( noteRole ));
    }

    /**
     * Lookup roles by name
     */
    public void testGetRole() {
        assertEquals( taskRole, relation.getRole( "Task.notes" ));
        assertEquals( noteRole, relation.getRole( "Note" ));
        assertEquals( null, relation.getRole( "" ));
        assertEquals( null, relation.getRole( "Task" ));
    }

}

