/**
 * @author julien
 */

package com.serli.jderay.modules;

import com.serli.jderay.modules.exceptions.AllreadyAddedVersionException;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.CyclicDependencyDetectedException;
import com.serli.jderay.modules.exceptions.DependencyNotFoundException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import com.serli.jderay.modules.exceptions.NoMainModuleException;
import com.serli.jderay.modules.impl.Modules;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import org.json.simple.parser.ParseException;
import sun.misc.JarFilter;


public class Launch {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, DependencyNotFoundException, AllreadyAddedVersionException, NoMainModuleException, BadArgumentsException, CyclicDependencyDetectedException {
        String[] modulesPaths;
        
        if ( args[0].equals( "-mp" ) ) {
            File folder = new File( args[1] );
            if ( !folder.isDirectory() )
                throw new BadArgumentsException(); 
            int i = 0;
            File[] modulesF = folder.listFiles(new JarFilter());
            modulesPaths = new String[ modulesF.length ];
            for( File pathModule : modulesF ) {
                modulesPaths[i] = pathModule.getAbsolutePath().substring(0, pathModule.getAbsolutePath().length() - 4);
                i++;
            }
        }
        else if ( args[0].equals( "-m" ) ) {
            modulesPaths = new String[ args.length - 1 ];
            for (int i = 1; i < args.length; i++)
                modulesPaths[i-1] = args[i];
        }
        else if ( args[0].equals( "-mprefix" ) ) {
            if ( !args[2].equals( "-m" ) )
                throw new BadArgumentsException();
            modulesPaths = new String[ args.length - 3 ];
            for (int i = 3; i < args.length; i++)
                modulesPaths[i-3] = args[1] + args[i];
        } 
        else {
            throw new BadArgumentsException();
        }
        
        ModuleManager modules = new Modules(modulesPaths);
    }
}
