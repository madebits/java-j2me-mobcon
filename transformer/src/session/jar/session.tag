# @description =
# Transformer stores to a session related data on the server. So it can be loaded later.
# The mobileID is used as sessionID.

# @addedMethods =
# - Void storeContext()	
# Stores the Hashtable context on server-side
# - Void retrieveContext()	
# Gets the Hashtable context from server-side

# @changedMethods =
# - Void startApp()	
# To begin: Gets the session-context from server, 
# initializes all fields that are taged with "addToSession", initializes the field 
# firstForm with the last Form seen in the last session
# - Void destroyApp( boolean unconditional)	
# To begin: Puts the fields tagged with "addToSession" to the session-context 
# and stores it on server-side

# @serverOps =
# 1	
# Data from client: StoreableStringHashtable ssh	
# Store Hastbale context on server.
# 2	
# From client: String sessionId
# From server: StoreableStringHashtable ssh	
# Send context to client
PREFIX = @ses


# @effects =
# Stroeable-Objects can be added to session. 
# They will be initialized in the startApp() method with the value of the last saved session.
# @parameter = 
# Storeable
addToSession = addToSession


# @classTag =
# @effects =
# If set, the last visited Display will be remembered
# @parameter = 
# -
rememberLastDisplay = rememberLastDisplay


# @effects =
# Can be added to displayable. This display wont be remembered in the
# sence of rememberLastDisplay
# @parameter = 
noBackEntry = noBackEntry