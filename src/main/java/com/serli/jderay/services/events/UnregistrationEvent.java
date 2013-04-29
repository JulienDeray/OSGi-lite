/**
 * @author julien
 */

package com.serli.jderay.services.events;


public class UnregistrationEvent extends ServicesEvent {

    public UnregistrationEvent(Class serviceClass, Object service) {
        super(serviceClass, service);
    }
     
}
