package vdoclet;

import com.thoughtworks.qdox.model.JavaSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import vdoclet.docinfo.DocInfo;
import vdoclet.docinfo.QDoxBuilder;

/**
 * Command-line entry-point to vDoclet
 */
public class Main {

    //---( State )---
    
    /** output directory */
    File destDir;
    
    /** control template */
    String template;
    
    /** input files/directories */
    String[] sourceFiles;

    /** input path */
    String sourcePath;

    /** verbose flag */
    boolean verbose = false;

    /** docinfo */
    DocInfo docInfo;

    //---( Execution )---
    
    public static void main( String[] args ) throws Exception {
        new Main().execute(args);
    }
    
    public void execute( String[] args ) throws Exception {
        parseCommandLine(args);
        validateCommandLine();
        parseSources();
        generate();
    }
    
    void log( String msg ) {
        if (verbose) System.err.println( msg );
    }

    /**
     * Parse the command-line
     */
    void parseCommandLine( String[] args ) throws Exception {
        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            if (args[i].equals("-d")) {
                destDir = new File( args[++i] );
            } else if (args[i].equals("-s")) {
                sourcePath = args[++i];
            } else if (args[i].equals("-t")) {
                template = args[++i];
            } else if (args[i].equals("-v")) {
                verbose = true;
            } else {
                throw new Exception( "Illegal command-line arg: " 
                                     + args[i] );
            }
            i++;
        }
        int nFiles = args.length - i;
        sourceFiles = new String[nFiles];
        System.arraycopy( args, i, sourceFiles, 0, nFiles );
    }

    /**
     * Check that we have all the required args
     */
    void validateCommandLine() throws Exception {
        if (destDir == null) {
            throw new Exception( "missing -d argument" );
        }
        if (template == null) {
            throw new Exception( "missing -t argument" );
        }
        if (sourceFiles.length == 0) {
            throw new Exception( "no source-files provided" );
        }
    }

    /**
     * Parse the source-files
     */
    void parseSources() throws IOException {    
        com.thoughtworks.qdox.JavaDocBuilder builder = 
            new com.thoughtworks.qdox.JavaDocBuilder();
        ArrayList sourceDirList = new ArrayList();
        for (int i = 0; i < sourceFiles.length; i++) {
            log( "reading " + sourceFiles[i] );
            File f = new File(sourceFiles[i]);
            if (f.isDirectory()) {
                builder.addSourceTree( f );
                sourceDirList.add( f );
            } else {
                builder.addSource( new FileReader(f) );
            }
        }
 
        JavaSource[] javaSources = builder.getSources();
        log( javaSources.length + " source-files" );
        docInfo = QDoxBuilder.build( javaSources );

        if (sourcePath != null) {
            docInfo.setSourcePath( sourcePath );
        } else {
            String[] srcPath = 
                (String[]) sourceDirList.toArray( new String[0] );
            docInfo.setSourcePath( srcPath );
        }
    }
    
    /**
     * Kick-start generation
     */
    void generate() throws Exception {
        Generator generator = new Generator( destDir );
        generator.setAttribute( "docInfo", docInfo );
        generator.eval( template, 
                        new OutputStreamWriter(System.out) );
    }

}

