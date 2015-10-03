package vdoclet.ejb;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import vdoclet.beaninfo.PropertyInfo;
import vdoclet.docinfo.*;

public class EjbRelationRole_Test extends TestCase {

    //---( Setup )---

    public EjbRelationRole_Test( String name ) {
        super( name );
    }
    
    ClassInfo taskSrc;
    MethodInfo getProjectMethod;
    MethodInfo getParentMethod;
    MethodInfo getNotesMethod;
    PropertyInfo getNotesField;
    EjbInfo taskEjb;
    EjbRelationRole taskRole;
    ClassInfo noteSrc;
    EjbInfo noteEjb;
    EjbRelationRole noteRole;
    
    public void setUp() {
        taskSrc = new ClassInfo( "demo.TaskBean" );
        taskSrc.addTag( EjbTags.ENTITY, "y" );
        getProjectMethod = new MethodInfo( "demo.ProjectLocal", "getProject" );
        taskSrc.addMethod( getProjectMethod );
        getNotesMethod = new MethodInfo( "java.util.Collection", "getNotes" );
        getNotesField = PropertyInfo.fromGetter( getNotesMethod );
        taskSrc.addMethod( getNotesMethod );
        taskEjb = new EjbInfo( taskSrc );
        taskRole = new EjbRelationRole( taskEjb );
        noteSrc = new ClassInfo( "demo.NoteBean" );
        noteSrc.addTag( EjbTags.ENTITY, "y" );
        noteEjb = new EjbInfo( noteSrc );
        noteRole = new EjbRelationRole( noteEjb );
    }

    //---( Tests )---

    /**
     * getEjb() returns the source EJB
     */
    public void testGetEjb() {
        assertEquals( taskEjb, taskRole.getEjb() );
    }

    /**
     * Without a CMR field, the role-name is just the EjbName
     */
    public void testGetNameDefault() {
        assertEquals( "Task", taskRole.getName() );
    }
    
    /**
     * With a CMR field, role-name is ejb.name + field.name
     */
    public void testGetNameWithField() {
        taskRole.setField( getNotesMethod );
        assertEquals( "Task.notes", taskRole.getName() );
    }

    /**
     * With no CMR field, role-name is still ejb.name
     */
    public void testGetNameWithTarget() {
        noteRole.setTarget( taskRole );
        assertEquals( "Note", noteRole.getName() );
    }

    /**
     * CMR-field defaults to undefined
     */
    public void testGetSetField() {
        assertNull( taskRole.getField() );
        taskRole.setField( getNotesField );
        assertEquals( getNotesField, taskRole.getField() );
    }

    /**
     * Get the name of the CMR-field
     */
    public void testGetFieldName() {
        assertNull( taskRole.getFieldName() );
        taskRole.setField( getNotesMethod );
        assertEquals( "notes", taskRole.getFieldName() );
    }

    /**
     * Get the type of the CMR-field, if it's Set or Collection
     */
    public void testGetFieldType() {
        assertEquals( null, taskRole.getFieldType() );
        taskRole.setField( getNotesMethod );
        assertEquals( "java.util.Collection", taskRole.getFieldType() );
        taskRole.setField( getProjectMethod );
        assertEquals( null, taskRole.getFieldType() );
    }

    /**
     * Can also pass CMR-field in constructor
     */
    public void testGetFieldDefinedConstructor() {
        taskRole = new EjbRelationRole( taskEjb, getNotesField );
        assertEquals( getNotesField, taskRole.getField() );
    }

    /**
     * Tags should propagate to the field
     */
    public void testGetTagValue() {
        getNotesMethod.addTag( "@snort", "slurp" );
        taskRole.setField( getNotesMethod );
        assertEquals( "slurp", taskRole.getTagValue( "@snort" ));
    }

    /**
     * Check that multiplicity constants convert correctly to strings
     */
    public void testMultiplicityToString() {
        assertEquals( "One", EjbRelationRole.Multiplicity.ONE.toString() );
        assertEquals( "Many", EjbRelationRole.Multiplicity.MANY.toString() );
    }
    
    /**
     * Set/get target
     */
    public void testTarget() {
        assertEquals( null, taskRole.getTarget() );
        taskRole.setTarget( noteRole );
        assertEquals( noteRole, taskRole.getTarget() );
    }

    /**
     * Multiplicity defaults to ONE
     */
    public void testMultiplicityDefault() {
        assertEquals( EjbRelationRole.Multiplicity.ONE, 
                      taskRole.getMultiplicity() );
    }
    
    /**
     * If the target CMR field is a Collection/Set, multiplicity is MANY
     */
    public void testMultiplicityMany() {
        taskRole.setField( getNotesMethod );
        noteRole.setTarget( taskRole );
        assertEquals( EjbRelationRole.Multiplicity.MANY, 
                      noteRole.getMultiplicity() );
    }

    /**
     * If the target CMR field is not a Collection/Set, multiplicity is ONE
     */
    public void testMultiplicityOne() {
        getNotesMethod.setType( "java.lang.Object" );
        taskRole.setField( getNotesMethod );
        noteRole.setTarget( taskRole );
        assertEquals( EjbRelationRole.Multiplicity.ONE, 
                      noteRole.getMultiplicity() );
    }
    
    /**
     * But, multiplicity is MANY if there's an @ejb-cmr-target-many tag
     */
    public void testMultiplicityManyExplicit() {
        MethodInfo getParentMethod = 
            new MethodInfo( "demo.TaskLocal", "getParent" );
        getParentMethod.addTag( EjbTags.CMR_TARGET_MULTIPLICITY, "Many" );
        taskSrc.addMethod( getParentMethod );
        taskRole.setField( getParentMethod );
        noteRole.setTarget( new EjbRelationRole( taskEjb ));
        assertEquals( EjbRelationRole.Multiplicity.MANY, 
                      taskRole.getMultiplicity() );
    }

}

