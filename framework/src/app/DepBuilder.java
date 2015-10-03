package mobcon.app;

import java.util.Vector;
import java.util.TreeSet;
import java.util.Iterator;
import java.lang.Exception;
import java.io.File;
import java.io.FileWriter;


public class DepBuilder
{
    private static boolean debug = false;

    Vector depGroups;
    Vector depElements;

    public DepBuilder()
    {
        depGroups = new Vector();
        depElements = new Vector();
    }

    //removes a DepGroup
    public void removeGroup(DepGroup tmpGroup)
    {
        depGroups.remove(tmpGroup);

        DepElement de;
        if(tmpGroup.getElements().size() > 0)
        {
            for (Iterator iter = tmpGroup.getElements().iterator(); iter.hasNext() ;)
            {
                de = (DepElement)iter.next();
                this.removeElement(de);
            }
        }

    }

    public Vector getGroups()
    {
        return depGroups;
    }

    private void addElement(DepElement tmpElement)
    {
        depElements.add(0, tmpElement);
    }

    private void removeElement(DepElement tmpElement)
    {
        depElements.remove(tmpElement);
    }

    public Vector getElements()
    {
        return depElements;
    }

    //Returns the position of the group in the list in which the element is, -1 if nowhere
    public int getGroupPos(String elName)
    {
        DepGroup dg;

        if(depGroups.size() > 0)
        {
            for (Iterator iter = depGroups.iterator(); iter.hasNext() ;)
            {
                dg = (DepGroup)iter.next();
                if(dg.contains(elName)) return depGroups.indexOf(dg);
            }
        }

        return -1;
    }

    //Returns the depElement if its allready inserted, when not null
    public DepElement getElement(String elName)
    {
        DepElement tmpElement;

        if(depElements.size() > 0)
        {
            for (Iterator iter = depElements.iterator(); iter.hasNext() ;)
            {
                tmpElement = ((DepElement)iter.next());
                if((tmpElement.getName()).equals(elName)) return tmpElement;
            }
        }
        return null;
    }

    //Only use if there is only one element per Group
    public void insertGroup(DepGroup tmpGroup)
    {
        DepElement tmpElement = tmpGroup.getFirstElement();

        depGroups.add(tmpGroup);
    }

    //Only use if there is only one element per Group
    public void insertElement(DepElement tmpElement)
    {
        DepGroup tmpGroup = new DepGroup();
        tmpGroup.addElement(tmpElement);

        this.insertGroup(tmpGroup);
    }

    // Only use if there is only one element per Group
    // Brings the elements in the right order
    public void orderElements()
    {
        int i, j, k;
        DepGroup dg1;
        DepGroup dg2;

        // Building the full dependencies
        for (k = 0; k < depGroups.size(); k++)
        {
            for (i = 0; i < depGroups.size(); i++)
            {
                dg1 = (DepGroup)depGroups.get(i);

                for (j = 0; j < depGroups.size(); j++)
                {
                    dg2 = (DepGroup)depGroups.get(j);

                    if(dg1.inUB(dg2.getFirstElement()))
                    {
                        dg2.addUA(dg1.getFirstElement());
                    }
                    if(dg1.inUA(dg2.getFirstElement()))
                    {
                        dg2.addUB(dg1.getFirstElement());
                    }
                }
            }
        }

        if(debug) System.out.println(this.getClass().getName()+": After full dependencies "+this.toString());

        Vector tmpGroups = (Vector)depGroups.clone();
        depGroups.clear();

        for (Iterator iter = tmpGroups.iterator(); iter.hasNext() ;)
        {
            dg1 = (DepGroup)iter.next();
            this.orderdInsert(dg1);
        }

    }

    public void orderdInsert(DepGroup tmpGroup)
    {

        DepElement tmpElement = tmpGroup.getFirstElement();

        if(depElements.contains(tmpElement))
        {
            if(debug) System.out.println(this.getClass().getName()+": Allready inserted");
            return;
        }

        if(debug) System.out.println(this.getClass().getName()+": Inserting "+tmpGroup);

        // First possible position to insert element (according to its UB/UA-Lists)
        int firstPos = 0;
        int i;
        DepGroup dg;
        if(tmpGroup.getUB().size() > 0)
        {
            for (i = 0; i < depGroups.size(); i++)
            {
                dg = (DepGroup)depGroups.get(i);
                //If in UB, use after element i
                if(tmpGroup.inUB(dg.getFirstElement()))
                {
                    firstPos = i+1;
                }
            }
        }

        depGroups.add(firstPos, tmpGroup);
        this.addElement(tmpElement);
    }

    //Groups elements together, which can be processed in paralell
    public void groupElements()
    {
        System.out.println();
        System.out.println(this.getClass().getName()+": GROUPING ELEMENTS...");

        DepGroup dg;
        DepGroup dg2;

        if(depGroups.size() > 0)
        {
            for (Iterator iter = depGroups.iterator(); iter.hasNext() ;)
            {
                dg = (DepGroup)iter.next();

                for (Iterator iter2 = depGroups.iterator(); iter2.hasNext() ;)
                {
                    dg2 = (DepGroup)iter2.next();

                    //Merges if dg and dg2 are friendly and if they are not the same Groups
                    //and if the moving of the group dg2 into dg doesnt hurt any dependencies
                    if(dg.isFriendlyGroup(dg2) & !dg.getElements().equals(dg2.getElements()) & this.canMoveGroupTo(dg2, dg))
                    {
                        if(debug) System.out.println(this.getClass().getName()+": Grouping "+dg);
                        if(debug) System.out.println(this.getClass().getName()+": with "+dg2);
                        dg.mergeGroup(dg2);

                        iter2.remove();

                        //To Fix
                        iter = depGroups.iterator();
                    }
                }

            }
        }

    }

    //Returns false if dg cant be moved to the position of moveToDG, cause of dependencies
    private boolean canMoveGroupTo(DepGroup dg, DepGroup movetToDG)
    {
        int i;
        int pos = depGroups.indexOf(dg);
        int moveTo = depGroups.indexOf(movetToDG);

        DepGroup dg2;
        DepElement de;

        //Group moves down
        if(moveTo < pos)
        {
            //Checking for all the groups below dg if an element is in the UB-List of dg
            for (i = pos-1; i > moveTo; i--)
            {
                dg2 = (DepGroup)depGroups.get(i);

                if(dg2.getElements().size() > 0)
                {
                    for (Iterator iter = dg2.getElements().iterator(); iter.hasNext() ;)
                    {
                        de = (DepElement)iter.next();
                        if(dg.inUB(de.getName())) return false;
                    }
                }
            }
        //Group moves up
        } else {
            //Checking for all the groups over dg if an element is in the UA-List of dg
            for (i = pos+1; i < moveTo; i++)
            {
                dg2 = (DepGroup)depGroups.get(i);

                if(dg2.getElements().size() > 0)
                {
                    for (Iterator iter = dg2.getElements().iterator(); iter.hasNext() ;)
                    {
                        de = (DepElement)iter.next();
                        if(dg.inUA(de.getName())) return false;
                    }
                }

            }
        }
        return true;

    }

    //Checks if all dependencies are right
    public boolean checkDepend()
    {
        int i,j;
        boolean returnValue = true;

        DepGroup dg,dg2;
        DepElement de;

        if(depGroups.size() > 0)
        {
            for (i = 0; i < depGroups.size(); i++)
            {
                dg = (DepGroup)depGroups.get(i);

                if(dg.getElements().size() > 0)
                {
                    for (Iterator iter = dg.getElements().iterator(); iter.hasNext() ;)
                    {
                        de = (DepElement)iter.next();

                        for (j = 0; j < depGroups.size(); j++)
                        {
                            dg2 = (DepGroup)depGroups.get(j);

                            if( (dg2.inUB(de.getName())) & (j < i) )
                            {
                                if(debug) System.out.println(this.getClass().getName()+": "+de.getName()+" from "+dg+" must be before "+dg2);
                                returnValue = false;
                            }
                            if( (dg2.inUA(de.getName())) & (j > i) )
                            {
                                if(debug) System.out.println(this.getClass().getName()+": "+de.getName()+" from "+dg+" must be after "+dg2);
                                returnValue = false;
                            }

                        }
                    }
                }
            }
        }
        return returnValue;
    }

    public void toFile(String fileName)
    {
        StringBuffer sb = new StringBuffer();

        String out ="";
        try
        {
            FileWriter fOut = new FileWriter(fileName);
            fOut.write(out);
            fOut.close();
        } catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in writing ... "+e);
        }

    }

    public String toString()
    {
        String NL = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        DepGroup dg;

        sb.append("DepGroups ="+NL);
        if(depGroups.size() > 0)
        {
            for (Iterator iter = depGroups.iterator(); iter.hasNext() ;)
            {
                dg = (DepGroup)iter.next();
                sb.append(dg.toString2()+NL+NL);
            }
        }

        return sb.toString();
    }

}//EOC

class DepElement
{
    String name;

    TreeSet useBefore;
    TreeSet useAfter;
    Object element;

    public DepElement()
    {
        useBefore = new TreeSet();
        useAfter = new TreeSet();
    }

    public DepElement(String tmpName, TreeSet tmpUB, TreeSet tmpUA)
    {
        name = tmpName;
        useBefore = tmpUB;
        useAfter = tmpUA;
    }

    public DepElement(Object el, String tmpName, TreeSet tmpUB, TreeSet tmpUA)
    {
        name = tmpName;
        useBefore = tmpUB;
        useAfter = tmpUA;

        element = el;
    }

    public Object getElement()
    {
        return element;
    }

    public void setElement(Object el)
    {
        element = el;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String tmpName)
    {
        name = tmpName;
    }

    public void setUB(TreeSet tmpUB)
    {
        useBefore = tmpUB;
    }

    public TreeSet getUB()
    {
        return useBefore ;
    }

    public void setUA(TreeSet tmpUA)
    {
        useAfter = tmpUA;
    }

    public TreeSet getUA()
    {
        return useAfter;
    }

}//EOC

class DepGroup
{
    Vector depElements;
    //Set of all the elements UB
    TreeSet useBefore;
    //Set of all the elements UA
    TreeSet useAfter;

    public DepGroup()
    {
        depElements = new Vector();
        useBefore = new TreeSet();
        useAfter = new TreeSet();
    }

    public void setUB(TreeSet tmpUB)
    {
        useBefore = tmpUB;
    }

    public TreeSet getUB()
    {
        return useBefore ;
    }

    public void setUA(TreeSet tmpUA)
    {
        useAfter = tmpUA;
    }

    public TreeSet getUA()
    {
        return useAfter;
    }

    public void addUB(String elName)
    {
        useBefore.add(elName);
    }

    public void addUA(String elName)
    {
        useAfter.add(elName);
    }

    public void addUB(DepElement tmpElement)
    {
        useBefore.add(tmpElement.getName());
    }

    public void addUA(DepElement tmpElement)
    {
        useAfter.add(tmpElement.getName());
    }

    public boolean addElement(DepElement tmpElement)
    {
        depElements.add(tmpElement);

        if(tmpElement.getUB().size() > 0)
        {
            for (Iterator iter = tmpElement.getUB().iterator(); iter.hasNext() ;)
            {
                useBefore.add(iter.next());
            }

        }

        if(tmpElement.getUA().size() > 0)
        {
            for (Iterator iter = tmpElement.getUA().iterator(); iter.hasNext() ;)
            {
                useAfter.add(iter.next());
            }

        }

        return false;
    }

    public Vector getElements()
    {
        return depElements;
    }

    //Only to use when beginning with insert of all elements
    public DepElement getFirstElement()
    {
        return (DepElement)depElements.firstElement();
    }

    //Returns true if this group contains the element
    public boolean contains(String elName)
    {
        if(depElements.size() > 0)
        {
            for (Iterator iter = depElements.iterator(); iter.hasNext() ;)
            {
                if(((DepElement)iter.next()).getName().equals(elName)) return true;
            }

        }

        return false;
    }

    //returns true if elName is in UA-List
    public boolean inUA(String elName)
    {
        if(useAfter.size() > 0)
        {
            return (useAfter.contains(elName));
        } else return false;
    }

    //returns true if elName is in UB-List
    public boolean inUB(String elName)
    {
        if(useBefore.size() > 0)
        {
            return (useBefore.contains(elName));
        } else return false;
    }

    //returns true if element is in UA-List
    public boolean inUA(DepElement de)
    {
        if(useAfter.size() > 0)
        {
            return (useAfter.contains(de.getName()));
        } else return false;
    }

    //returns true if element is in UB-List
    public boolean inUB(DepElement de)
    {
        if(useBefore.size() > 0)
        {
            return (useBefore.contains(de.getName()));
        } else return false;
    }


    public Iterator getElementIterator()
    {
        if(depElements.size() > 0)
        {
            return depElements.iterator();
        }else{
            return null;
        }
    }

    //Merges two groups into this group
    public void mergeGroup(DepGroup dg2)
    {
        if(dg2.getElements().size() > 0)
        {
            for (Iterator iter = dg2.getElements().iterator(); iter.hasNext() ;)
            {
                this.addElement((DepElement)iter.next());
            }

        }

    }

    //Returns true when dg2 has no dependencies between Elements (Means no elements of the groups in UB or UA)
    public boolean isFriendlyGroup(DepGroup dg2)
    {
        DepElement de;

        if(dg2.getElements().size() > 0)
        {
            for (Iterator iter = dg2.getElements().iterator(); iter.hasNext() ;)
            {
                de = (DepElement)iter.next();
                if(this.inUB(de)) return false;
                if(this.inUA(de)) return false;
            }

        }

        if(this.getElements().size() > 0)
        {
            for (Iterator iter = this.getElements().iterator(); iter.hasNext() ;)
            {
                de = (DepElement)iter.next();
                if(dg2.inUB(de)) return false;
                if(dg2.inUA(de)) return false;
            }

        }

        return true;

    }

    public String toString()
    {
        String NL = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();

        sb.append("{ ");
        if(depElements.size() > 0)
        {
            for (Iterator iter = depElements.iterator(); iter.hasNext() ;)
            {
                sb.append(((DepElement)iter.next()).getName());
                if(iter.hasNext()) sb.append(" / ");
            }

        }
        sb.append(" }");

        return sb.toString();
    }

    public String toString2()
    {
        String NL = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();

        sb.append("GROUP: "+this.toString()+NL);

        sb.append("UB: [ ");
        if(useBefore.size() > 0)
        {
            for (Iterator iter = useBefore.iterator(); iter.hasNext() ;)
            {
                sb.append(iter.next());
                if(iter.hasNext()) sb.append(" / ");
            }

        }
        sb.append(" ]"+NL);

        sb.append("UA: [ ");
        if(useAfter.size() > 0)
        {
            for (Iterator iter = useAfter.iterator(); iter.hasNext() ;)
            {
                sb.append(iter.next());
                if(iter.hasNext()) sb.append(" / ");
            }

        }
        sb.append(" ]");

        return sb.toString();
    }


}//EOC
