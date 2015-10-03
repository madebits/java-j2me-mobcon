package vdoclet.docinfo;

import java.util.*;

/**
 * Info about a Tag.
 */
public class TagInfo {

    //---( Instance variables )---

    private final String name;
    private final String value;

    //---( Constructors )---

    public TagInfo( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    //---( Accessors )---

    /**
     * Get the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @return the values of the tag as elements of a List.
     */
    public List getWords() {
        if (getValue() == null) return Collections.EMPTY_LIST;
        StringTokenizer tokenizer = 
            new StringTokenizer( getValue(), " \r\n" );
        List words = new ArrayList();
        while (tokenizer.hasMoreElements()) {
            words.add(tokenizer.nextToken());
        }
        return words;
    }

    /**
     * Return the specified word 
     */
    public String getWord( int index ) {
        List words = getWords();
        if (index >= words.size()) return null;
        return (String) words.get(index);
    }
    
    /**
     * Get the value.
     */
    public String toString() {
        return this.value;
    }

}

