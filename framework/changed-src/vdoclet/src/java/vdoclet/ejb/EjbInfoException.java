package vdoclet.ejb;

/**
 * Thrown to indicate that the input data is bad
 */
public class EjbInfoException extends RuntimeException {
    
    //---( Constructors )---

    /**
     * Construct a new EjbInfoException.
     * @param msg the error-message
     * @param className the name of the class that contains the error
     */
    public EjbInfoException( String msg ) {
        super( msg );
    }

}
