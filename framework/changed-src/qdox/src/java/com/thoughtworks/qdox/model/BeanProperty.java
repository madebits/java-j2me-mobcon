package com.thoughtworks.qdox.model;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class BeanProperty {
    private final String name;
    private JavaMethod accessor;
    private JavaMethod mutator;

    public BeanProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JavaMethod getAccessor() {
        return accessor;
    }

    public void setAccessor(JavaMethod accessor) {
        this.accessor = accessor;
    }

    public JavaMethod getMutator() {
        return mutator;
    }

    public void setMutator(JavaMethod mutator) {
        this.mutator = mutator;
    }
}
