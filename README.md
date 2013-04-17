OSGi-lite
=========

OSGi-lite is a made home modular container created for a pedagogical goal. 



Run
===

Programmatically : 
------------------

>Modules modules = new Modules(String[] modulesPaths);

Using command lines :
---------------------

### To include all modules in the pointed directory :
>-mp /the/path

### To choose modules one by one with absolute path :
>-m /path/to/module1 /path/to/module2

### To specify the common path and choose modules to include :
>-mprefix /path/to/modules -m module1 module2 module3
