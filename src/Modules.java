
import exceptions.AllreadyAddedVersionException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author julien
 */
public class Modules {

    public static void main(String[] args) {
        Modules modules = new Modules("/Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/");
    }
    private String path;
    private Map<String, Module> listModules;
    //     Name:Version

    public Modules(String path) {
        this.listModules = new HashMap<String, Module>();
        this.path = path;

        Module mod2_1 = loadModule("module2-1.0-SNAPSHOT");
        Module mod1_2 = loadModule("module1-2.0-SNAPSHOT");
        Module mod1_1 = loadModule("module1-1.0-SNAPSHOT");
        Module mod3_1 = loadModule("module3-1.0-SNAPSHOT");

        setDependenciesGlobal();
        displayDependencies();

        mod2_1.run();
    }

    private void displayDependencies() {
        for (Map.Entry pairs : listModules.entrySet()) {
            Module mod = (Module) pairs.getValue();
        }
    }

    private Module loadModule(String fileName) {
        Module mod = new Module(path + fileName);
        addToMap(mod);
        return mod;
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
}
