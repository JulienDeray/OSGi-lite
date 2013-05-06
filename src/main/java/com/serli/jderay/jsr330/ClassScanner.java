/**
 * @author julien
 */

package com.serli.jderay.jsr330;

import com.serli.jderay.modules.Module;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class ClassScanner {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
    private static final Pattern FILE_SEPARATOR_REGEX = Pattern.compile("\\" + System.getProperty("file.separator"));
    private static final Pattern ENTRY_SEPARATOR_REGEX = Pattern.compile("/");
    
    private Map<String, Class<?>> classes = new HashMap<>();

    public ClassScanner() {
        classes = new HashMap<>();
    }
    
    Map<String, Class<?>> scanModule( Module module ) throws MalformedURLException {
        Map<URL, ClassLoader> urls = new HashMap<>();
        urls.put(module.getUrl(), module.getClassLoader());
        return scanClasses(urls);
    }
    
    Map<String, Class<?>> scanClasses(Map<URL, ClassLoader> urls) throws MalformedURLException {
        classes.clear();
        String tmp;
        URI uri;
        File file;
        for ( URL url : urls.keySet() ) {
            tmp = url.toExternalForm();
            try {
                if (tmp.startsWith("file:")) {
                    uri = new URI(tmp);
                    file = new File(uri);
                    traverseFile(file.getAbsolutePath(), file, urls.get(url), classes);
                } else if (tmp.startsWith("jar:")) {
                    uri = new URI(tmp.substring(4, tmp.length() - 2));
                    file = new File(uri);
                    traverseJar(file.getAbsolutePath(), file.toURI().toURL(), urls.get(url), classes);
                }
            } catch (URISyntaxException exception) {
                logger.error("!! Error while generating URI !! ", exception);
            }
        }
        
        return classes;
    }
    
    private void traverseJar(String base, URL url, ClassLoader loader, Map<String, Class<?>> classes) {
        JarInputStream stream = null;
        JarEntry entry;
        String name;
        
        try {
            stream = new JarInputStream(url.openStream());
            while ((entry = stream.getNextJarEntry()) != null) {
                if ((name = entry.getName()).endsWith(".class")) {
                    name = ENTRY_SEPARATOR_REGEX.matcher(name).replaceAll(".");
                    name = name.substring(0, name.length() - 6);
                    
                    createClass(loader, classes, name);
                }
            }
        } catch (Exception exception) {
            logger.error("!! Error while traversing jar !!", exception);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException exception) {
                    logger.error("!! Error while closing stream !!", exception);
                }
            }
        }
    }
    
    private void traverseFile(String base, File file, ClassLoader loader, Map<String, Class<?>> classes) {
        String name;
        
        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                traverseFile(base, tmp, loader, classes);
            }
        } else if ((name = file.getAbsolutePath()).endsWith(".class")) {
            name = name.substring(base.length() + 1);
            name = FILE_SEPARATOR_REGEX.matcher(name).replaceAll(".");
            name = name.substring(0, name.length() - 6);
            
            createClass(loader, classes, name);
        }
    }
    
    private void createClass(ClassLoader loader, Map<String, Class<?>> classes, String name) {
        try {
            classes.put(name, Class.forName(name, false, loader));
        } catch (Throwable exception) {
            logger.error("! Error while loading class !!", exception);
        }
    }
}
