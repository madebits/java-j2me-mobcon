package vdoclet.docinfo;

import java.util.*;
import vdoclet.util.DelimitedList;
import vdoclet.util.DelimitedSet;

/**
 * Info about a Method.
 */
public class MethodInfo extends BaseClassElementInfo {

    //---( Instance variables )---

    private String type;
    private ArrayList params = new ArrayList();
    private TreeSet exceptions = new TreeSet();
    
    //ADDED FOR MOBCON
    private String methodBody = "";

    //---( Constructors )---

    public MethodInfo( String type, String name ) {
        super( name );
        this.type = type;
    }

    public Object clone() {
        MethodInfo clone = (MethodInfo)super.clone();
        clone.params = (ArrayList)this.params.clone();
        clone.exceptions = (TreeSet)this.exceptions.clone();
        return clone;
    }

    //---( Accessors )---
    
    public String getType() {
        return this.type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    //---( Parameters )---

    public List getParameters() {
        return new DelimitedList( this.params, ", " );
    }

    public void addParameter( ParameterInfo parameter ) {
        this.params.add( parameter );
    }

    public void addParameter( String type, String name ) {
        addParameter( new ParameterInfo( type, name ));
    }

    public ParameterInfo getParameter( int index ) {
        return (ParameterInfo) getParameters().get( index );
    }

    /**
     * Get a List of the parameter-types
     */
    public List getParameterTypes() {
        ArrayList paramTypes = new ArrayList();
        Iterator i = getParameters().iterator();
        while (i.hasNext()) {
            ParameterInfo p = (ParameterInfo) i.next();
            paramTypes.add( p.getType() );
        }
        return new DelimitedList( paramTypes, "," );
    }

    /**
     * Get a List of the parameter-names
     */
    public List getParameterNames() {
        ArrayList paramNames = new ArrayList();
        Iterator i = getParameters().iterator();
        while (i.hasNext()) {
            ParameterInfo p = (ParameterInfo) i.next();
            paramNames.add( p.getName() );
        }
        return new DelimitedList( paramNames, "," );
    }

    //---( Exceptions )---

    public Set getExceptions() {
        return new DelimitedSet( this.exceptions, ", " );
    }

    public void addException( String exception ) {
        this.exceptions.add( exception );
    }

    //ADDED FOR MOBCON
    public String getMethodBody() {
        return this.methodBody;
    }
    
    //ADDED FOR MOBCON
    public void addMethodBody(String mb) {
        this.methodBody = mb;
    }
    
}

