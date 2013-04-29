/**
 * @author julien
 */

package com.serli.jderay.services;


public interface Registration<T> {
    
    /*
     * Unregister the service of the register. It will not be available from others modules.
     */
    public void unregister();

}
