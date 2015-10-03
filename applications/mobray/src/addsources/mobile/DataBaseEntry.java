import java.util.Hashtable;
import java.util.Enumeration;
import javax.microedition.lcdui.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mobcon.message.*;

public class DataBaseEntry implements Storeable
{
    String id = "";
    int securityLevel = 0;
    String patientName = "";
    String imageName = "";
    int revCnt = 0;
    public Hashtable reviews = null;

    public DataBaseEntry()
    {
        reviews = new Hashtable();
    }

    public void setId(String set)
    {
        this.id = set;
    }

    public String getId()
    {
        return this.id;
    }

    public void setSecurityLevel(int set)
    {
        this.securityLevel = set;
    }

    public int getSecurityLevel()
    {
        return this.securityLevel;
    }

    public void setPatientName(String set)
    {
        this.patientName = set;
    }

    public String getPatientName()
    {
        return this.patientName;
    }

    public void setImageName(String set)
    {
        this.imageName = set;
    }

    public String getImageName()
    {
        return this.imageName;
    }

    public void setRevCnt(int set)
    {
        this.revCnt = set;
    }

    public int getRevCnt()
    {
        return this.revCnt;
    }

    public void addReview(String doctorName, String interpretation)
    {
        reviews.put(doctorName, interpretation);
        revCnt++;
    }

    public String getReview(String doctorName)
    {
        return (String)reviews.get(doctorName);
    }

    public String getAllReviews()
    {
        StringBuffer sb = new StringBuffer();


        if(reviews.size() > 0)
        {
            for (Enumeration e = reviews.keys() ; e.hasMoreElements() ;) {
                String doc = (String)e.nextElement();
                sb.append(doc).append(": \n");
                sb.append(this.getReview(doc)).append("\n");
                sb.append("-------------------------------------------").append("\n");
            }
        }

        return sb.toString();
    }

    public String toString()
    {
        StringBuffer out = new StringBuffer();

        out.append("\n").append("ID = ").append(this.getId()).append("\n");
        out.append("\n").append("Security = ").append(this.getSecurityLevel()).append("\n");
        out.append("\n").append("Patient = ").append(this.getPatientName()).append("\n");
        out.append("\n").append("Reviews : ").append(this.getAllReviews()).append("\n");

        return out.toString();
    }

    public byte[] object2record()throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);

        outputStream.writeUTF(this.getId());
        outputStream.writeInt(this.getSecurityLevel());
        outputStream.writeUTF(this.getPatientName());
        outputStream.writeUTF(this.getImageName());

        outputStream.writeInt(this.getRevCnt());
        if(reviews.size() > 0)
        {
            for (Enumeration e = reviews.keys() ; e.hasMoreElements() ;) {
                String doc = (String)e.nextElement();
                outputStream.writeUTF(doc);
                outputStream.writeUTF(this.getReview(doc));
            }
        }

        return baos.toByteArray();
    }


    public void record2object(byte[] rec)throws Exception
    {
        int i;
        ByteArrayInputStream bais = new ByteArrayInputStream(rec);
        DataInputStream inputStream = new DataInputStream(bais);

        this.setId(inputStream.readUTF());
        this.setSecurityLevel(inputStream.readInt());
        this.setPatientName(inputStream.readUTF());
        this.setImageName(inputStream.readUTF());

        int befCnt = inputStream.readInt();
        for(i=0; i < befCnt; i++)
        {
            String doc = inputStream.readUTF();
            String interpretation = inputStream.readUTF();
            this.addReview(doc, interpretation);
        }
    }

}