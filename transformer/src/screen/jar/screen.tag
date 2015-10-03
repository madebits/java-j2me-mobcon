# @addedFields =
# - String firstForm 	
# Holds the name of the field, that will be the first display shown in this application
# - Hashtable context	
# In this Hashtable all needed values for the session-context are stored. 
# (See Transformer session)
# - String[] listElements	
# Here are stored the elements of the last called List-Object. 
# Must be accessed when evaluating the select-command of a List.
# - Command nextCommand, Command backCommand, Command exitCommand	
# If one of these is choosen as tag in a Form-Object.
# - Command selectCommandXxx	
# Inserted if a List-Object is tagged. (xxx is the field-name of the List)
# - TextBox messageBox	
# Needed for the method callMessageBox

# @addedMethods =
# - void callXxx(), for Form xxx	
# Instantiates the Form-Object, adds provided Textfields, Buttons... 
# Remembers in context, that this Form is called.
# If this method is called, this Form will be dis-played as defined.
# - void callXxx(), for List xxx	
# Instantiates the List-Object, adds provided list-elements... 
# Remembers in context, that this Form is called.
# If this method is called, this List will be displayed as defined.
# - void callXxx(), for TextField xxx	
# Instantiates the TextField-Object, sets provided label and text 
# If this method is called, this TextField will be configurated as defined. 
# (Must only be appended to the Displayable)
# - void callXxx(String text), for TextField xxx	
# Like above, only with a new text.
# - void callXxx(), for Command xxx	
# Empty method or, when execute tag is set, with code from execute-tag. 
# Will be called when the associated command is executed.
# - void viewDisplay(String displayName)	
# This method maps the field-name from Display-able fields to the callXxx-method, 
# which calls this field. When this method is called, it will call the callXxx-method 
# and change the display

# @changedMethods =
# - void startApp()	
# To end: Initializes the application with the Display that is saved in firstForm
# - void destroyApp( boolean unconditional)	
# To begin: Puts the fields tagged with "addToSession" to the session-context 
# and stores it on server-side
# - void commandAction( Command command,  Displayable screen)	
# All commands will be added as needed and defined in the tags.

# @serverOps =
# -

PREFIX = @scr


# @effects =
# This Displayable-Object will be the first Display shown
# @parameter = 
# -
firstDisplay = firstDisplay


# @effects =
# (Obligatoire) Label of this Object. 
# Adds method callXxx (xxx is the field-name)
# @parameter = 
# String
label = label


# @effects =
# Adds a back-button to this display. When the back-button is pushed, the Display will be shown
# @parameter = 
# Displayable
backButton = backButton


# @effects =
# Adds a next-button to this display. When the next-button is pushed, the Display will be shown	
# @parameter = 
# Displayable
nextButton = nextButton


# @effects =
# Adds an exit-button to this display.
# @parameter = 
exitButton = exitButton


# @effects =
# Adds one or more TextField-Objects to this display.
# They are added and their callXxx (xxx is the field name) are called.
# @parameter = 
# TextField
textField = textField


# @effects =
# Text that will be shown in the Object
# @parameter = 
# String
string = string


# @effects =
# Constraints of this TextField
# @parameter = 
# constraints
constraints = constraints


# @effects =
# Max size of this TextField
# @parameter = 
# int
maxSize = maxSize


# @effects =
# ListType of this Object
# @parameter = 
# ListType
listType = listType


# @effects =
# The elements, that will be shown in the list
# @parameter = 
# One or more String
listElements = listElements


# @effects =
# The elements, that will be shown in the list
# @parameter = 
# String[]
listElementArray = listElementArray


# @effects =
# Executes the provided method, when something in the List is choosen
# @parameter = 
# Method-name
commandAction = commandAction


# @effects =
# Adds a self-defined-button to the display. This command must be defined in code.
# They are added and their callXxx (xxx is the com-mand-field name) are called when executed.
# @parameter = 
# Command
command = command


# @effects =
# (Obligatoire for Command-Object)
# Adds method callXxx (xxx is the field-name)
# @parameter = 
# -
addCommand = addCommand


# @effects =
# Code-Block that will be executed, when the command is called. 
# For complicated commands its bet-ter not to write in this tag, 
# but to add an entire callXxx-method in code.
# @parameter = 
# Code-Block
execute = execute


# @effects =
# Adds an Image-Object to this form. Will always be the last Object added.
# @parameter = 
# Image
image = image


# @effects = 
# Text that will be shown when, allert appears
# @parameter = 
# String
alertText = alertText


# @effects =
# This Image will be shown for Allert
# @parameter = 
# Image
alertImage = alertImage


# @effects =
# Type of allert
# @parameter = 
# Allert.TYPE
alertType = alertType


# @effects =
# Time in ms. So long allert will be shown
# @parameter = 
# int
alertTimeout = alertTimeout


# @effects =
# Next Screen, when allert is dismissed
# @parameter = 
# Displayable
alertNextScreen = alertNextScreen


# @effects =
# Adds this stringItem to the displayable
# @parameter = 
# StringItem
stringItem = stringItem


# @effects =
# The mode for the Image-Object
# @parameter = 
# mode
mode = mode


# @effects =
# Width of the Object
# @parameter = 
# int
width = width


# @effects =
# Height of the Object
# @parameter = 
# int
height = height


# @effects =
# Adds an ImageItem to this displayable
# @parameter = 
# ImageItem
imageItem = imageItem


# @effects =
# Alternativ text for an image
# @parameter = 
# String
altText = altText


# @effects =
# Layout of the ImageItem
# @parameter = 
# layout
layout = layout


# @effects =
# FontFace of the StringItem
# @parameter = 
# Font
fontFace = fontFace


# @effects =
# FontStyle of the StringItem
# @parameter = 
# fontstyle
fontStyle = fontStyle


# @effects =
# FontSize of the StringItem
# @parameter = 
# fontsize
fontSize = fontSize


# @effects =
# Adds a ChoiceGroup-Object to this displayable
# @parameter = 
ChoiceGroup
choiceGroup = choiceGroup