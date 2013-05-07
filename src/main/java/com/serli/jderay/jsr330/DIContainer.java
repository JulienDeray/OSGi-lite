/**
 * @author julien
 */

package com.serli.jderay.jsr330;

import com.serli.jderay.modules.Module;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

    
public class DIContainer {

    private ClassScanner classScanner;
    private InheritanceAnalyser inheritanceAnalyser;
    
    private Map<String, Class<?>> classes;
    private Map<String, Class<?>[]> inheritances;
    private boolean initialized;
    
    public DIContainer() {
        initialized =  false;
        classes = new HashMap<>();
        inheritances = new HashMap<>();
        
        inheritanceAnalyser = new InheritanceAnalyser();
        classScanner = new ClassScanner();
    }

    public void init(Map<String, Module> listModules) throws IOException, URISyntaxException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        scanModules( listModules );
        inheritances = inheritanceAnalyser.analyse( classes );
        initialized = true;
    }

    private void scanModules(Map<String, Module> listModules) throws MalformedURLException {
        Map<URL, ClassLoader> urls = new HashMap<>();
        for ( Module mod : listModules.values() )
            urls.put( mod.getUrl(), mod.getClassLoader() );
        
        classes = classScanner.scanClasses(urls);
    }
}
