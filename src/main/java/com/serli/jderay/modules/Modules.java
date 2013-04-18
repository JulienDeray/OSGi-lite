package com.serli.jderay.modules;


import com.serli.jderay.modules.exceptions.AllreadyAddedVersionException;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.DependenceNotFoundException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import com.serli.jderay.modules.exceptions.NoMainModuleException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.JarFilter;


/**
 * @author julien
 * 
 */

public class Modules {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependenceNotFoundException, AllreadyAddedVersionException, NoMainModuleException, BadArgumentsException {
        String[] modulesPaths;
        
        if ( args[0].equals( "-mp" ) ) {
            File folder = new File( args[1] );
            if ( !folder.isDirectory() )
                throw new BadArgumentsException(); 
            int i = 0;
            File[] modulesF = folder.listFiles(new JarFilter());
            modulesPaths = new String[ modulesF.length ];
            for( File pathModule : modulesF ) {
                modulesPaths[i] = pathModule.getAbsolutePath().substring(0, pathModule.getAbsolutePath().length() - 4);
                i++;
            }
        }
        else if ( args[0].equals( "-m" ) ) {
            modulesPaths = new String[ args.length - 1 ];
            for (int i = 1; i < args.length; i++)
                modulesPaths[i-1] = args[i];
        }
        else if ( args[0].equals( "-mprefix" ) ) {
            if ( !args[2].equals( "-m" ) )
                throw new BadArgumentsException();
            modulesPaths = new String[ args.length - 3 ];
            for (int i = 3; i < args.length; i++)
                modulesPaths[i-3] = args[1] + args[i];
        }
        else {
            throw new BadArgumentsException();
        }
        
        Modules modules = new Modules(modulesPaths);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(Modules.class);
    
    private Map<String, Module> listModules;
    //      Name:Version

    public Modules(String ... modulesToLoad) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependenceNotFoundException, AllreadyAddedVersionException, NoMainModuleException {
        this.listModules = new HashMap<String, Module>();
        String[] args = {};
        
        logger.info("--- Loading modules ---");
        for (String module : modulesToLoad) {
            loadModule( module );
            logger.info("{} -> OK", module);
        }

        setDependenciesGlobal();

        Module mainModule = findMainModule();
        logger.debug("--- Main module found : {} ---", mainModule);
        
        String mainClassName = mainModule.getMainClass();
        logger.debug("--- Main class found in {} : {} ---", mainModule, mainClassName);
        
        logger.info("--- Invoking main( ... ) ---");
        mainModule.invokeMain( mainClassName, args );
    }
  
    private Module loadModule(String path) throws IOException, ParseException, InvalidModException, AllreadyAddedVersionException {
        URL url = new URL("jar:file:" + path + ".jar!/");
        Module mod = new Module( url );
        addToMap(mod);
        return mod;
    }

    private void setDependencesLocal(Module mod) throws DependenceNotFoundException {
        Map<String, Module> modDependenciesNames = mod.getDependenciesNames();

        for (String modCode : modDependenciesNames.keySet()) {
            if (this.listModules.containsKey(modCode)) {
                mod.addDependence(this.listModules.get(modCode));
                logger.debug("* {}", modCode);
            } else {
                throw new DependenceNotFoundException( modCode );
            }
        }
    }

    private void setDependenciesGlobal() throws DependenceNotFoundException {
        logger.debug("--- Loading dependences ---");
        for (Module mod : listModules.values()) {
            logger.debug(" -> {}", mod);
            setDependencesLocal( mod );
        }
    }

    private String formatKey(Module mod) {
        return mod.getName() + ":" + mod.getVersion();
    }

    private void addToMap(Module mod) throws AllreadyAddedVersionException {

        if (listModules.containsKey( formatKey(mod) ))
            throw new AllreadyAddedVersionException();
        else
            listModules.put(formatKey(mod), mod);
    }

    private Module findMainModule() throws NoMainModuleException {
        
        for ( Module mod : listModules.values() ) {
            if ( mod.getMainClass() != null ) 
                return mod;
        }
        throw new NoMainModuleException();
    }

}
