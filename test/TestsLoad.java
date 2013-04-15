/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author julien
 */
public class TestsLoad {
    
    public TestsLoad() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /*@Test
    public void testScanMod() {
        Module mod1 = new Module("/Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/mod1");
        
        assertEquals( mod1.getName(), "mod1" );
        assertEquals( mod1.getVersion(), "v1.0" );
        assertEquals( mod1.getMainClass(), "Speak" );
        
        assertEquals( mod1.getDependenciesNames().get("mod2"), "v1" );
        assertEquals( mod1.getDependenciesNames().get("mod3"), "v2" );
    }
    
    @Test
    public void testLoaddependencies() {
        Module mod1 = new Module("/Users/julien/Serli/Weld-OSGI/ConteneurModulaire/Modules/modules/mod1");
        
        assertEquals( mod1.getDependenciesFiles().get(0).toString(), "mod2:v1" );
        assertEquals( mod1.getDependenciesFiles().get(1).toString(), "mod3:v2" );
    }*/
}