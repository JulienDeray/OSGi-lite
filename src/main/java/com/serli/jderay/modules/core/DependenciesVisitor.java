/**
 * @author julien
 */

package com.serli.jderay.modules.core;

import com.serli.jderay.modules.Module;
import java.util.Map;


public class DependenciesVisitor {

    private Module module;
    
    public DependenciesVisitor(Module aModule) {
        this.module = aModule;
    }
    
    public Map<String, Module> visitDependencies() {
        return this.module.getDependenciesNames();
    }
    
    public String visitName() {
        return this.module.toString();
    }

}
