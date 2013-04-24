/**
 * @author julien
 */

package com.serli.jderay.modules.exceptions;


public class MultipleMainModulesFoundedException extends MainModuleException {

    public MultipleMainModulesFoundedException(String message) {
        super(message);
    }

}
