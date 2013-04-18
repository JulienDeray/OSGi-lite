package core;


import exceptions.AllreadyAddedVersionException;
import exceptions.BadArgumentsException;
import exceptions.DependenceNotFoundException;
import exceptions.InvalidModException;
import exceptions.NoMainModuleException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;
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
    
    private Map<String, Module> listModules;
    //      Name:Version

    public Modules(String ... modulesToLoad) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependenceNotFoundException, AllreadyAddedVersionException, NoMainModuleException {
        this.listModules = new HashMap<String, Module>();
        String[] args = {};

        for (String module : modulesToLoad)
            loadModule( module );

        setDependenciesGlobal();
        displayDependencies();

        Module mainModule = findMainModule();
        String mainClassName = mainModule.getMainClass();
        
        mainModule.invokeMain( mainClassName, args );
    }
  
    private void displayDependencies() {
        System.out.println("--- Dependencies ---");
        for (Map.Entry pairs : listModules.entrySet()) {
            Module mod = (Module) pairs.getValue();
            System.out.println(mod + " -> " + mod.getDependenciesNames());
        }
        System.out.println("\n");
    }

    private Module loadModule(String path) throws IOException, ParseException, InvalidModException, AllreadyAddedVersionException {
        URL url = new URL("jar:file:" + path + ".jar!/");
        Module mod = new Module( url );
        addToMap(mod);
        return mod;
    }

    private void setDependencesLocal(Module mod) throws DependenceNotFoundException {
        Map<String, Module> modDependenciesNames = mod.getDependenciesNames();

        for (Map.Entry pairs : modDependenciesNames.entrySet()) {
            String modCode = (String) pairs.getKey();
            if (this.listModules.containsKey(modCode)) {
                mod.addDependence(this.listModules.get(modCode));
            } else {
                throw new DependenceNotFoundException( modCode );
            }
        }
    }

    private void setDependenciesGlobal() throws DependenceNotFoundException {
        for (Map.Entry pairs : listModules.entrySet()) {
            setDependencesLocal( (Module) pairs.getValue() );
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
