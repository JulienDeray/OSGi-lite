/**
 * @author julien
 */

package com.serli.jderay.services;

import com.serli.jderay.services.events.RegistrationEvent;
import com.serli.jderay.services.events.ServicesEvent;
import com.serli.jderay.services.events.UnregistrationEvent;
import com.serli.jderay.services.exceptions.MoreThanOneInstancePublishedException;
import com.serli.jderay.services.exceptions.NotPublishedInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Services {
    
    private static final Map<Class, ListenerRegistration<?, ?>> listListeners = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Services.class);
    
    /*
     * Publish a service. It then will be available from another module. 
     * @param serviceClass Interface of the service.
     * @param service Implementation of the service.
     * @return Registration<TheService> : this object will allow to manage the registration (unpublish, etc ...).
     */
    public static <T, K extends T> RegistrationImpl publish( Class<T> serviceClass, K service ) throws MoreThanOneInstancePublishedException, NotPublishedInstance {
        if ( SIContainer.containsClass(serviceClass) ) {
            List<K> listK = (List<K>) SIContainer.get( serviceClass );
            listK.add( service );
        }
        else {
            List<K> srv = new ArrayList<>();            
            srv.add( service );
            SIContainer.put(serviceClass, srv);
        }
        
        logger.debug("--------------------- Publication : {} ---------------------", serviceClass.getName());
        fire( new RegistrationEvent(serviceClass, service) );
        return new RegistrationImpl( serviceClass );
    }
    
    static <T, K extends T> void unregister(Class<T> classToUnregister) throws MoreThanOneInstancePublishedException, NotPublishedInstance {
        List<K> services = (List<K>) SIContainer.get( classToUnregister );
        for ( K service : services ) {
            fire( new UnregistrationEvent( classToUnregister, service ) );
        }
        SIContainer.remove( classToUnregister );
        logger.debug("--------------------- Unregistration : {} ---------------------", classToUnregister.getName());
    }
    
    /*
     * Add a listener to the specified service.
     * @param serviceClass Interface of the service.
     */
    public static <T, K extends T> ListenableService<T> listenTo( Class<T> serviceClass ) {
        logger.debug("--------------------- Listener added to : {} ---------------------", serviceClass.getName());
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

    public static <T> T get(Class<T> clazz) throws MoreThanOneInstancePublishedException, NotPublishedInstance {
        return SIContainer.get( clazz );
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

}
