package mobcon.message;

import java.io.IOException;

public interface Storeable
{
    public byte[] object2record() throws IOException;
    public void record2object(byte[] rec) throws Exception;
}