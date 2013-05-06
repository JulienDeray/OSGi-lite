/**
 * @author julien
 */

package com.serli.jderay.services;

import com.serli.jderay.jsr330.DIContainer;
import com.serli.jderay.services.exceptions.MoreThanOneInstancePublishedException;
import com.serli.jderay.services.exceptions.NotPublishedInstance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Service Injection Container
 */
class SIContainer {
    
    private static final Map<Class, List<?>> listServices = new HashMap<>();
    
    /*
     * Getter for registred services.
     * @param serviceClass Interface of the service you want to get the implementation.
     * @return The service implementation if one and only one is available.
     * @throws MoreThanOneInstancePublishedException
     */
    static <T> T get(Class<T> serviceClass) throws MoreThanOneInstancePublishedException, NotPublishedInstance {
        if ( listServices.containsKey( serviceClass ) ) {
            List<?> services = listServices.get( serviceClass );
            if ( services.size() == 1 )
                return (T) services.get(0);
            else
                throw new MoreThanOneInstancePublishedException();
        }
        else
            throw new NotPublishedInstance( serviceClass.getName() );
    }
    
    /*
     * Getter for registred services.
     * @param serviceClass Interface of the service you want to get the implementation.
     * @return A iterable list of the implementation of the specified service.
     */
    static <T> Iterable<T> getAll(Class<T> serviceClass) {
        return (Iterable<T>) listServices.get( serviceClass );
    }
    
    static <T> void remove(Class<T> classToUnregister) {
        listServices.remove( classToUnregister );
    }
    
    static <T> boolean containsClass( Class<T> clazz ) {
        return listServices.containsKey(clazz);
    }

    static <T, K extends T> void put(Class<T> serviceClass, List<K> srv) {
        listServices.put(serviceClass, srv);
    }

}
