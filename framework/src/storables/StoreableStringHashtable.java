package mobcon.storeables;

import mobcon.message.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;

// A hashtable, which keys and values are Strings only
public class StoreableStringHashtable implements Storeable
{
    private Hashtable hash;

    public StoreableStringHashtable()
    {
        this.hash = new Hashtable();
    }

    public void setHashtable(Hashtable in)
    {
        this.hash = in;
    }

    public Hashtable getHashtable()
    {
        return hash;
    }

    public byte[] object2record() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);

        outputStream.writeInt((this.hash).size());

        if(this.hash.size() > 0)
        {
            for (Enumeration e = hash.keys() ; e.hasMoreElements() ;) {
                String key = (String)e.nextElement();
                outputStream.writeUTF(key);
                outputStream.writeUTF((String)(hash.get(key)));
            }
        }

        return baos.toByteArray();
    }

    public void record2object(byte[] rec) throws Exception
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(rec);
        DataInputStream inputStream = new DataInputStream(bais);

        int hashSize = 0;
        hashSize = inputStream.readInt();

        int i;

        String key;
        String value;

        if(hashSize > 0)
        {

            for(i=0; i < hashSize; i++)
            {
                key = inputStream.readUTF();
                value = inputStream.readUTF();
                this.hash.put(key, value);
            }
        }
    }
}