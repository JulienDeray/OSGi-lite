package com.serli.jderay.modules.core;

import com.serli.jderay.modules.Module;
import com.serli.jderay.services.Services;
import java.net.URL;
import java.net.URLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author julien
 */
public class ModuleClassLoader extends URLClassLoader {

    private ModuleVisitor visitor;
    private static final Logger logger = LoggerFactory.getLogger(ModuleClassLoader.class);
    
    public ModuleClassLoader(URL url, ModuleVisitor visitor) {
        super(new URL[] { url });
        this.visitor = visitor;
    }

    public ModuleClassLoader(URL[] urls) {
        super(urls);
    }
    
    @Override
    public Class loadClass( String name ) throws ClassNotFoundException {
        logger.debug("{} : Loading class  {}", visitor.visitName(), name);
        
        if ( findLoadedClass( name ) != null )
            return findLoadedClass( name );

        else if ( Services.isPublished( name ) ) {
            return Services.getClass( name );
        }
            
        else if ( isJDKClass( name ) )
            return this.getClass().getClassLoader().loadClass( name );

        else if ( isInCurrentModule( name ) )
            return super.loadClass( name );

        else if ( canBeLoadedFromDep( name ) )
            return loadFromDep( name );

        else
            throw new ClassNotFoundException();
    }
    
    private boolean isJDKClass( String name ) {
        if ( name.startsWith( "java." )
            || name.startsWith( "javax." )
            || name.startsWith( "sun." )
            || name.startsWith( "sunw." )
            || name.startsWith( "com.sun." )
            || name.startsWith( "org.w3c.dom." ) )
            return true;
        else
            return false;
    }
    
    public boolean isInCurrentModule( String name ) throws ClassNotFoundException {

        try {
            super.loadClass(name);
            return true;
        } catch ( ClassNotFoundException e ) {
            return false;
        } 
    }
    
    private boolean canBeLoadedFromDep( String name ) throws ClassNotFoundException {
        if ( isInCurrentModule( name ) )
            return true;
        else
            for ( Module mod : visitor.visitDependencies().values() ) {
                if ( mod.getClassLoader().isInCurrentModule(name) )
                    return true;
            }
        return false;
    }
    
    private Class loadFromDep( String name ) throws ClassNotFoundException {
        for ( Module mod : visitor.visitDependencies().values() ) {
            if ( mod.getClassLoader().isInCurrentModule(name) )
                return mod.getClassLoader().loadClass( name );
        }
        throw new ClassNotFoundException();
    }
}
