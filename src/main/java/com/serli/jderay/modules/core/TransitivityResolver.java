/**
 * @author julien
 */

package com.serli.jderay.modules.core;

import com.serli.jderay.modules.Module;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class TransitivityResolver {
    
    private static Boolean[][] matrixAdj;
    private static Boolean[][] matrixRes;
    private static Map<String, Integer> nameToNum;
    private static Map<Integer, String> numToName;
    
    private static void displayMatrix( Boolean[][] matrix ) {
        System.out.println("------------------- MATRIX -------------------");
        for ( Boolean[] col : matrix ) {
            for ( Boolean lig : col ) {
                System.out.print(lig + " ");
            }
            System.out.println("\n");
        }
        System.out.println("----------------------------------------------");
    }
    
    public static Map<String, Module> resolve( Map<String, Module> listModules ) {
        init( listModules.values() );
        
        traceMatrix( listModules.values(), nameToNum );
        displayMatrix( matrixAdj );
        
        warshallAlgorithme( listModules.values() );
        displayMatrix( matrixRes );
        
        return makeOptimateMatrix( listModules );
    }

    private static void traceMatrix(Collection<Module> dependencies, Map<String, Integer> table) {
        for ( Module mod : dependencies ) {
            int modNumber = table.get(mod.toString());
            matrixAdj[ modNumber ][ modNumber ] = true;
            for ( Module dep : mod.getDependencies().values() ) {
                int depNumber = table.get(dep.toString());
                matrixAdj[ depNumber ][ modNumber ] = true;
                traceMatrix( mod.getDependencies().values(), table );
            }
        }
    }
    
    private static void warshallAlgorithme(Collection<Module> dependencies) {
        int n = dependencies.size();
        matrixRes = matrixAdj;
        
        for ( int k = 0; k < n; k++ )
            for ( int i = 0; i < n; i++ )
                for ( int j = 0; j < n; j++ )
                    matrixRes[i][j] = matrixRes[i][j] || ( matrixRes[i][k] && matrixRes[k][j] );
    }

    private static void init(Collection<Module> dependencies) {
        matrixAdj = new Boolean[ dependencies.size() ][ dependencies.size() ];
        numToName = new HashMap<>();
        nameToNum = new HashMap<>();
        int i = 0;
        int n = dependencies.size();
        
        for ( int j = 0; j < n; j++ ) {
            for ( int k = 0; k < n; k++ ) {
                matrixAdj[ j ][ k ] = false;
            }
        }
        
        for ( Module mod : dependencies ) {
            nameToNum.put(mod.toString(), i);
            numToName.put(i, mod.toString());
            i++;
        }
    }

    private static Map<String, Module> makeOptimateMatrix(Map<String, Module> listModules) {
        Map<String, Module> dependenciesRes = new HashMap<>();
        int n = listModules.size();
        
        for ( int j = 0; j < n; j++ ) {
            for( int i = 0; i < n; i++ ) {
                if ( matrixRes[ i ][ j ] && i != j ) {
                    String nameModI = numToName.get(i);
                    dependenciesRes.put( nameModI, listModules.get( nameModI ) );
                }
            }
            String nameModJ = numToName.get(j);
            for ( Module mod : dependenciesRes.values() )
            listModules.get( nameModJ ).setDependencies(dependenciesRes);
            dependenciesRes = new HashMap<>();
        }
        
        return listModules;
    }
}
