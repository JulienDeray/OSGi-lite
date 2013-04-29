/**
 * @author julien
 */

package com.serli.jderay.services;


public class RegistrationImpl implements Registration {

    Class myClass;

    public RegistrationImpl(Class serviceClass) {
        myClass = serviceClass;
    }

    @Override
    public void unregister() {
        Services.unregister(myClass);        
    }

}
