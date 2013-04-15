package core;


import exceptions.InvalidModException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * @author julien
 */

class Module extends URLClassLoader {

    private String name;
    private String version;
    private String mainClass;
    private Map<String, Module> dependences;
    
    private File jarFile;
    private File modFile;

    public Module(URL url) throws IOException {
        super(new URL[] { url });
        String modulePath = url.getPath().substring(5, url.getPath().length() - 6);
        
        this.dependences = new HashMap<String, Module>();
        
        loadFiles(modulePath); 
        scanModFile(modulePath);
    }

    private void loadFiles(String modulePath) {
        
        try{
            jarFile = new File(modulePath + ".jar");
            modFile = new File(modulePath + ".mod");
        }
        catch(Exception e) {
            System.err.println("Le module " + modulePath + " n'existe pas.");
        }
    }
    
    private String nullPrevent(String att) {
        
        try {
            if (!"null".equals(att))
                return att;
            else
                throw new InvalidModException();
        }
        catch(Exception e) {
            System.err.println("Invalid .mod");
        }
        return null;
    }

    private void scanModFile(String modulePath) {
        JSONParser parser = new JSONParser();
 
        try {
            Object obj = parser.parse(new FileReader( modFile ));

            JSONObject jsonObject = (JSONObject) obj;

            // String fields
            this.name = nullPrevent( (String) jsonObject.get("name") );
            this.version = nullPrevent( (String) jsonObject.get("version") );
            this.mainClass = (String) jsonObject.get("mainClass");

            // Dependencies
            JSONArray dependenciesJson = (JSONArray) jsonObject.get("dependencies");
            Iterator<String> iterator = dependenciesJson.iterator();
            while (iterator.hasNext()) {
                String nameAndVersion = iterator.next();
                String name = nameAndVersion.substring(0, nameAndVersion.indexOf(":"));
                String version = nameAndVersion.substring(nameAndVersion.indexOf(":") + 1, nameAndVersion.length());
                
                this.dependences.put(name + ":" + version, null);
            }
 
        } catch (FileNotFoundException e) {
            System.err.println("Le module " + modulePath + " n'existe pas.");
        } catch (IOException e) {
            System.err.println("Le module " + modulePath + " n'existe pas.");
        } catch (ParseException e) {
            System.err.println("Erreur de parsing sur le .mod de " + modulePath);
        }
    }
    
    private boolean checkVersion(Module dep, String expectedVersion) {
        if ( dep.getVersion().equals(expectedVersion) )
            return true;
        else
            return false;
    }
    
    public void addDependence( Module mod ) {
        this.dependences.put( mod.name + ":" + mod.version, mod);
    }
   
    public String run() {
        return name;
    }
    
    public void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class c = loadClass(name);
        System.out.println( c.getName() );
        Method m = c.getMethod("main", new Class[]{args.getClass()});
        m.setAccessible(true);
        int mods = m.getModifiers();
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");
        }
        try {
            m.invoke(null, new Object[]{args});
        } catch (IllegalAccessException e) {
        }
    }
    
    /*
     * Getters
     */
    public File getJarFile() {
        return jarFile;
    }

    public Map<String, Module> getDependenciesNames() {
        return dependences;
    }

    public String getMainClass() {
        return mainClass;
    }

    public File getModFile() {
        return modFile;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return this.name + ":" + this.version;
    }

}
