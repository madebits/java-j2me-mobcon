package vdoclet.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Javadoc;
import org.apache.tools.ant.types.Path;

/**
 * (Deprecated) Ant task for invocation of vDoclet.  It takes the following
 * arguments:
 *
 * <ul>
 *    <li>destDir       = output directory (required)</li>
 *    <li>srcDir        = input Java source directory (required)</li>
 *    <li>classPath     = path for templates and helper-classes</li>
 *    <li>template      = name of control template (required)</li>
 *    <li>private       = boolean flag to include package/private data</li>
 * </ul>
 */
public class GeneratorTask extends Task {
    
    //---( Variables )---

    /** output directory */
    private File destDir;
    
    /** input Java source directories */
    private Path srcPath;

    /** path for templates and helper-classes*/
    private Path classPath;

    /** control template */
    private String template;

    /** private flag */
    private boolean includePrivate = false;

    //---( Property access )---

    public void setDestDir( File destDir ) {
        this.destDir = destDir;
    }

    public void setClassPath( Path classPath ) {
        if (this.classPath == null) {
            this.classPath = classPath;
        } else {
            this.classPath.append( classPath );
        }
    }

    public Path createClassPath() {
        if (this.classPath == null) {
            this.classPath = new Path( project );
        }
        return this.classPath.createPath();
    }

    public void setSrcDir( Path srcDir ) {
        if (this.srcPath == null) {
            this.srcPath = srcDir;
        } else {
            this.srcPath.append( srcDir );
        }
    }

    public Path createSrcDir() {
        if (this.srcPath == null) {
            this.srcPath = new Path( project );
        }
        return this.srcPath.createPath();
    }

    public void setTemplate( String template ) {
        this.template = template;
    }

    public void setPrivate( boolean includePrivate ) {
        this.includePrivate = includePrivate;
    }

    //---( Javadoc task management )---

    /**
     * Create a Javadoc task to delegate the work to
     */
    protected Javadoc createJavadocTask() throws BuildException {

        Javadoc javadoc = (Javadoc) project.createTask( "javadoc" );
        javadoc.setTaskName( this.getTaskName() );
        javadoc.setOwningTarget( this.getOwningTarget() );
        javadoc.setDestdir( this.destDir );
        javadoc.setSourcepath( this.srcPath );
        javadoc.setClasspath( this.classPath );
        javadoc.setVerbose( false );
        javadoc.setFailonerror( true );
        javadoc.setPrivate( includePrivate );
        javadoc.setUseExternalFile( true );
        javadoc.setPackagenames( "*.*" );

        Javadoc.DocletInfo doclet = javadoc.createDoclet();
        doclet.setName( "vdoclet.GeneratorDoclet" );
        doclet.setPath( this.classPath );

        Javadoc.DocletParam param = doclet.createParam();
        param.setName( "-c" );
        param.setValue( template );
        
        return javadoc;
    }
    
    //---( Parameter validation )---

    /** 
     * Check that the required parameters have been provided
     */
    private void checkParams() throws BuildException { 
        if( destDir == null ) complainAboutMissing( "destDir" );
        if( srcPath == null ) complainAboutMissing( "srcDir" );
        if( template == null ) complainAboutMissing( "template" );
    }

    /**
     * Complain that a required parameter is missing
     * @param paramName name of the missing parameter
     */
    private void complainAboutMissing( String paramName ) 
        throws BuildException 
    {    
        throw new BuildException( "no '" + paramName + "' provided",
                                  getLocation() );
    }

    //---( Execution )---

    /**
     * Invoke vDoclet
     */
    public void execute() throws BuildException {
        checkParams();
        createJavadocTask().execute();
    }

}
