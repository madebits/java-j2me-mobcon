package vdoclet.beaninfo;

import java.util.*;
import vdoclet.docinfo.*;
import vdoclet.util.*;

/**
 * Info about a JavaBean property
 */
public class PropertyInfo extends FieldInfo {

    //---( Instance variables )---

    private final MethodInfo _getter;
    private final MethodInfo _setter;

    //---( Constructors )---
    
    /**
     * Construct a new PropertyInfo
     */
    public PropertyInfo( String type, String name,
                         MethodInfo getter, MethodInfo setter )
    { 
        super( type, name );
        _getter = getter;
        _setter = setter;
    }

    //---( Utilities for construction )---

    /**
     * Create a PropertyInfo based on field details
     * @param type data-type
     * @param name field-name
     * @return a PropertyInfo
     */
    public static PropertyInfo fromField( String type, String name ) {
        return new PropertyInfo( type, name, 
                                 makeGetter( type, name ),
                                 makeSetter( type, name ) );
    }

    private static MethodInfo makeSetter( String type, String name ) {
        MethodInfo setter = 
            new MethodInfo( "void",
                            "set" + StringUtils.capitalise(name) );
        setter.addParameter( type, name );
        return setter;
    }

    private static MethodInfo makeGetter( String type, String name ) {
        String prefix = (type.equals("boolean") ? "is" : "get");
        return new MethodInfo( type,
                               prefix + StringUtils.capitalise(name) );
    }

    /**
     * Create a PropertyInfo based on a getter-method
     * @param method field getter-method
     * @return a PropertyInfo
     */
    public static PropertyInfo fromGetter( MethodInfo getter ) 
    {
        String name;
        if (getter.getName().startsWith("get")) {
            name = StringUtils.stripPrefix( "get", getter.getName() );
        } else if (getter.getName().startsWith("is")) {
            name = StringUtils.stripPrefix( "is", getter.getName() );
        } else {
            throw new IllegalArgumentException( "Can't derive a field from "
                                                + getter.getName() + "(): " +
                                                "bad name" );
        }
        if (! getter.getParameters().isEmpty()) {
            throw new IllegalArgumentException( "Can't derive a field from "
                                                + getter.getName() + "(): " +
                                                "getters cannot have parameters" );
        }
        return new PropertyInfo( getter.getType(), name, 
                                 getter,
                                 makeSetter( getter.getType(), name ));
    }

    //---( Accessors )---

    /**
     * Get the getter-method
     */
    public MethodInfo getGetter() {
        return _getter;
    }

    /**
     * Get the setter-method
     */
    public MethodInfo getSetter() {
        return _setter;
    }
    
    //---( Tags )---

    /**
     * Get all tags with the given name.
     * @return List(TagInfo)
     */
    public List getTags( String name ) {
        List tags = new ArrayList();
        tags.addAll( super.getTags(name) );
        if (getGetter() != null) {
            tags.addAll( getGetter().getTags(name) );
        }
        return Collections.unmodifiableList( tags );
    }

}

