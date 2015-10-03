package mobcon.storeables;

import mobcon.message.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class StoreableStringData implements Storeable
{
    private String string;
    private byte[] data;

    public StoreableStringData()
    {
        string = "";
        data = new byte[1];
    }

    public StoreableStringData(String set, byte[] dat)
    {
        this.string = set;
        this.data = dat;
    }

    public void setString(String set)
    {
        this.string = set;
    }

    public String getString()
    {
        return string;
    }

    public void setData(byte[] set)
    {
        this.data = set;
    }

    public byte[] getData()
    {
        return data;
    }

    public byte[] object2record() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);

        outputStream.writeUTF(this.getString());
        outputStream.writeInt(this.getData().length);
        outputStream.write(this.getData());

        return baos.toByteArray();
    }

    public void record2object(byte[] rec) throws Exception
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(rec);
        DataInputStream inputStream = new DataInputStream(bais);

        this.setString(inputStream.readUTF());
        this.data = new byte[inputStream.readInt()];
        inputStream.read(this.data);
    }
}