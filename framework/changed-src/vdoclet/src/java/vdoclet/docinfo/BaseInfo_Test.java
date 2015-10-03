package vdoclet.docinfo;

import junit.framework.TestCase;

public class BaseInfo_Test extends TestCase
{
    //---( Setup )---

    public BaseInfo_Test( String name ) {
        super( name );
    }

    BaseInfo getInstance() {
        return new BaseInfo( "blah" ) {};
    }

    //---( Tests )---

    public void testAddTag() {
        BaseInfo info = getInstance();
        info.addTag( "tagname", "value" );
        assertEquals( "value", info.getTagValue( "tagname" ) );
    }

    public void testSetComment() {
        BaseInfo info = getInstance();
        info.setComment( "blah" );
        assertEquals( "blah", info.getComment() );
    }

    public void testGetTags() {
        BaseInfo info = getInstance();
        TagInfo tag = new TagInfo( "tagname", "value" );
        info.addTag( tag );
        assertTrue( info.getTags().contains( tag ));
        assertEquals( 1, info.getTags().size() );
    }

    public void testGetTag() {
        BaseInfo info = getInstance();
        info.addTag( "tagname", "one" );
        info.addTag( "thee", "two" );
        info.addTag( "tagname", "free" );
        assertEquals( 2, info.getTags( "tagname" ).size() );
        TagInfo tagname2 = (TagInfo) info.getTags( "tagname" ).get(1);
        assertEquals( "free", tagname2.getValue() );
    }

    public void testGetTagValueDefaultNull() {
        BaseInfo info = getInstance();
        assertEquals( "theDefault", 
                      info.getTagValue( "tagname", "theDefault" ));
    }

    public void testGetTagValueDefaultBlank() {
        BaseInfo info = getInstance();
        info.addTag( "tagname", "" );
        assertEquals( "", 
                      info.getTagValue( "tagname", "theDefault" ));
    }

}
