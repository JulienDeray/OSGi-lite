/**
 * @author julien
 */

package com.serli.jderay.modules.exceptions;


public class CyclicDependencyDetectedException extends DependencyException {
    
    public CyclicDependencyDetectedException(String message) {
        super(message);
    }
    
    
}
