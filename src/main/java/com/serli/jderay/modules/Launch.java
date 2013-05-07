/**
 * @author julien
 */

package com.serli.jderay.modules;

import com.serli.jderay.modules.core.JarFilter;
import com.serli.jderay.modules.exceptions.BadArgumentsException;
import com.serli.jderay.modules.exceptions.DependencyException;
import com.serli.jderay.modules.exceptions.InvalidModException;
import com.serli.jderay.modules.exceptions.MainModuleException;
import com.serli.jderay.modules.impl.Modules;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.json.simple.parser.ParseException;


public class Launch {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, ParseException, InvalidModException, BadArgumentsException, MainModuleException, DependencyException, URISyntaxException, IllegalAccessException {
        String[] modulesPaths;
        switch (args[0]) {
            case "-mp":
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
                break;
            case "-m":
                modulesPaths = new String[ args.length - 1 ];
                for (int j = 1; j < args.length; j++)
                    modulesPaths[j-1] = args[j];
                break;
            case "-mprefix":
                if ( !args[2].equals( "-m" ) )
                    throw new BadArgumentsException();
                modulesPaths = new String[ args.length - 3 ];
                for (int k = 3; k < args.length; k++)
                    modulesPaths[k-3] = args[1] + args[k];
                break;
            default:
                throw new BadArgumentsException();
        }
        
        ModuleManager modules = new Modules(modulesPaths);
    }
}
