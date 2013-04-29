/**
 * @author julien
 */

package com.serli.jderay.services.events;


public class RegistrationEvent extends ServicesEvent {

    public RegistrationEvent(Class serviceClass, Object service) {
        super(serviceClass, service);
    }
    
}
