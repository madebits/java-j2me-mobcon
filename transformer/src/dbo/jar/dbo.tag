# @serverOps =
# -

# @description =
# Creates a class that holds the store and the operations to store this class-objects. The name of the
# store is the class-name of the decorated object, so pay attention that it is a a unique one. You can set
# the name of the store yourself with the tag "storeName".

PREFIX = @dbo

# @effects =
# This field is a primary key for this object
# @parameter = 
primarykey = pk

# @effects =
# Sorts the entries in the store, depending on this field.
# If more than one field is sorted, the objects will be sorted depending on the order of the tagged "sort"-fields.
# @parameter = 
# asc - Objects are sorted in ascending order
# des - Objects are sorted in descending order
sort = sort

# @effects =
# Validates if the field's-value is not less than this. 
# @parameter = 
# int
minValue = min

# @effects =
# Validates if the field's-value is not higher than this. 
# @parameter = 
# int
maxValue = max

# @classTag =
# @effects =
# Here you have to specify, which dbo-objects you use in your application.
# The container creates the stores for this objects and handles them.
# @parameter = 
# Class-Name of the used dbo-objects.
use = use

# @classTag =
# @effects =
# Here you can specify another name for the store used in the MIDlet. It hasnt to be longer than 32 chars.
# @parameter = 
# String
storeName = storeName