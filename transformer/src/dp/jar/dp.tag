# @addedFields =
# - {class.shortName}Store store
# Holds the RecordStore for this application. The name of the RecordStore is {class.shortName}

# @addedMethods =
# - Void setYyy(xxx in), for field yyy of type xxx	
# Stores the persistent field yyy with type xxx in the local (mobile) store
# - xxx getYyy(), for field yyy of type xxx		
# Gets the persistent field yyy with type xxx from the local (mobile) store
# - String storeObject(Storeable sa)	
# Stores the Storeable-Object sa at the server and returns a String id to get it later
# - void retrieveObject(String id,  Storeable sa)	
# Gets the object that is stored at server-side with ID id and saves it in sa
# - void store()	
# Stores the StoreObject to server
# - void retrieve()	
# Retrieves the StoreObject from server
# - String getMobileID()
# Retrieves a unique ID from the server and stores it in the StoreObject.
# So the pair mobile/application and its StoreObject on the server can be identified.

# @changedMethods =
# - Void startApp()	
# To begin: Loads the StoreObject
# - Void destroyApp(boolean)	
# Stores the StoreObject remotely and closes the {class.shortName}Store store

# @serverOps =
# 1	
# Data from client: StoreObject-byte-data	
# Store StoreObject local at first position in TreeMap
# 2	
# Data to client: StoreObject-byte-data	
# Send object back from local at first position in TreeMap
# 3	
# Data from client: Storeable-Object-byte-data	
# Store object local, return id
# 4	
# Data from client: String id
# Data to client: Storeable-Object-byte-data	
# Send object back from local TreeMap at posi-tion id
# 5
# Data from client: - 
# Data to client: StoreableStringData
# Server generates unique ID and send it back to the client as a String
PREFIX = @dp


# @effects =
# This field is added to the StoreObject. 
# A field with name All{Field.name} is added to the StoreObject, 
# that holds the default value of this type of field (String = "*", int = -1 ). 
# Get/Set-methods in the StoreObject are generated for this field.
# @parameter = -
access = access

# @effects =
# Only primitive fields or Storeable Obejcts can have this attribute.
# This field will be included, when the StoreObject is stored (locally or remotely). 
# More precise, it is in-cluded in the object2record and record2object meth-ods.
# @parameter = -
store = store