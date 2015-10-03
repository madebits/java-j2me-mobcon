package vdoclet.ejb;

import vdoclet.docinfo.*;
import java.util.*;

/**
 * A factory for utility-objects of various types.
 */
public class EjbUtils {

    /**
     * Derive EJB information from Javadoc data
     * @param docInfo Javadoc data
     * @return an EjbBundle representing the EJBs
     */
    public static EjbBundle getEjbBundle( DocInfo docInfo ) {
        return new EjbBundle( docInfo );
    }

}
