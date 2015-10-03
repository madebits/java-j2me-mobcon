package vdoclet.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Check that XML documents are valid with a SAX validating parser.
 * 
 * <p>The following nested elements are supported:</p>
 * <ul>
 *    <li>fileset       = files to check</li>
 *    <li>dtd           = register a DTD</li>
 * </ul>
 */
public class XmlCheckTask extends Task {

    //---( Instance variables )---

    /** sets of files to be validated */
    private List filesets = new ArrayList(); // sets of file to be validated
    
    /** list of configured DTD locations */
    private Map dtdCatalog = new HashMap();

    /** parser used to validate files */
    private SAXParser parser = null;

    //---( Parameter handling )---

    /**
     * Add a set of files to be checked
     */
    public void addFileset( FileSet set ) {
        filesets.add(set);
    }

    //---( DTD resolution )---

    /**
     * A configured DTD location
     */
    public static class DTDLocation {

        private String publicId = null;
        private String location = null;

        public void setPublicId( String publicId ) {
            this.publicId = publicId;
        }

        public void setLocation( String location ) {
            this.location = location;
        }

        public String getPublicId() {
            return publicId;
        }

        public URL getLocation() throws MalformedURLException {
            File dtdFile = new File( location );
            if (dtdFile.exists()) {
                return dtdFile.toURL();
            }
            URL dtdResource = this.getClass().getResource( location );
            if (dtdResource != null) {
                return dtdResource;
            }
            return new URL( location );
        }

    }

    /**
     * Add a DTD location
     */
    public void addConfiguredDTD( DTDLocation dtdLocation ) {
        try {
            log( "adding DTD: id='" + dtdLocation.getPublicId() +
                 "'; location='" + dtdLocation.getLocation() + "'",
                 Project.MSG_VERBOSE ); 
            dtdCatalog.put( dtdLocation.getPublicId(),
                            dtdLocation.getLocation() );
        } catch (MalformedURLException e) {
            // ignore
        }
    }

    //---( Parse handler )---

    /**
     * A parse-handler that reports errors via Ant logging
     */
    protected class CheckHandler extends DefaultHandler {

        /**
         * Resolve an external entity, using the local catalog if possible
         */
        public InputSource resolveEntity( String publicId, 
                                          String systemId )
            throws SAXException
        {
            if (dtdCatalog.containsKey( publicId )) {
                URL dtdURL = (URL) dtdCatalog.get( publicId );
                try {
                    return new InputSource( dtdURL.openStream() );
                } catch (IOException e) {
                    throw new SAXException( "can't open " + dtdURL, e );
                }
            }
            return null;
        }
        
        /**
         * Handle a well-formedness error
         */
        public void fatalError( SAXParseException e )
            throws SAXParseException
        {
            throw(e);
        }
        
        /**
         * Handle a validation error
         */
        public void error( SAXParseException e )
            throws SAXParseException 
        {
            throw(e);
        }
         
    }

    //---( Execution )---

    /**
     * Execute the task
     */
    public void execute() throws BuildException {
        checkParams();
        initParser();
        validateFiles();
    }
    
    /**
     * Check that sufficient information has been provided
     */
    private void checkParams() throws BuildException { 
        if (filesets.isEmpty() ) {
            throw new BuildException( "No files to check" );
        }
    }

    /**
     * Initialise parser
     */
    private void initParser() throws BuildException {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating( true );
            this.parser = parserFactory.newSAXParser();
        } catch (SAXException e) {
            throw new BuildException( e );
        } catch (ParserConfigurationException e) {
            throw new BuildException( e );
        }
    }

    /**
     * Parse a file
     */
    protected void validateFile( File file ) throws BuildException {
        if (! (file.isFile() && file.canRead())) { 
            throw new BuildException( "can't read file '" + file + "'" );
        }
        log( "validating: " + file, Project.MSG_VERBOSE );
        CheckHandler fileChecker = new CheckHandler();
        try {
            this.parser.parse( file, fileChecker );
        } catch (SAXParseException e) {
            log( getMessage(e), Project.MSG_ERR );
            throw new BuildException( "failed to validate: " + file, e );
        } catch (SAXException e) {
            throw new BuildException( "failed to validate: " + file, e );
        } catch (IOException e) {
            throw new BuildException( e );
        }
    }

    /**
     * Parse all the files
     */
    protected void validateFiles() throws BuildException {
        for (Iterator fi = filesets.iterator(); fi.hasNext();) {
            FileSet fileset = (FileSet) fi.next();
            String[] files = fileset.getDirectoryScanner(project).getIncludedFiles();
            for (int j=0; j < files.length; j++) {
                File file = new File( fileset.getDir(project), files[j] );
                validateFile( file );
            }
        }
    }

    /**
     * Extract a message including location info from a
     * SAXParseException
     */
    private String getMessage( SAXParseException e ) {
        StringBuffer msg = new StringBuffer();
        if (e.getSystemId() != null &&
            e.getSystemId().startsWith("file:"))
        {
            msg.append( e.getSystemId().substring(5) );
            msg.append( ':' );
            if (e.getLineNumber() != -1) {
                msg.append( e.getLineNumber() );
                msg.append( ':' );
            }
            if (e.getColumnNumber() != -1) {
                msg.append( e.getColumnNumber() );
                msg.append( ':' );
            }
            msg.append( ' ' );
        }
        msg.append( e.getMessage() );
        return msg.toString();
    }
    
}
