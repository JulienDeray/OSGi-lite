package com.serli.jderay.modules;

import com.serli.jderay.modules.exceptions.AlreadyAddedVersionException;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.CyclicDependencyDetectedException;
import com.serli.jderay.modules.exceptions.DependencyNotFoundException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import com.serli.jderay.modules.exceptions.NoMainModuleException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.json.simple.parser.ParseException;


/**
 *
 * @author julien
 */
public interface ModuleManager {

    /**
     * Load a module (.jar) in the module manager. 
     * @param path  Absolute path of the module without file extension.
     * @throws IOException
     * @throws ParseException
     * @throws InvalidModException
     * @throws AlreadyAddedVersionException
     */
    public void loadModule(String path) throws IOException, ParseException, InvalidModException, AlreadyAddedVersionException;
    
    /**
     * List loaded modules
     */
    public void listModules();
    
    /**
     * Load all modules (.jar) founded  into the directory in the module manager. 
     * @param path  Absolute path of the directory containing the modules
     * @throws IOException
     * @throws ParseException
     * @throws InvalidModException
     * @throws AlreadyAddedVersionException
     */
    public void loadModulesFromDirectory(String globalPath) throws BadArgumentsException, IOException, ParseException, InvalidModException, AlreadyAddedVersionException, DependencyNotFoundException, NoMainModuleException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CyclicDependencyDetectedException;

    /**
     * Run the program. The method first look at dependences (check and resolve them), then search the main module and its main class. Finally, it launchs the main class.
     * @throws DependenceNotFoundException
     * @throws NoMainModuleException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public void run() throws DependencyNotFoundException, NoMainModuleException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CyclicDependencyDetectedException;

    /**
     * Return the main class if founded in the loaded modules. Else, return null.
     */
    public Class getMainClass();
}
