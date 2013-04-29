/**
 * @author julien
 */

package com.serli.jderay.services;


public interface ServiceListener<T> {

    public void registered(T serviceClass);
    public void unregistered(T serviceClass);
}
