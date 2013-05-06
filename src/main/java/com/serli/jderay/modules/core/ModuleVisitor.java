/**
 * @author julien
 */

package com.serli.jderay.modules.core;

import com.serli.jderay.modules.Module;
import java.util.Map;


public class ModuleVisitor {

    private Module module;
    
    public ModuleVisitor(Module aModule) {
        this.module = aModule;
    }
    
    public Map<String, Module> visitDependencies() {
        return this.module.getDependencies();
    }
    
    public String visitName() {
        return this.module.toString();
    }
    
    public DIContainerVisitor visitDIContainerVisitor() {
        return this.module.getDIVisitor();
    }

    public Module visitModule() {
        return this.module;
    }
    
}
