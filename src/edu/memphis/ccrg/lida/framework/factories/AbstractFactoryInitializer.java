/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryConfig;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryObject;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaParam;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * 
 * @author Sean Kugele
 * 
 * @param <T>
 *            type of the factory to be initialized
 * 
 */
public abstract class AbstractFactoryInitializer<T extends InitializableFactory> implements
        FactoryInitializer<T> {

    private static final Logger logger = Logger.getLogger(FactoryInitializer.class
            .getCanonicalName());

    /*
     * Singleton instance of the FactoryManager -- used to retrieve factories
     * upon which this factory is depends
     */
    private FactoryManager factoryManager = DefaultFactoryManager.getInstance();

    private GlobalInitializer globalInitializer = GlobalInitializer.getInstance();

    /**
     * The {@link Factory} to be initialized
     */
    protected T factory;

    /**
     * The {@link LidaFactoryDef} that will be used to perform the
     * initialization
     */
    protected LidaFactoryDef factoryDef;

    /**
     * Constructor.
     * 
     * @param factory
     *            the {@link Factory} to be initialized
     * @param factoryDef
     *            the {@link LidaFactoryDef} that will be used referenced for
     *            initialization details
     */
    protected AbstractFactoryInitializer(T factory, LidaFactoryDef factoryDef) {
        this.factory = factory;
        this.factoryDef = factoryDef;
    }

    @Override
    public void init() {
        checkPreconditions();
        loadData();
    }

    /**
     * An abstract method that derived classes must implement. This method
     * should load the factory data details into the
     * {@link InitializableFactory} that this {@link FactoryInitializer} is
     * designed to initialize.
     */
    protected abstract void loadData();

    /**
     * Returns a reference to the {@link Factory} that will be initialized by
     * this initializer.
     * 
     * @return the {@link Factory}
     */
    protected T getFactory() {
        return factory;
    }

    /**
     * Returns the {@link LidaFactoryDef} that will be used to initialize the
     * {@link Factory} serviced by this initializer.
     * 
     * @return the {@link LidaFactoryDef}
     */
    protected LidaFactoryDef getFactoryDef() {
        return factoryDef;
    }

    protected List<LidaFactoryObject> getFactoryObjects() {
        if (factoryDef != null) {
            LidaFactoryConfig factoryConfig = factoryDef.getFactoryConfig();
            if (factoryConfig != null) {
               return factoryConfig.getFactoryObjects();
            }
        }
        
        return new ArrayList<LidaFactoryObject>();
    }
    
    protected List<LidaParam> getFactoryParams() {
        if (factoryDef != null) {
            LidaFactoryConfig factoryConfig = factoryDef.getFactoryConfig();
            if (factoryConfig != null) {
               return factoryConfig.getFactoryParams();
            }
        }
        
        return new ArrayList<LidaParam>();
    } 
    
    @SuppressWarnings("unchecked")
    protected Class<?> getInterface(LidaFactoryObject factoryObj)
            throws IllegalArgumentException {
        if (factoryObj == null) {
            throw new IllegalArgumentException("Factory object definition cannot be null");
        }

        String objType = factoryObj.getObjectType();

        Class<?> objInterface = null;
        try {
            objInterface = Class.forName(objType);
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "Unable to find factory object type {1}.",
                    new Object[] { TaskManager.getCurrentTick(), objType });
        }

        if (objInterface == null) {
            throw new IllegalArgumentException(
                    "Unable to resolve interface class for factory object type " + objType);
        }
        return objInterface;
    }
    
    protected <C> C createFactoryObject(LidaFactoryObject factoryObject, Class<C> interfaceClass) {
        C newObject = null;

        try {
            Class<?> clazz = Class.forName(factoryObject.getObjectImpl());

            @SuppressWarnings("unchecked")
            Constructor<C> constructor = (Constructor<C>) clazz.getDeclaredConstructor();

            newObject = constructor.newInstance();

        } catch (Exception e) {
            logger.log(Level.WARNING,
                    "Unable to instantiate class {1} of factory object type {2}.", new Object[] {
                            TaskManager.getCurrentTick(), factoryObject.getObjectImpl(),
                            factoryObject.getObjectType() });

            return null;
        }

        return newObject;
    }
    
    protected Map<String, Object> getTypedParams(List<LidaParam> params) {
        Map<String, Object> prop = new HashMap<String, Object>();
        for (LidaParam param : params) {
            String name = param.getName();
            String type = param.getType();
            String sValue = param.getValue();
            Object value = sValue;
            if (sValue != null) {

                if (type == null || "string".equalsIgnoreCase(type)) {
                    value = sValue;
                } else if ("int".equalsIgnoreCase(type)) {
                    try {
                        value = Integer.parseInt(sValue);
                    } catch (NumberFormatException e) {
                        value = null;
                        logger.log(Level.FINE, e.toString(), TaskManager.getCurrentTick());
                    }
                } else if ("double".equalsIgnoreCase(type)) {
                    try {
                        value = Double.parseDouble(sValue);
                    } catch (NumberFormatException e) {
                        value = null;
                        logger.log(Level.FINE, e.toString(), TaskManager.getCurrentTick());
                    }
                } else if ("boolean".equalsIgnoreCase(type)) {
                    value = Boolean.parseBoolean(sValue);
                }
            }
            prop.put(name, value);
        }
        return prop;
    }
    
    private void checkPreconditions() {

        // Verify that the supplied FactoryDef matches the factory's class
        if (hasValidFactoryDef()) {
            logger.log(Level.INFO, "Factory definition verified for {0} initializer",
                    new Object[] { factoryDef.getFactoryName() });
        } else {
            logger.log(Level.WARNING,
                    "Invalid factory definition encountered for {0} initializer",
                    new Object[] { factoryDef.getFactoryName() });
        }

        // Verify that factory dependencies have been initialized
        if (hasInitializedDependencies()) {
            logger.log(Level.INFO,
                    "Factory dependencies have been initialized for {0} initializer",
                    new Object[] { factoryDef.getFactoryName() });
        } else {
            logger.log(
                    Level.WARNING,
                    "Factory dependencies have not been initialized in FactoryManager for {0} initializer",
                    new Object[] { factoryDef.getFactoryName() });
        }
    }

    private boolean hasValidFactoryDef() {
        if (factoryDef == null) {
            return false;
        }

        if (factoryDef.getFactoryName() == null || factoryDef.getFactoryType() == null
                || factoryDef.getFactoryImpl() == null || factoryDef.getFactoryConfig() == null) {
            return false;
        }

        return true;
    }

    /*
     * Returns true if all factories that this factory is dependent on have been
     * initialized in the FactoryManager; otherwise, false is returned and a
     * warning is logged.
     */
    private boolean hasInitializedDependencies() {
        if (factoryDef == null) {
            logger.log(Level.WARNING, "Null factory definition encountered.");
            return false;
        }

        Set<String> dependencies = factoryDef.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }

        for (String factoryName : dependencies) {
            @SuppressWarnings("unchecked")
            Class<? extends Factory> dependency = (Class<? extends Factory>) globalInitializer
                    .getAttribute(factoryName);

            if (dependency == null || factoryManager.getFactory(dependency) == null) {
                logger.log(Level.WARNING,
                        "Missing dependency {0} in factory definition for {1} initializer.",
                        new Object[] { factoryName, factoryDef.getFactoryName() });
                return false;
            }
        }

        return true;
    }
}
