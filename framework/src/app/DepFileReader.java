package mobcon.app;

import java.io.BufferedReader;
import java.io.FileReader;
import com.exploringxml.xml.*;

public class DepFileReader
{
    private Node rootNode;      // The root node of the xml-file
    private int currGroup;      // The actual group-number, beginning with 1
    private int currTransformer;// The actual transformer-number, beginning with 1

    private final static String TRANSFORMERNAME = "Transformer-Name";
    private final static String TID = "tid";
    private final static String IID = "iid";
    private final static String MERGER = "merger";
    private final static String MERGERCLASS = "class";
    private final static String TRANSFORMER = "transformer";
    private final static String GROUP = "group";
    private final static String ROOT = "flow";

    public DepFileReader(String fileName)
    {
        this.parseXML(fileName);
        currGroup = 0;
        currTransformer = 0;
    }

    public void resetCounter()
    {
        currGroup = 0;
        currTransformer = 0;
    }

    public void parseXML(String fileName)
    {
        StringBuffer sb = new StringBuffer();

        try
        {
            // read XML document from URL into a string
            BufferedReader filereader = new BufferedReader(new FileReader(fileName));

            String line = new String();
            line = filereader.readLine();
            while(line != null){
                sb.append(line);
                line = filereader.readLine();
            }
            //System.out.println(sb);

         } catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in reading Dep-File "+e);
         }

        // parse string and build document tree beginnig from key "flow"
        Node tmp = new Xparse().parse(sb.toString());
        rootNode = this.findNode(tmp, ROOT, 1);
    }

    // returns true if there is another group-element in the dependency-file
    public boolean nextGroup()
    {
        currGroup++;
        if(this.findNode(rootNode, GROUP, currGroup) != null)
        {
            currTransformer = 0;
            return true;
        } else {
            return false;
        }
    }

    // returns true if there is another transformer-element in the current group-element
    public boolean nextTransformer()
    {
        currTransformer++;

        if(this.findNode(rootNode, GROUP+"/"+TRANSFORMER, currGroup, currTransformer) != null)
        {
            return true;
        } else {
            return false;
        }

    }

    // returns the current transformers TID
    public String getTID()
    {
        String tid;

        Node transformer = this.findNode(rootNode, GROUP+"/"+TRANSFORMER, currGroup, currTransformer);
        tid = this.findNodeData(transformer, TID, 1);

        if(tid.length() == 1) {
            tid = "0"+tid;
        }

        return tid;

    }

    // returns the current transformers IID
    public String getIID()
    {
        String iid;
        Node transformer = this.findNode(rootNode, GROUP+"/"+TRANSFORMER, currGroup, currTransformer);
        iid = this.findNodeData(transformer, IID, 1);

        if(iid.length() == 1) {
            iid = "0"+iid;
        }

        return iid;
    }

    // returns the current transformers merger-class
    public String getTransformerMergerClass()
    {
        Node transformer = this.findNode(rootNode, GROUP+"/"+TRANSFORMER, currGroup, currTransformer);
        String merger = this.findNodeData(transformer, MERGER+"/"+MERGERCLASS, 1, 1);

        if(merger != null) return merger;
        else return null;
    }

    // returns the current transformers merger-class
    public String getGroupMergerClass()
    {
        Node group = this.findNode(rootNode, GROUP, currGroup);
        String merger = this.findNodeData(group, MERGER+"/"+MERGERCLASS, 1, 1);

        if(merger != null) return merger;
        else return null;
    }

    public Node findNode(Node node, String text, int idx)
    {
        //returns the Node with entry "text", under Node node

        int occur[] = {idx};
        Node doc = node.find(text, occur);

        return doc;
    }

    public Node findNode(Node node, String text, int idx1, int idx2)
    {
        //returns the Node with entry "text", under Node node

        int occur[] = {idx1, idx2};
        Node doc = node.find(text, occur);

        return doc;
    }

    public String findNodeData(Node node, String text, int idx)
    {
        //returns the character data in the idx-th child element "text" of node

        int occur[] = {idx};
        Node doc = node.find(text, occur);

        if(doc != null) return doc.getCharacters();
        else return null;
    }

    public String findNodeData(Node node, String text, int idx1, int idx2)
    {
        //returns the character data in the idx-th child element "text" of node

        int occur[] = {idx1, idx2};
        Node doc = node.find(text, occur);

        if(doc != null) return doc.getCharacters();
        else return null;
    }

}