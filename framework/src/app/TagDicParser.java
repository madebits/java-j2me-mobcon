package mobcon.app;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class TagDicParser
{
    private String inputFile = "";
    private String tagPrefix = "";
    private String infoFile = "info.txt";
    private Hashtable tagHash = new Hashtable();
    private Hashtable infoHash = new Hashtable();

    private final static String NL = System.getProperty("line.separator");

    public TagDicParser()
    {
    }

    public void init()
    {
        //parse Input-Files and save them into the Hashtables
        readFile(inputFile);
        writeInfoFile();
    }

    public void init(String tmpInputFile)
    {
        //parse Input-Files and save them into the Hashtables
        readFile(tmpInputFile);
        writeInfoFile();
    }

    public Set getDPTags()
    {
        // returns the key-set of the DP-Tags
        return tagHash.keySet();
    }

    public String getDPTag(String key)
    {
        // returns tag in DP-Tags for the key(attribute)
        // Example: key = sort -> return dp.sort
        return tagHash.get(key).toString();
    }

    public void setInfoFile(String in)
    {
        infoFile = in;
    }

    public void writeInfoFile()
    {
        int i;
        StringBuffer sb = new StringBuffer();

        String key;
        try
        {
            FileWriter fOut = new FileWriter(infoFile);

            if(infoHash.size() > 0)
            {
                key = ""+tagPrefix;
                for(i = 0; i < key.length()+4; i++)
                {
                    sb.append("#");
                }
                sb.append(NL);
                sb.append("# "+key+" #").append(NL);
                for(i = 0; i < key.length()+4; i++)
                {
                    sb.append("#");
                }
                sb.append(NL);
                sb.append(infoHash.get("PREFIX"));
                sb.append(NL).append(NL);

                for (Iterator iter = infoHash.keySet().iterator(); iter.hasNext() ;)
                {
                    key = ""+iter.next();
                    if(!key.equals("PREFIX"))
                    {
                        sb.append(key).append(NL);
                        for(i = 0; i < key.length(); i++)
                        {
                            sb.append("-");
                        }
                        sb.append(NL);
                        sb.append(infoHash.get(key));
                        sb.append(NL).append(NL);
                    }
                }
            }

            fOut.write(sb.toString());
            fOut.close();
        } catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in writing Info-File "+e);
        }
    }

    public void readFile(String fileName)
    {
        //Reads pairs of Strings (key=value) from file into tagHash and ads to values Prefix tagPrefix
        String key = new String();
        String value = new String();

        boolean readInfo = false;
        String out ="";
        String infoTag = "";
        String infoTagContent = "";

        try{
            System.out.println("Readig file "+fileName+" ...");
            BufferedReader filereader = new BufferedReader(new FileReader(fileName));

            String line = new String();
            line = filereader.readLine();

            while(line != null){
                infoTag = "";
                infoTagContent = "";

                if((line.indexOf("=") != -1) && (line.indexOf("#") == -1))
                {
                    key = line.substring(0, line.indexOf("="));
                    value = line.substring(line.indexOf("=")+1);

                    key = key.trim();
                    value = value.trim();

                    if(key.equals("PREFIX")){
                        this.tagPrefix = value;
                    } else {
                        value = tagPrefix+"."+value;
                        tagHash.put(key, value);
                    }

                    readInfo = false;
                    infoHash.put(key, out);
                    out = "";
                }


                if((line.indexOf("#") == 0) && (line.indexOf("@") != -1) && (line.indexOf("=") != -1))
                {
                    readInfo = true;
                    infoTag = line.substring(line.indexOf("@"), line.indexOf("="));
                    infoTag.trim();
                    infoTagContent = line.substring(line.indexOf("=")+1);
                    if(infoTagContent.trim().length() > 0) infoTagContent = infoTagContent+NL;
                    out = out+infoTag+": "+NL+infoTagContent;
                }else if((line.indexOf("#") == 0) && readInfo){
                    // alreadey reading a tagInfo, means NL between entries
                    out = out+line.substring(line.indexOf("#")+1)+NL;
                }

                line = filereader.readLine();
            }

            filereader.close();

            //System.out.println(tagHash);

        }catch (Exception e){
            System.out.println("Exception in readFile: "+e);
        }
    }

    public static void main(String[] args)
    {
        TagDicParser tdc = new TagDicParser();
        tdc.setInfoFile(args[1]);

        tdc.init(args[0]);
    }

}