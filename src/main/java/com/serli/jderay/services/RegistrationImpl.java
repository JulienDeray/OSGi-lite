/**
 * @author julien
 */

package com.serli.jderay.services;

import com.serli.jderay.services.exceptions.PublicationException;


public class RegistrationImpl implements Registration {

    Class myClass;
    
    public RegistrationImpl(Class serviceClass) {
        myClass = serviceClass;
    }

    @Override
    public void unregister() throws PublicationException {
        Services.unregister(myClass);        
    }

}
