########
# DOCS #
########

Description:
------------
A transformer is a Velocity-Script that represents a technical concern. It takes two inputs: the vDoclet
parsed object tree of a taged marked Java-Source and the Class Template.Then, as it processes/transforms
the Java-Source, it makes changes to the CT (Example: Generates the get/set-methodes if marked by a tag 
in the Java-Source). The CT will then be passed to the next transformer that makes itself changes to the
CT and so on...
The different transformers are organized as plug-ins. You can put them in the plugins-directory and then
they will be included automatically in the transforming-process.


Elements of a transformer:
--------------------------
- Manifest-file: 
    Information about the transformer
- TagDic: 
    Mappings of the tags in the Java-Source
- Server-file: 
    Server-side transformer logic
- vm-File: 
    Velocity-Script that represents a technical concern


TagDic(*.tag):
--------------
Tags are used by the transformers to evaluate which concerns should be applied to the Java-Source and 
with which properties. For example you can mark a field in the source-file with a tag that tells the 
transformer to make it accessesibel with get/set-methods.
The dictionaries hold key-value-pairs, where the keys are properties and the values are the 
representation of this properties in the Java-Source as a tag. That means that the tags can be 
freely defined, because in the transformers you search them only by the property-name.
A speciall key is the "PREFIX"-key. Its value will be the prefix to all other tags defined in the 
dictionary-file.


Manifest-file:
--------------
The information about the container is stored in the manifest-attributes. 
These are:
- Transformer-Name: The name of the transformer
- TID: Container-Type-ID
- IID: Container-Type-Instance-ID
- File-Name: Name of the transformer.vm file
- Tag-File: Name of the tag-file
- Use-Before: List separated by "/" with transformers IDs(TID.IID) that must be used before
- Use-After: List separated by "/" with transformers IDs(TID.IID) that must be used after
- Server-File: The transformer-file in the jar, which will be used on server-side to process NetMessages


transformer-file (*.vm):
------------------------
Input:
- vDoclet parsed object tree of a taged marked Java-Source
- Class Template

Output:
- Modified CT, depending on Java-Source


Usage of transformer scripts:
-----------------------------
$CT is the standard ClassTemplate that is property of the transformer and will be trans-formed afterwards into a class. 
$transformer is the transformer-class that is processed at the moment. At the moment it is used to access the TagDic ($transformer.getTagDic()) and to store other ClassTemplates ($transformer.addOtherClassTemplate(…)) with the transformer, these will be transformed afterwards into a class.
$docInfo is the vdoclet-class doclet.docinfo.DocInfo
$vdoclet is the vdoclet-class vdoclet.Generator
$CID is the Containers CID
$TID is the transformers TID
$IID is the transformers IID
$SERVERIP is the server-IP from the deployment-descriptor

With #set($class = $vdoclet.makeBean("…"))  you can construct a new class and access it via $class.
Classes used to construct ClassTemplates are (Usage can be seen in the example files):
mobcon.util.FieldTemplate 
- methods: get/set -access, -type, -name, -value
mobcon.util.MethodTemplate 
- methods: 
  get/set -type, -name, -access
  add -exceptions, -args, -fields, -begin, -body, -end 
mobcon.util.ClassTemplate 
- methods: 
  get/set -name, -access, -baseClass
  add -imports, -interfaces, -methods, -fields




dir-structure:
--------------

transformer
|
|---build(batch commands)
|
|---out (place of the jared plugins)
|
|---src (place of the transformer sources)
|
|---txt (place of the generated transformer documentation)


