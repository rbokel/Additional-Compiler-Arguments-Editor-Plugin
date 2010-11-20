The project contains a Eclipse plugin which allows you to edit the additional compiler arguments 
of FlashBuilder. It uses a form based editor which shows all the available options and allows you 
to change them easily. No longer you need to search for the name of a option or the syntax. 

The current version of the plugin supports most of the on/off and simple string options. Options
with multiple strings and += options will be implemented soon. 

A note on security
------------------
Since this is a public site, everybody could upload his own, hacked version of the plugin. So please 
make sure, that the version you are about to download, has been uploaded by me (rbokel)

History 
-------
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