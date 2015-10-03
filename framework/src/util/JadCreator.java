package mobcon.util;

import java.io.File;
import java.io.FileWriter;

public class JadCreator{
    private File jadFile;
    private File jarFile;

    private String startClassName;
    private String jarFileName;
    private String jadFileName;

    private String jadText = "";
    private long jarFileSize = 0;

    private final String NL = System.getProperty("line.separator");

    public void writeJad()
    {
        try
        {
            FileWriter fOut = new FileWriter(jadFile);
            fOut.write(jadText);
            fOut.close();
        } catch(Exception e){
            System.out.println(this.getClass().getName()+": Error in writing Jad-File "+e);
        }
    }

    public void createJadText()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("MIDlet-Name: MobConApp").append(NL);
        sb.append("MIDlet-Version: 2.0").append(NL);
        sb.append("MIDlet-Vendor: Sun Microsystems, Inc.").append(NL);
        sb.append("MIDlet-Jar-URL: "+jarFileName).append(NL);
        sb.append("MIDlet-Jar-Size: ").append(jarFileSize).append(NL);
        sb.append("MicroEdition-Profile: MIDP-2.0").append(NL);
        sb.append("MicroEdition-Configuration: CLDC-1.0").append(NL);
        sb.append("MIDlet-1: "+startClassName+",, "+startClassName).append(NL);

        jadText = sb.toString();
    }

    public void createJad()
    {
        jarFile = new File(jarFileName);

        jarFileSize = jarFile.length();
        this.createJadText();

        jadFile = new File(jadFileName);
        writeJad();
    }

    public static void main(String[] args)
    {
        JadCreator jc = new JadCreator();

        jc.startClassName = args[0];
        jc.jarFileName = args[1];
        jc.jadFileName = args[2];

        jc.createJad();
    }

}