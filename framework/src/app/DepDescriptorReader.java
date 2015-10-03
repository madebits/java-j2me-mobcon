package mobcon.app;

import java.io.BufferedReader;
import java.io.FileReader;
import com.exploringxml.xml.*;

public class DepDescriptorReader
{
    private Node rootNode;      // The root node of the xml-file
    private int currGroup;      // The actual group-number, beginning with 1
    private int currTransformer;// The actual transformer-number, beginning with 1

    private final static String ROOT = "application";
    private final static String CONTAINER = "container";
    private final static String CID = "cid";
    private final static String TRANSFORMER = "transformer";
    private final static String PERSISTENCE = "transformer/persistence";
    private final static String SERVERIP = "server/ip";
    private final static String GENERATEKEY = "encryption/generatekey";

    public DepDescriptorReader(String fileName)
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

    // returns the current transformers CID
    public String getCID()
    {
        Node container = this.findNode(rootNode, CONTAINER, 1);
        return this.findNodeData(container, CID, 1);
    }

    // returns the persistence-transformers ip
    public String getIP()
    {
        Node persistence = this.findNode(rootNode, PERSISTENCE, 1, 1);
        return this.findNodeData(persistence, SERVERIP, 1, 1);
    }

    // returns the persistence-transformers ip
    public boolean getGenerateKey()
    {
        Node transformer = this.findNode(rootNode, TRANSFORMER, 1, 1);
        return (new Boolean(this.findNodeData(transformer, GENERATEKEY, 1, 1))).booleanValue();
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
             System.out.println(this.getClass().getName()+": Error in reading Deployment Descriptor "+e);
         }

        // parse string and build document tree beginnig from key "flow"
        Node tmp = new Xparse().parse(sb.toString());
        rootNode = this.findNode(tmp, ROOT, 1);
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