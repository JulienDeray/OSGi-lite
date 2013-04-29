/**
 * @author julien
 */

package com.serli.jderay.services;

import com.serli.jderay.services.events.RegistrationEvent;
import com.serli.jderay.services.events.ServicesEvent;
import com.serli.jderay.services.events.UnregistrationEvent;
import com.serli.jderay.services.exceptions.MoreThanOneInstancePublishedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Services {
    
    private static final Map<Class, List<?>> listServices = new HashMap<>();
    private static final Map<Class, ListenerRegistration<?, ?>> listListeners = new HashMap<>();

    /*
     * Publish a service. It then will be available from another module. 
     * @param serviceClass Interface of the service.
     * @param service Implementation of the service.
     * @return Registration<TheService> : this object will allow to manage the registration (unpublish, etc ...).
     */
    public static <T, K extends T> RegistrationImpl publish( Class<T> serviceClass, K service ) {
        if ( listServices.containsKey(serviceClass) ) {
            List<K> listK = (List<K>) listServices.get( serviceClass );
            listK.add( service );
        }
        else {
            List<K> srv = new ArrayList<>();            
            srv.add( service );
            listServices.put(serviceClass, srv);
        }
        
        fire( new RegistrationEvent(serviceClass, service) );
        return new RegistrationImpl( serviceClass );
    }
    
    /*
     * Add a listener to the specified service.
     * @param serviceClass Interface of the service.
     */
    public static <T, K extends T> ListenableService<T> listenTo( Class<T> serviceClass ) {
        return new ListenableService<>( serviceClass );
    }

    private static void fire(ServicesEvent servicesEvent) {
        if ( listListeners.containsKey( servicesEvent.getServiceClass() )) {
            if ( servicesEvent instanceof RegistrationEvent ) {
                listListeners.get( servicesEvent.getServiceClass() ).getListener().registered( servicesEvent.getService() );
            }
            else if ( servicesEvent instanceof UnregistrationEvent ) {
                listListeners.get( servicesEvent.getServiceClass() ).getListener().unregistered( servicesEvent.getService() );
            }
        }
    }
    
    public static class ListenableService<T> {
        public final Class<T> clazz;

        public ListenableService(Class<T> clazz) {
            this.clazz = clazz;
        }
        
        /*
         * Implements the actions.
         */
        public <K extends T> ListenerRegistration<T, K> with(ServiceListener<T> listener) {
             ListenerRegistration<T, K> reg = new ListenerRegistration<>(listener, clazz);
             Services.addListener(reg);
             return reg;
        }
    }
    
    static void addListener( ListenerRegistration<?, ?> listener ) {
        listListeners.put( listener.getServiceClass(), listener );
    }

    /*
     * Getter for registred services.
     * @param serviceClass Interface of the service you want to get the implementation.
     * @return The service implementation if one and only one is available.
     * @throws MoreThanOneInstancePublishedException
     */
    public static <T> T get(Class<T> serviceClass) throws MoreThanOneInstancePublishedException {
        List<?> services = listServices.get( serviceClass );
        if ( services.size() == 1 )
            return (T) services.get(0);
        else
            throw new MoreThanOneInstancePublishedException();
    }

    /*
     * Getter for registred services.
     * @param serviceClass Interface of the service you want to get the implementation.
     * @return A iterable list of the implementation of the specified service.
     */
    public static <T> Iterable<T> getAll(Class<T> serviceClass) {
        return (Iterable<T>) listServices.get( serviceClass );
    }
    
    static <T, K extends T> void unregister(Class<T> classToUnregister) {
        List<K> services = (List<K>) listServices.get( classToUnregister );
        for ( K service : services ) {
            fire( new UnregistrationEvent( classToUnregister, service ) );
        }
        listServices.remove( classToUnregister );
    }

}
