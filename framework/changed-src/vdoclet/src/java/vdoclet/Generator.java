package vdoclet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;

/**
 * A file-generator using Velocity
 */
public class Generator {

    //---( Constants )---

    private static final Properties defaultVelocityProps
        = loadProperties( "Generator-velocity.properties" );
    
    //---( Static methods )---

    protected static Properties loadProperties( String resourceName ) {
        Properties props = new Properties();
        try {
            InputStream inputStream = 
                Generator.class.getResourceAsStream( resourceName );
            if (inputStream != null) {
                props.load( inputStream );
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException( "can't load " + resourceName );
        }
        return props;
    }

    //---( Instance variables )---

    /** template engine */
    private VelocityEngine _engine;

    /** output dir */
    private File _outputDir;

    /** evaluation context */
    private Context _context;

    //---( Constructor )---

    /**
     * Construct a new generator. 
     *
     * @param outputDir base-directory for output
     */
    public Generator( File outputDir ) 
        throws GeneratorException
    {
        initVelocityEngine();
        _outputDir = outputDir;
        _context = new VelocityContext();
        setAttribute( "vdoclet", this );
    }
    
    //---( VelocityEngine management )---
    
    /**
     * Create a VelocityEngine 
     */
    private void initVelocityEngine() 
        throws GeneratorException 
    {
        try {
            _engine = new VelocityEngine();
            _engine.init( defaultVelocityProps );
        } catch (Exception e) {
            throw new GeneratorException(e);
        }
    }
    
    /**
     * Get the VelocityEngine used by this generator
     */
    public VelocityEngine getEngine() {
        return this._engine;
    }

    //---( Output-file management )---
    
    /**
     * Get the output directory
     */
    public File getOutputDir() {
        return _outputDir;
    }

    /**
     * Get a Writer to the specified File
     * @param file a File
     */
    protected FileWriter getFileWriter( String fileName ) throws IOException { 
        File file = new File( getOutputDir(), fileName );
        file.getParentFile().mkdirs();
        return new FileWriter( file );
    }

    //---( Context management )---
    
    /**
     * Get the evaluation-context used by this generator
     */
    public Context getContext() {
        return _context;
    }

    /**
     * Add something to the Generator's evaluation-context
     */
    public void setAttribute( String key, Object value ) {
        _context.put( key, value );
    }

    //---( Generate something )---

    /**
     * Evaluate a template.
     * @param templateName the name of the template
     * @param writer output destination
     * @param context merge context
     */
    void merge( String templateName, Writer writer, Context context )
        throws IOException, GeneratorException
    {
        try {
            Template template = getEngine().getTemplate( templateName );
            template.merge( context, writer );
            writer.flush();
        } catch (IOException e) {
            throw e;
        } catch (GeneratorException e) {
            throw e;
        } catch (MethodInvocationException e) {
            Throwable cause = e.getWrappedThrowable();
            if (cause instanceof GeneratorException) {
                throw (GeneratorException) cause;
            }
            if (cause == null) {
                cause = e;
            }
            throw new GeneratorException( "error invoking $" + e.getReferenceName() + 
                                          "." + e.getMethodName() + "() in \"" +
                                          templateName + "\"", 
                                          cause );
        } catch (Exception e) {
            throw new GeneratorException( "error parsing \"" + templateName + "\"", 
                                          e );
        }
    }

    /**
     * Evaluate a Velocity template.
     * @param templateName name of the template
     * @param writer output destination
     */
    public void eval( String templateName, Writer writer )
        throws IOException, GeneratorException
    {
        merge( templateName, writer, getContext() );
    }

    /**
     * Evaluate a Velocity template.
     * @param templateName name of the template
     * @param fileName name of output file
     */
    public void eval( String templateName, String fileName )
        throws IOException, GeneratorException
    {
        FileWriter writer = getFileWriter(fileName);
        eval( templateName, writer );
        writer.close();
    }

    //---( Tool creation )---

    /**
     * Create an instance of the named class.
     * @param className the name of the class to instantiate
     */
    public Object makeBean( String className ) throws Exception {
        return Class.forName( className ).newInstance();
    }
    
}
