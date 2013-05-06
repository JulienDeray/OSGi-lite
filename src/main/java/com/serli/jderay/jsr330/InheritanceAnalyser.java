/**
 * @author julien
 */

package com.serli.jderay.jsr330;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class InheritanceAnalyser {

    private static final String[] EMPTY_STRING_ARRAY = new String[] {};
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
    private static final Logger logger = LoggerFactory.getLogger(InheritanceAnalyser.class);
    
    /**
     * Simply maps the interfaces or abstract classes to their implementations.
     */
    Map<String, Class<?>[]> analyse(Map<String, Class<?>> classes) {
        Map<String, Class<?>[]> inheritances = new TreeMap<>();
        List<Class<?>> classList = new ArrayList<>();
        String[] classNames = classes.keySet().toArray(EMPTY_STRING_ARRAY);
        Class<?> type;
        
        for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
            classList.clear();
            logger.debug("analyse - looking for {}", entry.getKey());
            for (String className : classNames) {
                if (!entry.getKey().equals(className)) {
                    type = classes.get(className);
                    if (entry.getValue().isAssignableFrom(type) && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                        classList.add(type);
                        logger.debug("analyse - FOUND {}", type);
                    }
                }
            }
            if (!classList.isEmpty()) {
                inheritances.put(entry.getKey(), classList.toArray(EMPTY_CLASS_ARRAY));
            }
        }
        
        return inheritances;
    }
    
}
