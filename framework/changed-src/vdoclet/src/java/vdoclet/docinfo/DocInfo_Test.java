package vdoclet.docinfo;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

public class DocInfo_Test extends TestCase {

    //--- Constants ---

    public static final String BASEDIR = 
        System.getProperty( "ant.basedir", "." );
    
    //---( Tests )---

    public DocInfo_Test( String name ) {
        super(name);
    }

    public void testCreate() {
        DocInfo docInfo = new DocInfo();
        assertEquals( 0, docInfo.getClasses().size() );
    }

    public void testAddClass() {
        DocInfo docInfo = new DocInfo();
        ClassInfo myClass = new ClassInfo( "mypkg.MyClass" );
        assertEquals( null, myClass.getContainingDocInfo() );
        docInfo.addClass( myClass );
        assertEquals( docInfo, myClass.getContainingDocInfo() );
        assertTrue( docInfo.getClasses().contains( myClass ));
    }

    public void testGetClass() {
        DocInfo docInfo = new DocInfo();
        ClassInfo myClass = new ClassInfo( "mypkg.MyClass" );
        docInfo.addClass( myClass );
        assertEquals( myClass, docInfo.getClass( "mypkg.MyClass" ));
    }

    //---( Source-path access )---

    public void testSourcePath() throws Exception {
        File testDir = new File( BASEDIR + "/build/main/test/tmp" );
        testDir.mkdirs();
        assertTrue( "error creating testDir", testDir.isDirectory() );
        File testFile = new File( testDir, "sourcePath_Test.log" );
        testFile.createNewFile();
        assertTrue( "error creating testFile", testFile.isFile() );
        DocInfo docInfo = new DocInfo();
        docInfo.setSourcePath( "blah:" + testDir + ":blah" );
        assertNotNull( "missed an existing file",
                       docInfo.getSourceFile( "sourcePath_Test.log" ));
        assertNull( "returned a non-existant file", 
                    docInfo.getSourceFile( "noSuchFile.log" ));
    }

}

