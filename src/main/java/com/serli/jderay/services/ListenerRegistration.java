/**
 * @author julien
 */

package com.serli.jderay.services;


public class ListenerRegistration<T, K> {

    private final ServiceListener<T> listener;
    private final Class<T> serviceClass;

    public ListenerRegistration(ServiceListener<T> listener, Class<T> serviceClass) {
        this.listener = listener;
        this.serviceClass = serviceClass;
    }

    public ServiceListener getListener() {
        return listener;
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }

}
