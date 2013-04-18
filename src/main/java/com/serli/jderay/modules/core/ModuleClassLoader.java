package com.serli.jderay.modules.core;

import com.serli.jderay.modules.Module;
import java.net.URL;
import java.net.URLClassLoader;


/**
 *
 * @author julien
 */
public class ModuleClassLoader extends URLClassLoader {

    private DependenciesVisitor visitor;
    
    public ModuleClassLoader(URL url, DependenciesVisitor visitor) {
        super(new URL[] { url });
        this.visitor = visitor;
    }

    @Override
    public Class loadClass( String name ) throws ClassNotFoundException {
        
        if ( findLoadedClass( name ) != null )
            return findLoadedClass( name );

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
            || name.startsWith( "org.w3c.dom" ) )
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
            for ( Module mod : visitor.visit().values() ) {
                if ( mod.getClassLoader().isInCurrentModule(name) )
                    return true;
            }
        return false;
    }
    
    private Class loadFromDep( String name ) throws ClassNotFoundException {
        for ( Module mod : visitor.visit().values() ) {
            if ( mod.getClassLoader().isInCurrentModule(name) )
                return mod.getClassLoader().loadClass( name );
        }
        throw new ClassNotFoundException();
    }
}
