package vdoclet;

import com.sun.javadoc.*;
import java.io.*;
import java.util.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.io.VelocityWriter;
import vdoclet.docinfo.DocInfo;
import vdoclet.docinfo.JavadocBuilder;

/**
 * A doclet that processes classes using Velocity templates.
 */
public class GeneratorDoclet {

    //---( Static methods )---

    /**
     * Tell javadoc what options we recognise
     */
    public static int optionLength( String option ) {
        if (option.equals("-sourcepath")) {
            return 2;
        } else if (option.equals("-d")) {
            return 2;
        } else if (option.equals("-c")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * javadoc entry-point
     */
    public static boolean start( RootDoc root ) throws Exception {

        String sourcePath = null;
        String outputDirName = null;
        String controlTemplate = "Control.vm";
        String[][] options = root.options();
        for (int i = 0; i < options.length; i++) {
            if (options[i][0].equals( "-sourcepath" )) {
                sourcePath = options[i][1];
            } else if (options[i][0].equals( "-d" )) {
                outputDirName = options[i][1];
                System.out.println( "outputDirName=" + outputDirName );
            } else if (options[i][0].equals( "-c" )) {
                controlTemplate = options[i][1];
                System.out.println( "controlTemplate=" + controlTemplate );
            }
        }

        DocInfo docInfo = JavadocBuilder.build( root );
        docInfo.setSourcePath( sourcePath );
        
        Generator generator = new Generator( new File( outputDirName ));
        Writer outWriter = new OutputStreamWriter( System.out );
        generator.setAttribute( "docInfo", docInfo );
        generator.eval( controlTemplate, outWriter );
        
        return true;
    }
    
}
