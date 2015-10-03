package vdoclet;

import java.io.*;

public class GeneratorException extends Exception {

    //---( Instance variables )---

    private Throwable cause = null;

    //---( Constructors )---

    /**
     * Default constructor.
     */
    public GeneratorException() {
    }

    /**
     * @param msg an error message
     */
    public GeneratorException( String msg ) {
        super( msg );
    }

    /**
     * @param msg an error message
     * @param cause a wrapped exception
     */
    public GeneratorException( String msg, Throwable cause ) {
        super( msg );
        this.cause = cause;
    }

    /**
     * @param cause a wrapped exception
     */
    public GeneratorException( Throwable cause ) {
        this.cause = cause;
    }
    
    /**
     * Get the causing exception, if any.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Print the stack trace.
     */
    public void printStackTrace() {
        printStackTrace( System.err );
    }
    
    /**
     * Print the stack trace.
     *
     * @param ps a PrintStream to print the stack trace to.
     */
    public void printStackTrace( PrintStream ps ) {
        super.printStackTrace(ps);
        if (cause != null) {
            ps.println("Cause: ");
            cause.printStackTrace(ps);
        }
    }

    /**
     * Print the stack trace.
     *
     * @param pw a PrintWriter to print the stack trace to.
     */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (cause != null) {
            pw.println("Cause: ");
            cause.printStackTrace(pw);
        }
    }


}
