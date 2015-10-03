package mobcon.storeables;

import mobcon.message.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class StoreableStringArray implements Storeable
{
    private String[] arr;

    public StoreableStringArray()
    {
    }

    public StoreableStringArray(String[] a)
    {
        this.arr = a;
    }

    public void setStringArray(String[] a)
    {
        this.arr = a;
    }

    public String[] getStringArray()
    {
        return arr;
    }

    public byte[] object2record() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);

        outputStream.writeInt(this.arr.length);

        int i;
        for(i=0; i < this.arr.length;i++)
        {
            outputStream.writeUTF(this.arr[i]);
        }


        return baos.toByteArray();
    }

    public void record2object(byte[] rec) throws Exception
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(rec);
        DataInputStream inputStream = new DataInputStream(bais);

        int arrSize = 0;
        arrSize = inputStream.readInt();
        this.arr = new String[arrSize];

        int i;

        for(i=0; i < this.arr.length;i++)
        {
            this.arr[i] = inputStream.readUTF();
        }

    }
}