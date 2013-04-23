package com.serli.jderay.modules.impl;

import com.serli.jderay.modules.Module;
import com.serli.jderay.modules.ModuleManager;
import com.serli.jderay.modules.exceptions.AlreadyAddedVersionException;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.CyclicDependencyDetectedException;
import com.serli.jderay.modules.exceptions.DependencyNotFoundException;
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

public class Modules implements ModuleManager {

    
    private static final Logger logger = LoggerFactory.getLogger(Modules.class);
    
    private Map<String, Module> listModules;
    //      Name:Version

    public Modules() {
        this.listModules = new HashMap<>();    
    }
    
    public Modules(String ... modulesToLoad) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependencyNotFoundException, AlreadyAddedVersionException, NoMainModuleException, CyclicDependencyDetectedException {
        this.listModules = new HashMap<>();
        loadAutomaticaly( modulesToLoad );
    }
    
    private void loadAutomaticaly(String[] modulesToLoad) throws IOException, ParseException, InvalidModException, AlreadyAddedVersionException, DependencyNotFoundException, NoMainModuleException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CyclicDependencyDetectedException {
        logger.info("--------------------- Loading modules ---------------------");
        for (String module : modulesToLoad) {
            loadModule( module );
            logger.info("{} -> OK", module);
        }
        run();
    }
    
    
    @Override
    public Class getMainClass() {
        try {
            Module mainModule = findMainModule();
            return mainModule.getClassLoader().loadClass( mainModule.getMainClass() );
        }
        catch( NoMainModuleException | ClassNotFoundException e ) {
            return null;
        }
    }
    
    @Override
    public void run() throws DependencyNotFoundException, NoMainModuleException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CyclicDependencyDetectedException {
        if ( listModules.isEmpty() ) {
            logger.error("Please load modules before run.");
            return;
        }
        
        setDependencies();
        String[] args = {};

        Module mainModule = findMainModule();
        logger.debug("--------------------- Main module found : {} ---------------------", mainModule);
        
        String mainClassName = mainModule.getMainClass();
        logger.debug("--------------------- Main class found in {} : {} ---------------------", mainModule, mainClassName);
        
        logger.info("--------------------- Invoking main(String[] args) ---------------------");
        mainModule.invokeMain( mainClassName, args );
    }
    
    @Override
    public void loadModule(String path) throws IOException, ParseException, InvalidModException, AlreadyAddedVersionException {
        URL url = new URL("jar:file:" + path + ".jar!/");
        Module mod = new Module( url );
        addToMap(mod);
    }
    
    @Override
    public void loadModulesFromDirectory(String globalPath) throws BadArgumentsException, IOException, ParseException, InvalidModException, AlreadyAddedVersionException, DependencyNotFoundException, NoMainModuleException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CyclicDependencyDetectedException  {
        File folder = new File( globalPath );
        if ( !folder.isDirectory() )
            throw new BadArgumentsException(); 
        int i = 0;
        File[] modulesF = folder.listFiles(new JarFilter());
        String[] modulesPaths = new String[ modulesF.length ];
        for( File pathModule : modulesF ) {
            modulesPaths[i] = pathModule.getAbsolutePath().substring(0, pathModule.getAbsolutePath().length() - 4);
            i++;
        }
        loadAutomaticaly( modulesPaths );
    }

    private void setDependenciesLocal(Module mod) throws DependencyNotFoundException, CyclicDependencyDetectedException {
        Map<String, Module> modDependenciesNames = mod.getDependencies();
        
        for (String modCode : modDependenciesNames.keySet()) {
            if (this.listModules.containsKey(modCode)) {
                Module foundeDependency = this.listModules.get(modCode);
                mod.addDependency( foundeDependency );
                logger.debug("     * {}", modCode);
            } else {
                throw new DependencyNotFoundException( modCode );
            }
        }
    }
    
    private void checkCyclicDependency() throws CyclicDependencyDetectedException {
        logger.debug("--------------------- Checking cyclic dependencies ---------------------");
        for (Module mod : listModules.values()) {
            for (Module dep : mod.getDependencies().values() ) {
                if (dep.getDependencies().containsKey(mod.toString()))
                    throw new CyclicDependencyDetectedException("Direct Cyclic Dependency : " + mod + " <-> " + dep);
                else
                    dep.checkClyclicDependency( mod );
            }
            logger.debug("{} -> OK", mod);
        }
    }

    private void setDependencies() throws DependencyNotFoundException, CyclicDependencyDetectedException {
        logger.debug("--------------------- Loading dependences ---------------------");
        for (Module mod : listModules.values()) {
            logger.debug(" -> {}", mod);
            if ( !mod.getDependencies().isEmpty() )
                setDependenciesLocal( mod );
        }
        checkCyclicDependency();
    }

    private String formatKey(Module mod) {
        return mod.getName() + ":" + mod.getVersion();
    }

    private void addToMap(Module mod) throws AlreadyAddedVersionException {

        if (listModules.containsKey( formatKey(mod) ))
            throw new AlreadyAddedVersionException();
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
