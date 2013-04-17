package core;


import exceptions.AllreadyAddedVersionException;
import exceptions.BadArgumentsException;
import exceptions.DependenceNotFoundException;
import exceptions.InvalidModException;
import exceptions.NoMainModuleException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;

/**
 * @author julien
 * 
 * Utilisation en full programatic :
 *      Modules modules = new Modules("/Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/", "module2-1.0-SNAPSHOT", "module1-2.0-SNAPSHOT", "module1-1.0-SNAPSHOT", "module3-1.0-SNAPSHOT");
 *
 * Utilisation en ligne de commande :
 *      java -jar modules.jar -mp /Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/ module2-1.0-SNAPSHOT module1-2.0-SNAPSHOT module1-1.0-SNAPSHOT module3-1.0-SNAPSHOT
 */
public class Modules {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependenceNotFoundException, AllreadyAddedVersionException, NoMainModuleException, BadArgumentsException {
        
        if ( !args[0].equals( "-mp" ) )
            throw new BadArgumentsException();
        
        String[] modulesPaths = new String[ args.length - 2 ];
        for (int i = 2; i < args.length; i++)
            modulesPaths[i-2] = args[i];
        
        Modules modules = new Modules(args[1], modulesPaths);
    }
    
    private String path;
    private Map<String, Module> listModules;
    //      Name:Version

    public Modules(String path, String ... modulesToLoad) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependenceNotFoundException, AllreadyAddedVersionException, NoMainModuleException {
        this.listModules = new HashMap<String, Module>();
        this.path = path;
        String[] args = {};

        for (String module : modulesToLoad)
            loadModule( module );

        setDependenciesGlobal();
        displayDependencies();

        Module mainModule = findMainModule();
        String mainClassName = mainModule.getMainClass();
        
        mainModule.invokeClass( mainClassName, args );
    }
    
    private void displayDependencies() {
        System.out.println("--- Dependencies ---");
        for (Map.Entry pairs : listModules.entrySet()) {
            Module mod = (Module) pairs.getValue();
            System.out.println(mod + " -> " + mod.getDependenciesNames());
        }
        System.out.println("\n");
    }

    private Module loadModule(String fileName) throws IOException, ParseException, InvalidModException, AllreadyAddedVersionException {
        URL url = new URL("jar:file:" + path + fileName + ".jar!/");
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
