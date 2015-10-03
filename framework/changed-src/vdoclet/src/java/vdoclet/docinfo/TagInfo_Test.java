package vdoclet.docinfo;

import junit.framework.TestCase;
import java.util.*;

public class TagInfo_Test extends TestCase
{
    //---( Setup )---

    public TagInfo_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    public void testCreate() {
        TagInfo tagInfo = new TagInfo( "@tag", "value" );
        assertEquals( "@tag", tagInfo.getName() );
        assertEquals( "value", tagInfo.getValue() );
    }

    public void testToString() {
        TagInfo tagInfo = new TagInfo( "@tag", "value" );
        assertEquals( "value", tagInfo.toString() );
    }

    public void testGetWords() {
        TagInfo tagInfo = new TagInfo( "@wl-persistence-use", "WebLogic_CMP_RDBMS 6.0" );
        List words = tagInfo.getWords();
        assertEquals( "WebLogic_CMP_RDBMS", words.get(0) );
        assertEquals( "6.0", words.get(1) );
    }

    public void testGetNoWords() {
        TagInfo tagInfo = new TagInfo( "@horse", null );
        assertTrue( tagInfo.getWords().isEmpty() );
    }

    public void testGetWordsIndex() {
        TagInfo tagInfo = new TagInfo( "@use", "one two three" );
        assertEquals( "one", tagInfo.getWord(0) );
        assertEquals( "two", tagInfo.getWord(1) );
        assertEquals( "three", tagInfo.getWord(2) );
        assertNull( tagInfo.getWord(3) );
    }

    public void testGetWordsWhitespace() {
        TagInfo tagInfo = new TagInfo( "@foo", "  one two  \r\n    three  " );
        assertEquals( "one", tagInfo.getWord(0) );
        assertEquals( "two", tagInfo.getWord(1) );
        assertEquals( "three", tagInfo.getWord(2) );
        assertEquals( 3, tagInfo.getWords().size() );
    }

}
