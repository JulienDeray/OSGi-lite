package core;


import exceptions.AllreadyAddedVersionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author julien
 */
public class Modules {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Modules modules = new Modules("/Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/");
    }
    
    private String path;
    private Map<String, Module> listModules;
    //     Name:Version

    public Modules(String path) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        this.listModules = new HashMap<String, Module>();
        this.path = path;

        loadModule("module2-1.0-SNAPSHOT");
        loadModule("module1-2.0-SNAPSHOT");
        loadModule("module1-1.0-SNAPSHOT");
        loadModule("module3-1.0-SNAPSHOT");

        setDependenciesGlobal();
        displayDependencies();

        Module mainModule = findMainModule();
        String mainClassName = mainModule.getMainClass();
        
        String[] args = {};
        mainModule.invokeClass( mainClassName, args );
    }

    private void displayDependencies() {
        System.out.println("--- Dependencies ---");
        for (Map.Entry pairs : listModules.entrySet()) {
            Module mod = (Module) pairs.getValue();
            System.out.println(mod + " -> " + mod.getDependenciesNames());
        }
    }

    private Module loadModule(String fileName) throws IOException {
        try {
            //Module mod = new Module( new URL("file://" + path + fileName) );
            URL url = new URL("jar:file:" + path + fileName + ".jar!/");
            Module mod = new Module( url );
            addToMap(mod);
            return mod;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    private void setDependencesLocal(Module mod) {
        Map<String, Module> modDependenciesNames = mod.getDependenciesNames();

        for (Map.Entry pairs : modDependenciesNames.entrySet()) {
            String modCode = (String) pairs.getKey();
            if (this.listModules.containsKey(modCode)) {
                mod.addDependence(this.listModules.get(modCode));
            } else {
                System.err.println("Dependence not found ! " + modCode);
            }
        }
    }

    private void setDependenciesGlobal() {
        for (Map.Entry pairs : listModules.entrySet()) {
            setDependencesLocal((Module) pairs.getValue());
        }
    }

    private String formatKey(Module mod) {
        return mod.getName() + ":" + mod.getVersion();
    }

    private void addToMap(Module mod) {

        try {
            if (listModules.containsKey(formatKey(mod))) {
                throw new AllreadyAddedVersionException();
            } else {
                listModules.put(formatKey(mod), mod);
            }
        } catch (AllreadyAddedVersionException ex) {
            System.err.println("Allready added version");
        }
    }

    // TODO : Ne détecte pas s'il y a deux "main module"
    private Module findMainModule() {
        for ( Module mod : listModules.values() ) {
            if ( mod.getMainClass() != null ) 
                return mod;
        }
        
        return null;
    }

}
