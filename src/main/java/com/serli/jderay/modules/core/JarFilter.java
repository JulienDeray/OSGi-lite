/**
 * @author julien
 */

package com.serli.jderay.modules.core;

import java.io.File;
import java.io.FilenameFilter;


public class JarFilter implements FilenameFilter {
    
    @Override
    public boolean accept(File dir, String name) {
        if (name.toLowerCase().endsWith(".zip"))
            return (name.toLowerCase().endsWith(".zip"));
        
        else
            return (name.toLowerCase().endsWith(".jar"));
    }
}
