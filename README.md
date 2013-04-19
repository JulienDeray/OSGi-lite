OSGi-lite
=========

OSGi-lite is a made home modular container created for a pedagogical goal. 



Run
===

Programmatically : 
------------------

>ModuleManager modules = new Modules("/path/to/module1", "/path/to/module2", ...);

or

>ModuleManager modules = new Modules();

>modules.loadModule("path/to/module1");

>modules.loadModule("path/to/module2");

>[...]

>modules.run();

or

>ModuleManager modules = new Modules();

>modules.loadModulesFromDirectory("/the/global/path");

>modules.run();

Using command lines :
---------------------

### To include all modules in the pointed directory :
>java -jar ModuleManager-1.0-SNAPSHOT.jar -mp /the/path

### To choose modules one by one with absolute path :
>java -jar ModuleManager-1.0-SNAPSHOT.jar -m /path/to/module1 /path/to/module2

### To specify the common path and choose modules to include :
>java -jar ModuleManager-1.0-SNAPSHOT.jar -mprefix /path/to/modules -m module1 module2 module3
