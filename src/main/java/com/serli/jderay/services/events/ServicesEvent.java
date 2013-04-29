/**
 * @author julien
 */

package com.serli.jderay.services.events;


public class ServicesEvent<T, K> {

    private Class<T> serviceClass;
    private K service;

    public ServicesEvent(Class<T> serviceClass, K service) {
        this.serviceClass = serviceClass;
        this.service = service;
    }

    public K getService() {
        return service;
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }
    
}
