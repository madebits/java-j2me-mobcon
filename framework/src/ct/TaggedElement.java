package mobcon.ct;

import java.util.Vector;

public abstract class TaggedElement{
    public static final String invisible = "TE.invisible";

    protected Vector tags;

    public TaggedElement()
    {
        tags = new Vector();
    }

    public Vector getTags()
    {
        return this.tags;
    }

    public void setTag(String in)
    {
        if(!this.getTag(in)) this.tags.add(in);
    }

    public boolean getTag(String in)
    {
        return this.tags.contains(in);
    }

    public void removeTag(String in)
    {
        if(this.getTag(in)) this.tags.remove(in);
    }

    public void setVisible()
    {
        this.removeTag(invisible);
    }

    public void setInvisible()
    {
        this.setTag(invisible);
    }

    public boolean isVisible()
    {
        return (!(this.getTag(invisible)));
    }

    public String toString()
    {
        if(this.isVisible()) return this.stringRepresentation();
        else return "";
    }

    public abstract String stringRepresentation();
}