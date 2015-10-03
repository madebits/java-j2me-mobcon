import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import javax.microedition.rms.*;
import mobcon.message.*;
import mobcon.storeables.*;

public class MobAppStore implements RecordFilter
{
private MobAppStoreObject filter = null;
private RecordStore recordStore = null;
private  String storeName = "";

public  MobAppStore( String storeName)
{
this.storeName = storeName;
}


public void closeStore()throws Exception
{
if(isInited()){
recordStore.closeRecordStore();
recordStore = null;
}
}


public void deleteStore()throws Exception
{
if(!isInited()) RecordStore.deleteRecordStore(storeName);
}


public boolean exists( MobAppStoreObject o)throws Exception
{
return (getRecordID(o) < 0) ? false : true;
}


public MobAppStoreObject getFilter()
{
return this.filter;
}


public int getRecordID( MobAppStoreObject o)throws Exception
{
RecordEnumeration re = retrieve2(o, true);
if(re.hasNextElement()){
return re.nextRecordId();
}
return -1;
}


public MobAppStoreObject getStoreObject()
{
this.setFilter("MobAppStoreObject");
MobAppStoreObject storeObject = new MobAppStoreObject();
try {
RecordEnumeration re = recordStore.enumerateRecords(this, null, false);
try{
if(re.hasNextElement()){
storeObject.record2object(re.nextRecord());
}
}catch(Exception e){System.out.println(e);}
} catch (RecordStoreException rse) {
rse.printStackTrace();
System.out.println(rse.toString());
}
if(storeObject== null) storeObject = new MobAppStoreObject();
return storeObject;
}


public RecordStore initStore()throws RecordStoreException
{
if(!isInited()) recordStore = RecordStore.openRecordStore(storeName, true);
return recordStore;
}


public boolean isInited()
{
return ((recordStore == null) ? false : true);
}


public boolean matches( byte[] rec)
{
if(filter == null) return false; // none
MobAppStoreObject storeObject = new MobAppStoreObject();
try {
storeObject.record2object(rec);
} catch(Exception ex) {
return false;
}
boolean result = true;
if(!filter.getId().equals(MobAppStoreObject.AllId))
result = result &
(storeObject.getId().equals(filter.getId()));
return result;
}


public int remove( MobAppStoreObject o)throws Exception
{
int count = 0;
RecordEnumeration re = retrieve2(o, false);
while (re.hasNextElement()) {
count++;
recordStore.deleteRecord(re.nextRecordId());
}
return count;
}


public Vector retrieve( MobAppStoreObject o,  boolean sorted)throws Exception
{
RecordEnumeration re = retrieve2(o, sorted);
Vector result = new Vector();
while(re.hasNextElement())
{
int id = re.nextRecordId();
MobAppStoreObject el = new MobAppStoreObject();
el.record2object(recordStore.getRecord(id));
}
return result;
}


public RecordEnumeration retrieve2( MobAppStoreObject o,  boolean sorted)throws Exception
{
setFilter(o.getId());
return recordStore.enumerateRecords(this, null, false);
}


public void setFilter( MobAppStoreObject o)
{
filter = o;
}


public void setFilter( String id)
{
this.filter = new MobAppStoreObject();
filter.setId(id);
}


public int store( MobAppStoreObject o)throws Exception
{
if(this.isInited() && (o != null)) {
int id = getRecordID(o);
byte[] b = o.object2record();
if(id < 0) {
return recordStore.addRecord(b, 0, b.length);
} else {
recordStore.setRecord(id, b, 0, b.length);
return id;
}
}
return -1;
}


public String storeToString()
{
MobAppStoreObject storeObject = new MobAppStoreObject();
String out = "";
try {
RecordEnumeration re = recordStore.enumerateRecords(this, null, false);
while(re.hasNextElement()) {
try{
storeObject.record2object(re.nextRecord());
out = out+"\n"+storeObject.toString();
}catch(Exception e){System.out.println(e);}
}
return out;
} catch (RecordStoreException rse) {
rse.printStackTrace();
return rse.toString();
}
}


public String storeToString( String id)
{
this.setFilter(id);
MobAppStoreObject storeObject = new MobAppStoreObject();
String out = "";
try {
RecordEnumeration re = recordStore.enumerateRecords(this, null, false);
while(re.hasNextElement()) {
try{
storeObject.record2object(re.nextRecord());
out = out+"\n"+storeObject.toString();
}catch(Exception e){System.out.println(e);}
}
return out;
} catch (RecordStoreException rse) {
rse.printStackTrace();
return rse.toString();
}
}



} //EOC
