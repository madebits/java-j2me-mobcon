########
# DOCS #
########

1.1 What does a Mobile Container?
A mobile container manages common technical concerns and provides them transparently to the components. It serves as a wrapper module where other managed components live in, they request technical services from the container or information about its context.
So Mobile Containers reduce the cost of developing applications because technical concerns need not to be addressed repeatedly, what simplifies the debugging- and designing-processes.
1.2 Organization of a Mobile Container
The mobile application will not be executed only on the mobile device. Mobile container ar-chitecture can be seen as extension of the client-server model[11], that means part of the application logic run on the server side (server-side container, SC) and another part of the container will be placed on the client device (mobile container part, MC). The two parts, with the business logic that will use them, are a single mobile application.
The mobile container part contains logic to interact and respond to the needs of the custom application logic that will run under the container (BI - business interface). The two parts need to communicate with each-other and hence both need communication logic (CL). The communication logic takes care of transmitting data / requests between the two parts. The container logic (LG) found in both parts contains the custom logic that the container provides, depending on the type of the container.

Most of the container logic, also part of the business logic, goes into the server-side part, including the communication logic.
 



2   Parts of a Mobile Container
2.1 Mobile Container Set
Usually there are more than one single application on a device. An application is repres-sented by one mobile container set (each product line of mobile applications has its own set of containers). The container set includes implementations of technical concerns (contain-ers), some technical concerns can be included more than one time and in different realisa-tions. 

CID(Container ID): Each mobile containers set has a global, unique, 128-bit long ID.

2.2 Container
Containers implement different technical concerns (Container-Types).

2.3 Container-Type (Sub-Container)
That is the technical concern, that the container implements.
TID(Container-Type ID, PID): Each Container-Type has a unique ID denoting its type, which will be assigned by the designer when a specific technical concern is implemented. This ID will be the same for all containers that implement that technical concern.
2.4 Container Instance
A container instance is a specific implementation of a container-type, a container, in a con-tainer set. Because there can be more than one implementations of container-types in a con-tainer set we have to make this differentiation.
IID(Container-Instance ID): Unique number that will be assigned to each container instance, starting with 1.
3   Container Communication
3.1 Communication Router (CR, server-side)
We have a single point in the environment that takes care about routing communication messages between the two parts of the container and stands between the mobile device(s) and the containers. This is the container communication router (CR), it factorizes logic that is common to CL blocks for all server-side container parts. The number of communication pos-sibilities for the mobile device is greatly reduced communication is only possible with the CR unit. It is also a well-known point of service that server-side container parts can use to com-municate with the mobile device.

3.2 Communication Manager Unit (MU, mobile-side)
The same communication organization can be applied to the mobile container parts in the client device. The communication logic can be factorized out into a communication manager unit (MU). It will contain logic that is common to all mobile application parts. It will also for-ward / receive the communication messages of the two parts of the container to the container request router (CR) in the environment. The application components (AP) of the business logic, living inside the container, communicate only with the mobile container part.

 


3.3 Container Messages
A container message contains two parts, the head and the data. The head contains the Ids CID:TID:IID and the length of the data that follows (Because the length of the data can change).
Communication is socked based on a fixed port. Each request is processed in a separate thread on the server, for the client it’s the same (at the moment…).
3.3.1   How it works
A message is compiled in the client or server containing the header and the data. Based on the header Ids we know to which container instance the message will be forwarded. On the client-side the MU will forward the message based on the CID to a specific application. Each application knows, based on the TID and IID, where to forward the message. MU is the only point where the socked adress/port needs to be specified. The server uses similar logic for the CR.

4   Implementation of a Container
Each transformer is packed as a JAR-File. The JAR-Files are placed in the “plugins”- direc-tory. 
4.1 Contents of a Container
4.1.1   Transformer
A velocity-script(*.vm) that implements a technical concern.
4.1.2   Tags-File
It contains key-value-pairs, where the keys are properties and the values are the representa-tion of these properties in the Java-Source as a tag. (See 6)
4.1.3   Configuration-File
Contents:
-   Container TID, IID
-   Use before dependency-list, a list of container-types that must be run before that con-tainer.
-   Use after dependency-list, a list of container-types that must be run after that con-tainer.
4.1.4   Server-Side Container-Class
This class will be added to the server-side container and messages that will be send to this container (right CID, TID, IID), will be processed by this class. Must extend TransformerApp and implement the execute-method.




6   Helper Classes in mobcon.util.*
6.1 Control
Controls the workflow of the vdoclet-engine (Before it was written down in Control.vm). 
6.2 ClassTemplate (Method- and FieldTemplate)
Three classes which hold information to generate classes. They hold the methods, fields, interfaces… of a class which can be mixed with other Class- and MethodTemplates. They are used while the source-files are parsed by the transformers to represent the generated classes.

Example of use:
 
6.3 MixTemplate (DefaultMixTemplate)
Interface with two methods “mixClassTemplates”  and “mixMethodTemplates” . There you can define how ClassTemplates and MethodTemplates are mixed together. Offers possibility to change the default mixing behaviour defined in Class DefaultMixTemplate.
MixTemplates are placed in the /root/plugins/MixTemplates directory. For the moment they can only be applied to a transformer in the depend.xml-file (see …)
6.4 DepBuilder
Class which is used to build the dependencie-graph between containers or, more general, elements. Reads the Use-Before- and Use-After-Lists out of the Transformers manifest-file and builds the dependencies between them. After that it processes which transformers can be processed in parallel(they have no dependencies between them) and groups them to-gether. Then it specifies in which order the transformer-groups must be processed. 
Could be made much better, but is good enough for the moment
6.5 DepDescriptorReader
Reads the global deployment descriptor from a file. At the moment only to get the containers CID and ip-adress for the data-persistence-transformer. TOFIX.
6.6 DepFileReader
When there is a file named depend.xml in the plugins-directory, the dependencies are not build using the transformer-manifest-files. The new dependecies are build now using this file and this happens with the help of the DepFileReader-Class that uses the technology pro-vided from Xparse-J (Xparse-J ).
6.7 JadCreator
Simple class that gets a Jar-File as input and creates an Jad-File afterwards. I made that to avoid to look repeatedly on the file-size of the Jar-File…
6.8 TagDic
A class that keeps a kind of dictionary for the tags used by the transformers. Uses Java PropertyResourceBundle and can is initiated from tag-file in its containers jar-file. So tag names can be changed later, if required, without editing the code.
6.9 Transformer
Keeps all the information about and the access to a container-jar-file. Helps accessing the containers transformer-vm file, the tag-file, the use-before and after-list and so on… Holds also the ClassTemplate that results of processing the source-files with this transformer and other ClassTemplates that this Transformer produces (for example the Store and StoreOb-ject-classes in the data-persistence-transformer).
6.10    TransMan
Manages the control flow of the transforming process and calls all the methods for example to build the dependency-structure, or for writing out the ClassTemplates into Java-Source files, reading an extern dependency file...
6.11    VelocityNullLogEvent
Class to avoid this event and produce not necessary output.




7   Helper Classes in mobcon.message.*
7.1 MessageHandler
This is a class derived from Thread and is called, when a message is send on mobile-side. So the operation doesn’t block. Methods are sendNetMessage(NetMessage) and getNet-Message(NetMessage). The first one sends only messages to the server and the second one sends a message and waits for result then.
7.2 NetMessage
Net messages are like they were introduced before and hold the destinations CID, TID and IID. Also they have object2record- and record2object-methods to send them in network. This class also implements the Storeable-interface.
7.3 Storeable
This interface consists only of two methods the object2record and record2object. Classes that can be send in the data-part of messages, must be of that type to provide a way to send
them (better to get the bytecode of the object and to reconstruct the object from bytecode). This is needed, cause we have no general way on clientside (MIDP) to get bytecode from any objects (like ObjectOutputStream or similar).
8   Helper Classes in mobcon.server.*
8.1 TransformerApp
All server-side transformers must extend this class. Most important is the execute(int, byte[], Socket)-method. This is called, when a message arrives for this transformer. Here you can get the TID and IID, too.
8.2 CR
Is the Connector Router on server-side. The Connector Router processes the message and sends it to the right container and transformer, depending on the CID, PID, IID.
8.3 ContainerApp
This is the class on server-side to hold a whole mobile-application. You can add the trans-formersApps here. 
9   Helper Classes in mobcon.storeable.*
Here are some implementations of storeable-objects, which can be stored over network or in the RMS of the mobile device.
9.1 StoreableStringArray
This class can hold a string-array and process it into a byteArray or process a byteArray and gives the resulting string-array back.
10  Generated Classes
10.1    Mobile Side
10.1.1  CTX 
That is the container’s context. In the moment, its only responsible to send and receive Net-Messages.
10.1.2  MU
Communication Manager Unit. See above. At the moment starts only the threads to send and receive NetMessages.
10.2    Server Side
10.2.1  MobConMain
The entry point of the MobileContainer at server-side. This class must be inserted in the server-code to process the new sockets and call the right containers and transformers. The server-side transformer-parts are automatically inserted and generated. The information to do this, is read from the transformers-manifest.


 

15  Dependency file (depend.xml)
If you want to define another order in which the transformers are applied to the source-files, you can do this in the file “depend.xml”. If the engine finds this file in the plugin-directory it will be applied. 
Each group-tag will be processed one after the other in the transforming process. Trans-formers that will be processed in parallel are grouped in the group-tags. To specify a trans-former you need to write down the transformers tid and iid. However, the resulting ClassTemplates are mixed together seriell afterwards, in the order they are written down in the group-tags.
If you want another mixing behaviour of the transformers defined in the group-tags, you can use the MixTemplate-interface to create a mixing-class. If you define a mixing class in the merger-tag it will be applied to mix together the transformer in which the merger-tag is speci-fied and the following transformer.
If you want another mixing behaviour of the groups you can define it in the merger-tag of the groups. It will be applied to mix together the group in which the merger-tag is specified and the following group. (TODO)
The file is written in xml, with the following tags:
-   flow: Main-Tag
o   group: Signifies a group of transformers that will be processed in parallel
?   transformer: Holds the tid, iid and merger-tag
•   tid: The transformers-tid
•   iid: The transformers-iid
•   merger: Defines the way how ClassTemplates are merged
o   class: The Class which is used to mix ClassTemplates




16 Transforming Process
-   The engine looks up for any Java-Source-Files in the src-directory
-   Then it searches for transformers (JAR-Files) in the plugin-directory
-   The transformers-jar-files are opened and the dependency-structure with workflow are build from the 
    information provided by the manifest-file or, if there is a file named de-pedn.xml in the 
    plugin-directory, the dependency-structure provided in depend.xml is used.
-   The result of this is a number of TransformerGroups, which can consist of one or more transformers. 
    Transformers that are in the same TransformerGroup will be processed in parallel.
-   Then, according to the workflow, one TransformerGroup after another will be executed. 
    All transformers in one group are executed in parallel. Each TransformerGroup and each Transformer 
    hold their own ClassTemplates, which result on the transforming process.  
-   The transformers read their dictionaries to know which tags are used in the Java-sources to 
    represent properties
-   Depending on the class-tags (they tell us which concern to apply) of the Java-Sources, 
    the different transformers are applied to them
-   They parse the Java-Sources, using the vDoclet parsed object tree of the tag marked Java-Sources 
    and the Class Templates to generate classes, methodes, fields... depend-ing on the tags in the 
    Java-Sources. For each Transformer and each TransformerGroup there is one ClassTemplate
-   When all the Transformers in a TransformerGroup are executed, the ClassTemplates in the 
    Transformers are mixed together, according to the methods specified in  MixTem-plate-Classes, 
    and the resulting ClassTemplate is stored in the TransformerGroup
-   When all TransformerGroups are executed, the ClassTemplates of the Transformer-Groups are mixed 
    together according to the methods specified in  MixTemplate-Classes. The result is one final 
    ClassTemplate
-   Finally the Java-Class is generated using the final ClassTemplate, which represents it

Example: 
<flow>
    <group>
        <transformer>
            <tid>1</tid>
            <iid>1</iid>
        </transformer>
    </group>    
    
    <group>
        <transformer>
            <tid>2</tid>
            <iid>1</iid>
            <merger>
                <class>DepMerger</class>            
            </merger>
        </transformer>

        <transformer>
            <tid>3</tid>
            <iid>1</iid>
        </transformer>      
    </group>
</flow>

The resulting transforming process:
-   First the transformer 1.1 is applied to the Source-Files
-   The the transformers 2.1 and 3.1 are executed in parallel and the resulting ClassTemplates are mixed together with the help of the MixTemplate “DepMerger”
-   Then the ClassTemplates from (1.1) and (2.1, 3.1) are mixed together




20 Generated files (others than in 12.):
-   CTX.java (Container-Context): 
    Including the CID from an external deployment-descriptor (CID is only generated once after the
    application is developped)
-   MU.java (Mobile-Connector-Management-Unit): 
    Including the CONNECTOR_URL from external deployment-descriptor.
-   Main-File from Transformer-Generated-Files:
    Must Contain the TID and IID, both from the manifest-file of the transformers applied to it. 




dir-structure:
--------------

framework
|
|---build(batch commands)
|
|---changed-src(src of the changed libraries)
|
|---classes (place of the compiled sources)
|
|---doc (place of the documentation)
|
|---lib (place of the used libraries)
|
|---out (place of the mobcon-library)
|
|---src (place of the mobcon sources)


