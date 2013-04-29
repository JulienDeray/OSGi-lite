/**
 * @author julien
 */

package com.serli.jderay.services;


public interface ServiceListener<T> {

    /*
     * Define a behaviour when the specified service will be registred
     */
    public void registered(T serviceClass);
    
    /*
     * Define a behaviour when the specified service will be unregistred
     */
    public void unregistered(T serviceClass);
}
