package vdoclet.docinfo;

import java.util.*;
import junit.framework.TestCase;

public class FieldInfo_Test extends TestCase {

    //---( Setup )---

    public FieldInfo_Test(String name) {
        super(name);
    }
    
    //---( Tests )---
    
    public void testCreate() {
        FieldInfo field = new FieldInfo("int", "dog");
        assertEquals("dog", field.getName());
        assertEquals("int", field.getType());
    }

    public void testToString() {
        FieldInfo fieldInfo = 
            new FieldInfo( "int", "size" );
        assertEquals( "int size", fieldInfo.toString() );
    }

    public void testEquals() {
        FieldInfo f1 = new FieldInfo( "int", "a" );
        FieldInfo f2 = new FieldInfo( "int", "a" );
        assertEquals( f1, f2 );
    }

    public void testNotEquals() {
        FieldInfo f1 = new FieldInfo( "int", "a" );
        FieldInfo f2 = new FieldInfo( "int", "b" );
        FieldInfo f3 = new FieldInfo( "java.lang.String", "b" );
        assertTrue( !f1.equals(f2) );
        assertTrue( !f1.equals(f3) );
        assertTrue( !f2.equals(f3) );
    }

}

