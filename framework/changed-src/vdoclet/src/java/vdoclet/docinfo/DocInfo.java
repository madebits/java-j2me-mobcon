package vdoclet.docinfo;

import java.util.*;
import java.io.*;

/**
 * Root of doc-info tree.
 */
public class DocInfo {

    //---( Instance variables )---

    private final Map classMap = new TreeMap();
    private String[] sourcePath = new String[0];
    
    //---( Accessors )---

    public void addClass( ClassInfo classInfo ) {
        classMap.put( classInfo.getName(), classInfo );
        classInfo.setContainingDocInfo( this );
    }

    public Collection getClasses() {
        return classMap.values();
    }

    public ClassInfo getClass( String name ) {
        return (ClassInfo) classMap.get( name );
    }

    public ClassInfo makeClass( String name ) {
        ClassInfo clazz = new ClassInfo( name );
        addClass( clazz );
        return clazz;
    }

    //---( Source-path access )---

    /**
     * Set the source-path
     */
    public void setSourcePath( String[] sourcePath ) {
        this.sourcePath = sourcePath;
    }
    
    /**
     * Set the source-path
     */
    public void setSourcePath( String path ) {
        StringTokenizer tokens = 
            new StringTokenizer( path, File.pathSeparator );
        ArrayList sourceDirList = new ArrayList();
        while (tokens.hasMoreTokens()) {
            sourceDirList.add( tokens.nextToken() );
        }
        String[] newSourcePath = new String[sourceDirList.size()];
        setSourcePath( (String[]) sourceDirList.toArray(newSourcePath) );
    }

    /**
     * Find a named file on the source-path.  Return null if the file does
     * not exist.
     */
    public File getSourceFile( String name ) {
        for (int i = 0; i < sourcePath.length; i++) {
            File file = new File( sourcePath[i], name );
            if (file.exists()) return file;
        }
        return null;
    }

}
