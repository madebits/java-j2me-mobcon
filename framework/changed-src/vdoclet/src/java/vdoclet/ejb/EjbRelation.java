package vdoclet.ejb;

import vdoclet.docinfo.*;
import java.util.*;

/**
 * A relationship between two EJBs
 */
public class EjbRelation {
    
    //---( Instance variables )---

    private EjbRelationRole[] _roles = new EjbRelationRole[2];
    
    //---( Constructors )---

    /**
     * Construct a new EjbRelation.
     *
     * @param roleA first role
     * @param roleB second role
     */
    public EjbRelation( EjbRelationRole roleA, EjbRelationRole roleB ) {
        _roles[0] = roleA;
        _roles[1] = roleB;
        roleA.setTarget( roleB );
        roleB.setTarget( roleA );
    }

    //---( Naming )---
    
    public static String makeName( String nameA, String nameB ) {
        if (nameA.compareTo(nameB) > 0) {
            return (nameB + '-' + nameA);
        } else {
            return (nameA + '-' + nameB);
        }
    }

    /**
     * Get the canonical name of this relationship
     */
    public String getName() { 
        return makeName( _roles[0].getName(), _roles[1].getName() );
    }
    
    //---( Roles )---

    /**
     * Get the roles
     */
    public Collection getRoles() {
        return Arrays.asList( _roles );
    }
    
    /**
     * Find a role by name
     * @param name role-name
     */
    public EjbRelationRole getRole( String name ) {
        if (name.equals( _roles[0].getName() )) {
            return _roles[0];
        } else if (name.equals( _roles[1].getName() )) {
            return _roles[1];
        } else {
            return null;
        }
    }

}

