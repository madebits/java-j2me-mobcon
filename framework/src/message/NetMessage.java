package mobcon.message;

import java.io.*;
import javax.microedition.io.*;
import mobcon.util.ByteArray;

public class NetMessage
{
    // Size in bytes
    public static final int CID_LEN = 16;
    public static final int PID_LEN = 1;
    public static final int IID_LEN = 1;
    public static final int OP_LEN = 1;

    public byte[] CID = null; // container-set ID 128 bit
    public byte[] PID = null; // container plugin ID 8 bit
    public byte[] IID = null; // container instance ID 8 bit
    public byte[] OID = null;   // plugin dependent operation code 8 bit

    public byte[] data = null;// plugin dependent operation data

    public NetMessage()
    {
        CID = new byte[CID_LEN];
        PID = new byte[PID_LEN];
        IID = new byte[IID_LEN];
        OID = new byte[OP_LEN];
    }

    public void setCID(String cid)
    {
        if(cid.length() == 1) {
            cid = "0"+cid;
        }

        CID = ByteArray.stringToByteArray(cid);
    }

    public void setPID(String pid)
    {
        if(pid.length() == 1) {
            pid = "0"+pid;
        }

        PID = ByteArray.stringToByteArray(pid);
    }

    public void setIID(String iid)
    {
        if(iid.length() == 1) {
            iid = "0"+iid;
        }

        IID = ByteArray.stringToByteArray(iid);
    }

    public void setOID(String oid)
    {
        if(oid.length() == 1) {
            oid = "0"+oid;
        }

        OID = ByteArray.stringToByteArray(oid);
    }

    public void setData(byte[] dat)
    {
        this.data = dat;
    }

    public String getCID()
    {
        return ByteArray.byteArrayToString(CID);
    }

    public String getPID()
    {
        return ByteArray.byteArrayToString(PID);
    }

    public String getIID()
    {
        return ByteArray.byteArrayToString(IID);
    }

    public String getOID()
    {
        return ByteArray.byteArrayToString(OID);
    }

    public byte[] getData()
    {
        return data;
    }


    public static byte[] toByte(NetMessage o)  throws Exception
    {
        if(!isValid(o)) throw new Exception("NetMessage: "+"invalid message");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);

        int length = CID_LEN + PID_LEN + IID_LEN + OP_LEN;
        int dataLength = 0;

        if(o.data != null )
        {
            dataLength = o.data.length;
            length += dataLength;
        }

        outputStream.write(o.CID);
        outputStream.write(o.PID);
        outputStream.write(o.IID);
        outputStream.write(o.OID);
        outputStream.writeInt(dataLength);

        if(dataLength > 0)
        {
            outputStream.write(o.data);
        }
        return baos.toByteArray();
    }

    public static NetMessage byteToNetMessage(byte[] b) throws Exception
    {
            if((b == null) || (b.length < CID_LEN + PID_LEN + IID_LEN + OP_LEN))
                throw new Exception("NetMessage: "+"invalid message");

            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            DataInputStream inputStream = new DataInputStream(bais);

            NetMessage o = new NetMessage();

            inputStream.read(o.CID);
            inputStream.read(o.PID);
            inputStream.read(o.IID);
            inputStream.read(o.OID);

            //int dataLength = b.length - CID_LEN + PID_LEN + OP_LEN;
            int dataLength = inputStream.readInt();

            if(dataLength > 0)
            {
                o.data = new byte[dataLength];
                inputStream.read(o.data);
            }

            if(!isValid(o)) throw new Exception("NetMessage: "+"invalid message created");

            return o;
    }

    public static NetMessage streamToNetMessage(DataInputStream inputStream) throws Exception
    {
        NetMessage o = new NetMessage();

        inputStream.read(o.CID);
        inputStream.read(o.PID);
        inputStream.read(o.IID);
        inputStream.read(o.OID);

        //int dataLength = b.length - CID_LEN + PID_LEN + OP_LEN;
        int dataLength = inputStream.readInt();

        if(dataLength > 0)
        {
            o.data = new byte[dataLength];
            inputStream.read(o.data);
        }

        if(!isValid(o)) throw new Exception("NetMessage: "+"invalid message created");

        return o;
    }

    public static boolean isValid(NetMessage o)
    {
        if(o == null) return false;
        if(o.CID == null || (o.CID.length != CID_LEN))
        {
            System.out.println("Wrong CID length: "+o.CID.length);
            return false;
        }
        if(o.PID == null || (o.PID.length != PID_LEN))
        {
            System.out.println("Wrong PID length: "+o.PID.length);
            return false;
        }
        if(o.IID == null || (o.IID.length != IID_LEN))
        {
            System.out.println("Wrong IID length: "+o.IID.length);
            return false;
        }

        return true;
    }

    public static boolean isEqual(byte[] b1, byte[] b2)
    {
        if(b1 == null || b2 == null) return false;
        if(b1.length != b2.length) return false;
        for(int i = 0; i < b1.length; i++)
            if(b1[i] != b2[2]) return false;
        return true;
    }

    public static int compare(NetMessage o1, NetMessage o2)
    {
        if(isEqual(o1.CID, o2.CID))
        {
            if(isEqual(o1.PID, o2.PID))
            {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    public String toString()
    {

        int i;
        StringBuffer out = new StringBuffer();

        out.append("\n").append("CID: ").append("\n");
        out.append(getCID());
        out.append("\n");

        out.append("PID: ").append("\n");
        out.append(getPID());
        out.append("\n");

        out.append("IID: ").append("\n");
        out.append(getIID());
        out.append("\n");

        out.append("OID: ");
        out.append(getOID()).append("\n");

        out.append("DATA: ").append("\n");

//        if(data != null)out.append(new String(data)).append("\n");
        if(data != null)out.append("DATA").append("\n");
        else out.append("NO DATA").append("\n");

        return out.toString();
    }

} //EOC