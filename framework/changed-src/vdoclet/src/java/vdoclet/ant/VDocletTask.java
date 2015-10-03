package vdoclet.ant;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Ant task for invocation of vDoclet.  It takes the following arguments:
 *
 * <ul>
 *    <li>srcPath       = input Java source-path</li>
 *    <li>destDir       = output directory (required)</li>
 *    <li>template      = name of control template (required)</li>
 *    <li>classPath     = path for templates and helper-classes</li>
 * </ul>
 *
 * and supports the following nested elements:
 *
 * <ul>
 *    <li>fileset       = source-files
 *    <li>classPath     = path for templates and helper-classes</li>
 * </ul>
 *
 */
public class VDocletTask extends Task {
    
    //---( Variables )---

    /** output directory */
    private File destDir;
    
    /** control template */
    private String template;

    /** input Java files */
    private Path inputJavaFiles;

    /** search-path for resources */
    private Path sourcePath;
    
    /** path for templates and helper-classes */
    private Path classPath;

    //---( Constructor )---

    public void setProject(Project project) {
        super.setProject(project);
        inputJavaFiles = new Path(project);
        sourcePath = new Path(project);
        classPath = new Path(project);
    }

    //---( Property access )---

    /**
     * srcPath attribute
     */
    public void setSrcPath( Path srcPath ) {
        this.inputJavaFiles.append( srcPath );
        this.sourcePath.append( srcPath );
    }

    /**
     * Alias for srcPath
     */
    public void setSrcDir( Path srcDir ) {
        setSrcPath( srcDir );
    }
    
    /**
     * Nested &lt;fileset&gt; element
     */
    public void addConfiguredFileSet( FileSet fileSet ) {
        this.inputJavaFiles.addFileset( fileSet );
        File dir = fileSet.getDir( getProject() );
        this.sourcePath.createPathElement().setLocation( dir );
    }

    /**
     * destDir attribute
     */
    public void setDestDir( File destDir ) {
        this.destDir = destDir;
    }

    /**
     * template attribute
     */
    public void setTemplate( String template ) {
        this.template = template;
    }

    /**
     * classpath attribute
     */
    public void setClassPath( Path classPath ) {
        classPath.append( classPath );
    }

    /**
     * Nested &lt;classpath&gt; element
     */
    public Path createClassPath() {
        return classPath.createPath();
    }

    //---( Parameter validation )---

    /** 
     * Check that the required parameters have been provided
     */
    private void checkParams() throws BuildException { 
        if( destDir == null ) complainAboutMissing( "destDir" );
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

    Path getExecutionClassPath() {  
        Path execPath = new Path(getProject());
        execPath.addExisting( classPath );
        try {
            AntClassLoader classLoader = 
                (AntClassLoader) getClass().getClassLoader();
            String taskPath = classLoader.getClasspath();
            log( "taskPath: " + taskPath, Project.MSG_DEBUG );
            execPath.createPathElement().setPath( taskPath );
        } catch (ClassCastException e) {
            log( "task ClassLoader is not an AntClassLoader",
                 Project.MSG_ERR );
        }
        return execPath.concatSystemClasspath();
    }

    /**
     * Invoke vDoclet
     */
    public void execute() throws BuildException {

        checkParams();

        Java javaTask = (Java) project.createTask( "java" );
        javaTask.setTaskName( this.getTaskName() );
        javaTask.setOwningTarget( this.getOwningTarget() );
        javaTask.setFork( true );
        javaTask.setClassname( "vdoclet.Main" );
        javaTask.createArg().setValue("-d");
        javaTask.createArg().setFile(destDir);
        javaTask.createArg().setValue("-s");
        javaTask.createArg().setPath(sourcePath);
        javaTask.createArg().setValue("-t");
        javaTask.createArg().setValue(template);
        javaTask.setClasspath( getExecutionClassPath() );

        String[] sources = inputJavaFiles.list();
        for (int i = 0; i < sources.length; i++) {
            javaTask.createArg().setValue(sources[i]);
        }

        javaTask.execute();
        
    }
    
}
