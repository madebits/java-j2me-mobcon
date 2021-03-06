package com.thoughtworks.qdox.model;

import java.io.Serializable;

public class Type implements Comparable, Serializable {

    public static final Type[] EMPTY_ARRAY = new Type[0];

    private String name;
    private JavaSource parentSource;
    private String fullName;
    private int dimensions;

    public Type(String name, int dimensions, JavaSource parentSource) {
        this.name = name;
        this.dimensions = dimensions;
        this.parentSource = parentSource;
    }

    public Type(String fullName, int dimensions) {
        this.fullName = fullName;
        this.dimensions = dimensions;
    }

    public Type(String fullName) {
        this(fullName, 0);
    }

    public JavaSource getParentSource() {
        return parentSource;
    }

    public void setParentSource(JavaSource javaSource) {
        parentSource = javaSource;
    }

    public String getValue() {
        return isResolved() ? fullName : name;
    }

    public boolean isResolved() {
        if (fullName == null && parentSource != null) {
            fullName = parentSource.resolveType(name);
        }
        return (fullName != null);
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
        if (!(o instanceof Type))
            return 0;

        return getValue().compareTo(((Type) o).getValue());
    }

    public boolean isArray() {
        return dimensions > 0;
    }

    public int getDimensions() {
        return dimensions;
    }

    public String toString() {
        if (dimensions == 0) return getValue();
        StringBuffer buff = new StringBuffer(getValue());
        for (int i = 0; i < dimensions; i++) buff.append("[]");
        String result = buff.toString();
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        Type t = (Type) obj;
        return getValue().equals(t.getValue()) && t.getDimensions() == getDimensions();
    }

    public int hashCode() {
        return getValue().hashCode();
    }

    public JavaClass getJavaClass() {
        if (getParentSource() == null) {
            return null;
        }
        if (getParentSource().getClassLibrary() == null) {
            return null;
        }
        return getParentSource().getClassLibrary().getClassByName(getValue());
    }

    /**
     * @since 1.3
     */ 
    public boolean isA(Type type) {
        if (this.equals(type)) {
            return true;
        } else {
            // ask our interfaces
            JavaClass javaClass = getJavaClass();
            if (javaClass != null) {
                Type[] implementz = javaClass.getImplements();
                for (int i = 0; i < implementz.length; i++) {
                    if (implementz[i].isA(type)) {
                        return true;
                    }
                }

                // ask our superclass
                Type supertype = javaClass.getSuperClass();
                if (supertype != null) {
                    if (supertype.isA(type)) {
                        return true;
                    }
                }
            }
        }
        // We'we walked up the hierarchy and found nothing.
        return false;
    }
}
