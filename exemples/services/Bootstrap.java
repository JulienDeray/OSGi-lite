package com.serli.jderay.testapi;

import com.serli.jderay.modules.ModuleManager;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.DependencyException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import com.serli.jderay.modules.exceptions.MainModuleException;
import com.serli.jderay.modules.impl.Modules;
import com.serli.jderay.moduletest1.NameService;
import com.serli.jderay.moduletest1.NameServiceImpl;
import com.serli.jderay.moduletest2.Name2Service;
import com.serli.jderay.moduletest2.Name2ServiceImpl;
import com.serli.jderay.services.ListenerRegistration;
import com.serli.jderay.services.Registration;
import com.serli.jderay.services.ServiceListener;
import com.serli.jderay.services.Services;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.json.simple.parser.ParseException;

/**
 * @autor
 *
 */
public class Bootstrap  {
    
    public static void main( String[] args ) throws BadArgumentsException, IOException, ParseException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, DependencyException, MainModuleException, InvalidModException {
        Bootstrap app = new Bootstrap();
    }

    public Bootstrap() throws BadArgumentsException, IOException, ParseException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, DependencyException, MainModuleException, InvalidModException {
        testLoadSeparatly();
    }

    private void testLoadSeparatly() throws IOException, ParseException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, DependencyException, MainModuleException, InvalidModException {
        ModuleManager mm = new Modules();
        mm.loadModule("/moduleTest1/target/moduleTest1-1.0-SNAPSHOT");
        mm.loadModule("/ModuleTest2/target/ModuleTest2-1.0-SNAPSHOT");
        mm.loadModule("/ModuleTest3/target/ModuleTest3-1.0-SNAPSHOT");

        initModule1();
        initModule2();
        
        mm.run();
    }
    
    
    private NameService nameService;
    private ListenerRegistration<NameService, NameServiceImpl> listenerReg;
    private Registration<NameService> serviceRegistration;
    
    private void initModule1() {
        nameService = new NameServiceImpl();
        listenerReg = Services.listenTo( NameService.class ).with(new ServiceListener<NameService>() {

        @Override
        public void registered(NameService service) {
            System.out.println( "\"" + service.toString() + "\" registred hombre !" );
        }

        @Override
        public void unregistered(NameService service) {
            System.out.println( "Bye bye !! -> " + service.toString() );
        }
        });
        
        serviceRegistration = Services.publish( NameService.class, nameService );
    }
  
    
    private Name2Service name2Service;
    private ListenerRegistration<Name2Service, Name2ServiceImpl> listenerReg2;
    private Registration<Name2Service> serviceRegistration2;
    
    private void initModule2() {
        name2Service = new Name2ServiceImpl();
        listenerReg2 = Services.listenTo( Name2Service.class ).with(new ServiceListener<Name2Service>() {

        @Override
        public void registered(Name2Service service) {
            System.out.println( "\"" + service.toString() + "\" registred hombre !");
        }

        @Override
        public void unregistered(Name2Service service) {
            System.out.println("Bye bye !! -> " + service.toString());
        }
        });
        
        serviceRegistration2 = Services.publish( Name2Service.class, name2Service );
    }
    
}
