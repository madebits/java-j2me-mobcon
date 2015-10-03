package vdoclet.util;

public class StringUtils {
    
    /**
     * Capitalise a String
     */
    public static String capitalise( String s ) {
        if (s == null || s.length() == 0) return s;
        return (Character.toUpperCase( s.charAt(0) ) +
                s.substring( 1 ));
    }

    /**
     * Strip a prefix from a name
     * @param prefix the prefix to expect
     * @param name the prefixed name
     * @return the name minus the prefix, with the first character downcased
     */
    public static String stripPrefix( String prefix, String name ) {
        if (! name.startsWith( prefix )) {
            throw new IllegalArgumentException( "'" + name +
                                                "' doesn't start with '" +
                                                prefix + "'" );
        }
        int firstChar = prefix.length();
        return ( Character.toLowerCase( name.charAt(firstChar) ) +
                 name.substring(firstChar+1) );
    }

}
