The project contains a Eclipse plugin which allows you to edit the additional compiler arguments 
of FlashBuilder. It uses a form based editor which shows all the available options and allows you 
to change them easily. No longer you have to search for the name of a option or the syntax.s 

Update site: [Right-click this link and copy the url to the Eclipse update mamanger window.](https://github.com/rbokel/Additional-Compiler-Arguments-Editor-Plugin/raw/master/trunk/main/eclipse/AdditionalCompilerArgumentsEditorPluginUpdateSite/site.xml)

The current version of the plugin supports most of the on/off and simple string options. 


Todo
-----
1. Implement more complex content types, multi strings etc. 
2. Implement export to additional_compiler_arguments.xml 


History 
-------
`2010/11/23` Switched to Eclipse forms toolkit. Added Signal/Slots for event handling. Factored 
out some functionality of the FormEditor to other classes, eg. WidgetLocator, FormElementConfig, 
FormElementList.   

`2010/11/23` Added a first implementation of a multi string property. Multiple strings can 
be added as a comma separated list. The append checkbox switches the assignment operator from 
= to +=. The text input needs to be replaced by a real component. I'm thinking about something 
like the build path component in JDT. A listbox which shows all the available values, 
some buttons on the rhs, eg. edit, remove, move up, move down.   
Also i fixed the title of the editor. It now shows the filename.

`2010/11/21` Factored out the logic to filter the string for additional compiler arguments. 
Much cleaner now. A renderer is used to render the model to a string. The renderer uses a 
predicate to filter the items, which are included into that string. The model does not know 
anything about this. Via the predicate we are able to switch the logic easily.   

`2010/11/20` Added a update site

`2010/11/19` Added a file INSTALL with some notes about the installation. Added a description of the 
project to the README

`2010/11/19` The first distribution of the plugin
I had a quick look around and it seems like it is not that easy to mavenize the 
project. Even the Groovy Eclipse plugin is not mavenized, even though they are the same guys 
that developed Maven. They simply commit the complete Eclipse projects including the libraries 
to the repo. If they can do it ... :)

So instead of this, i did some cleaning of the project and added the first distributable jar. 
You can install it into your Eclipse by dropping it into the plugin folder. After you should be 
able to doubleclick your .actionScriptProperties file and the new editor should open. The first 
tab contains a simple XML editor. It is helpful to check what the file looks like. The second 
tab contains the form editor. Click around a bit und go back and forth between form and xml view
to check if it works ok. 

`2010/11/18` My first github project
Bear with me guys. I just committed the very first, vey raw version of this plugin. 
It is just a copy of my eclipse project really. As a next step i want to mavenize it, so the 
library can go away. At the moment there is no binary available. However, you could create 
your own, if you know how to work with PDE in Eclipse. 