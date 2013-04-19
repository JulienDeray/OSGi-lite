/**
 * @author julien
 */

package com.serli.jderay.modules.exceptions;


public class CyclicDependencyDetectedException extends Exception {
    
    public CyclicDependencyDetectedException(String message) {
        super(message);
    }
    
    
}
