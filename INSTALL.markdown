Automatic installation via the Eclipse update
--------------------------------------------- 
Update site <a href="https://github.com/rbokel/Additional-Compiler-Arguments-Editor-Plugin/raw/master/trunk/main/eclipse/AdditionalCompilerArgumentsEditorPluginUpdateSite/site.xml">https://github.com/rbokel/Additional-Compiler-Arguments-Editor-Plugin/raw/master/trunk/main/eclipse/AdditionalCompilerArgumentsEditorPluginUpdateSite/site.xml</a>

Manual installation
-------------------
Download the latest version of the plugin. You can find it <a href="https://github.com/rbokel/Additional-Compiler-Arguments-Editor-Plugin/tree/master/trunk/main/eclipse/AdditionalCompilerArgumentsEditorPluginUpdateSite/plugins/">here</a>.  
Close your Eclipse
Drop the file into the plugin directory of your Eclipse installation 
Start Eclipse 

Usage 
-----
If you doubleclick a .actionScriptProperties file, it should open in the new editor. 
If not, you might have registered another editor to this file. Right click the 
.actionScriptProperties file and use "open with" ActionScriptPropertiesEditor. 

The editor has two views. A xml view and a form view. You can switch the two views 
using the tabs at the bottom of the window. The xml editor is mainly for debugging. 
Try using the form editor and go back and forth to the xml view to check if the result 
looks ok. 