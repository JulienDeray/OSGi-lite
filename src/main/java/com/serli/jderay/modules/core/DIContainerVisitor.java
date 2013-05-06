/**
 * @author julien
 */

package com.serli.jderay.modules.core;

import com.serli.jderay.jsr330.DIContainer;
import com.serli.jderay.modules.impl.Modules;


public class DIContainerVisitor {

    Modules modules;
    
    public DIContainerVisitor(Modules modules) {
        this.modules = modules;
    }
    
    public DIContainer visitDIC() {
        return modules.getDiContainer();
    }

}
