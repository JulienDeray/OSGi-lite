package com.serli.jderay.modules;


import com.serli.jderay.modules.core.ModuleVisitor;
import com.serli.jderay.modules.core.ModuleClassLoader;
import com.serli.jderay.modules.exceptions.CyclicDependencyDetectedException;
import com.serli.jderay.modules.exceptions.DependencyException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
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

public class Module {

    private String name;
    private String version;
    private String mainClass;
    private Map<String, Module> dependencies;
    
    private File jarFile;
    private File modFile;
    
    private ModuleClassLoader classLoader;

    public Module(URL url) throws IOException, ParseException, InvalidModException {
        this.classLoader = new ModuleClassLoader( url, new ModuleVisitor( this ) );
        String modulePath = url.getPath().substring(5, url.getPath().length() - 6);
        this.dependencies = new HashMap<>();
        loadFiles(modulePath); 
        scanModFile();
    }

    private void loadFiles(String modulePath) {
        jarFile = new File(modulePath + ".jar");
        modFile = new File(modulePath + ".mod");
    }

    private void scanModFile() throws IOException, ParseException, InvalidModException {
        JSONParser parser = new JSONParser();
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
            String modName = nameAndVersion.substring(0, nameAndVersion.indexOf(":"));
            String modVersion = nameAndVersion.substring(nameAndVersion.indexOf(":") + 1, nameAndVersion.length());

            this.dependencies.put(modName + ":" + modVersion, null);
        }
    }

    private String nullPrevent(String att) throws InvalidModException {
        
        if (!"null".equals(att))
            return att;
        else
            throw new InvalidModException();
    }
    
    public void invokeMain(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class c = classLoader.loadClass(name);
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

    public void checkClyclicDependency(Module mod) throws DependencyException {
        if ( !dependencies.isEmpty() )
            for (Module dep : dependencies.values()) {
                if (dep.dependencies.containsKey( mod.toString() ))
                    throw new CyclicDependencyDetectedException("Indirect Cyclic Dependency : " + mod + " <-> " + dep);
                else
                    dep.checkClyclicDependency(mod);
            }
    }
    
    public void addDependency( Module mod ) {
        this.dependencies.put( mod.name + ":" + mod.version, mod);
    }
    
    /*
     * Getters
     */
    public File getJarFile() {
        return jarFile;
    }

    public Map<String, Module> getDependencies() {
        return dependencies;
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

    public ModuleClassLoader getClassLoader() {
        return classLoader;
    }

    public void setDependencies(Map<String, Module> dependencies) {
        this.dependencies = dependencies;
    }
    
    @Override
    public String toString() {
        return this.name + ":" + this.version;
    }
    
}
