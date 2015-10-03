import java.net.Socket;
import mobcon.server.*;

public class MobConMain
{
protected CR cr;

public  MobConMain()
{
cr = new CR();
ContainerApp ca = new ContainerApp("1a32b8bb3a603ef3dc0cd2765dade2a3");
ca.addTransformerApp(new DataPersistenceTrans("01", "01"));
cr.addContainerApp(ca);
}


public void process( Socket socket)throws Exception
{
cr.process(socket);
}



} //EOC
