/**
 * @author julien
 */

package com.serli.jderay.jsr330;

import com.serli.jderay.modules.Module;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

    
public class DIContainer {

    private ClassScanner classScanner;
    private InheritanceAnalyser inheritanceAnalyser;
    
    public DIContainer() {
        inheritanceAnalyser = new InheritanceAnalyser();
        classScanner = new ClassScanner();
    }

    public void scan(Map<String, Module> listModules) throws MalformedURLException {
        Map<String, Class<?>> classes;
        Map<String, Class<?>[]> inheritances;
    
        classes = getClasses( listModules );
        inheritances = inheritanceAnalyser.analyse( classes );
    }

    private Map<String, Class<?>> getClasses(Map<String, Module> listModules) throws MalformedURLException {
        Map<URL, ClassLoader> urls = new HashMap<>();
        for ( Module mod : listModules.values() ) {
            urls.put( mod.getUrl(), mod.getClassLoader() );
        }
        
        return classScanner.scanClasses(urls);
    }
   
}
