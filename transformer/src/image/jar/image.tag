# @addedFields =

# @addedMethods =
# - Image retrieveImage(String imageName) 	
# Loads image imageName from serverside and returns the Image-object
# - Image retrieveImage(String imageName, int width, int heigth)	
# Like above, but here with width and height
# - Image retrieveImage(String imageName, int width, int heigth, int numColors, int maxSize, boolean dither)	
# Like above, but with maximum colors and maximum size of the image. 
# If the image memory size is higher, then  the width and height of 
# the image will be reduced until it matches

# @changedMethods =
# - Void startApp()	
# If there are tagged image objects, than these images will be 
# loaded allready in the startApp() method and stored in these image-objects

# @serverOps =
# 1	
# Data from client: String name, int colors, int width, int height, boolean dither, int memory	
# Gets image "name" on serverside and transforms it, corresponding to the parameters
PREFIX = @img


# @effects =
# (Obligatoire) Name of the image on server-side
# @parameter =
# String
name = name

# @effects =
# Maximum width of displayed image
# @parameter =
# int
width = width

# @effects =
# Maximum height of displayed image
# @parameter =
# int
height = height

# @effects =
# Maximum colors of displayed image
# @parameter =
# int
maxcolors = maxcolors

# @effects =
# Maximum size in bytes of displayed image
# @parameter =
# int
maxsize = maxsize


# @effects =
# Searches the source of the image local. The name parameter is a 
# resource name as defined by Class.getResourceAsStream(name).
# @parameter =
# -
local = local
