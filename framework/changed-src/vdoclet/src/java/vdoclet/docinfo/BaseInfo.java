package vdoclet.docinfo;

import java.util.*;

/**
 * Base class for stuff that has tags.
 */
public abstract class BaseInfo implements Cloneable {

    //---( Instance variables )---

    private String name;
    private String comment;
    private ArrayList tags = new ArrayList();

    //---( Constructors )---

    public BaseInfo( String name ) {
        if (name == null) {
            throw new IllegalArgumentException( "name cannot be null" );
        }
        this.name = name;
    }

    public Object clone() {
        try {
            BaseInfo clone = (BaseInfo)super.clone();
            clone.tags = (ArrayList)tags.clone();
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException();
        }
    }

    //---( Accessors )---

    /**
     * Get the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name
     */
    public void setName( String name ) {
        this.name = name;
    }

    //---( Documentation )---

    /**
     * Get the comment
     */
    public void setComment( String comment ) {
        this.comment = comment;
    }

    /**
     * Get the comment
     */
    public String getComment() {
        return this.comment;
    }

    //---( Tags )---

    /**
     * Add a tag. Duplicates are allowed.
     */
    public void addTag( TagInfo tag ) {
        this.tags.add( tag );
    }

    /**
     * Add a tag. Duplicates are allowed.
     */
    public void addTag( String name, String value ) {
        addTag( new TagInfo( name, value ));
    }

    public void addTags( Collection tags ) {
        Iterator i = tags.iterator();
        while (i.hasNext()) {
            addTag( (TagInfo) i.next() );
        }
    }

    /**
     * Get all tags.
     * @return List(TagInfo)
     */
    public List getTags() {
        return Collections.unmodifiableList( this.tags );
    }

    /**
     * Get all tags with the given name.
     * @return List(TagInfo)
     */
    public List getTags( String name ) {
        Iterator i = getTags().iterator();
        List tags = new ArrayList();
        while (i.hasNext()) {
            TagInfo tag = (TagInfo) i.next();
            if (tag.getName().equals( name )) {
                tags.add( tag );
            }
        }
        return Collections.unmodifiableList( tags );
    }
    
    /**
     * Get the first tag with the given name, or null if there isn't one.
     */
    public TagInfo getTag( String name ) {
        List tags = getTags( name );
        return (tags.isEmpty() ? null : (TagInfo) tags.get(0));
    }

    /**
     * Get the value of first tag with the given name.  Return null if the
     * tag is not present.
     */
    public String getTagValue( String name ) {
        TagInfo tag = getTag( name );
        return (tag == null ? null : tag.getValue());
    }
    
    /**
     * Get the value of first tag with the given name.  Return the
     * specified default value if the tag is not present.
     */
    public String getTagValue( String name, String defaultValue ) {
        String value = getTagValue( name );
        return (value == null ? defaultValue : value);
    }
    
}

