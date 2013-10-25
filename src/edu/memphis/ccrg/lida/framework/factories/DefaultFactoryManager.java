/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.XmlUtils;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoriesXmlDoc;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A default implementation of the {@link FactoryManager} interface.
 * 
 * TODO: Add details about the XML configuration file expectations and usage.
 * 
 * @author Sean Kugele
 * @author Javier Snaider
 */
public class DefaultFactoryManager implements FactoryManager {

    private static final Logger logger = Logger.getLogger(DefaultFactoryManager.class
            .getCanonicalName());

    /*
     * Sole instance of this class that will be used.
     */
    private static DefaultFactoryManager instance = new DefaultFactoryManager();

    /*
     * Map of factory names to registered implementations of those interfaces
     */
    private Map<String, Factory> factories = new HashMap<String, Factory>();

    /*
     * Factories that were registered as defaults. This map is used when a
     * "factory name" is not provided to getFactory.
     */
    private Map<Class<? extends Factory>, String> defaultFactories = new HashMap<Class<? extends Factory>, String>();

    /*
     * Private constructor to prevent instantiation
     */
    private DefaultFactoryManager() {
    }

    /**
     * Returns the sole instance of this class. Implements the Singleton
     * pattern.
     * 
     * @return sole instance of this class
     */
    public static DefaultFactoryManager getInstance() {
        return instance;
    }

    /**
     * Returns an implementation matching the specified {@link Factory}
     * interface. A factory must have been registered as a default in the
     * factories XML configuration file using the "default" attribute of the
     * factory type. If no match is found for the specified interface, or no
     * factory was registered as the default for that type, then {@code null}
     * will be returned.
     * 
     * @param type
     *            a {@link Factory} interface for which an implementation will
     *            be returned
     * @return an implementation for the specified interface
     */
    @Override
    public <T extends Factory> T getFactory(Class<T> type) {
        if (type == null) {
            return null;
        }

        String name = defaultFactories.get(type);

        if (name == null) {
            logger.log(Level.WARNING,
                    "FactoryManager does not contain a default factory for type {1}",
                    new Object[] { TaskManager.getCurrentTick(), type });

            return null;
        }

        return getFactory(name, type);
    }

    /**
     * Returns a {@link Factory} implementation matching the specified factory
     * name and interface. The factory name should correspond to a "name"
     * attribute in the XML configuration file used to initialize the
     * {@code FactoryManager}. If no match is found for the specified criteria
     * then {@code null} will be returned.
     * 
     * @param name
     *            a name for the factory (set in the XML factory configuration)
     * @param type
     *            a {@link Factory} interface for which an implementation will
     *            be returned
     * @return an implementation for the specified interface
     */
    @Override
    public <T extends Factory> T getFactory(String name, Class<T> type) {
        if (name == null || type == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T factory = (T) factories.get(name);

        if (factory == null) {
            logger.log(Level.WARNING,
                    "FactoryManager does not contain a factory with alias {1} and type {2}",
                    new Object[] { TaskManager.getCurrentTick(), name, type });
        }

        return factory;
    }

    /**
     * Initializes the FactoryManager and its factories based on the contents of
     * the supplied properties file. The properties file is expected to have a
     * property that specifies the location on the LidaFactories XML
     * configuration file.
     * 
     * @param props
     *            a properties file containing factory config information
     */
    @Override
    public void init(Properties props) throws IllegalArgumentException {
        if (props == null) {
            throw new IllegalArgumentException("Properties file must not be null");
        }

        FactoryManagerInitializer initializer = new FactoryManagerInitializer(props);
        initializer.init();
    }

    /**
     * Reset the FactoryManager to an uninitialized state.
     */
    public void clear() {
        factories = new HashMap<String, Factory>();
    }

    // An inner class to initialize the FactoryManager and its factories
    private class FactoryManagerInitializer {

        // Property name that is expected to hold the file location of the
        // factories configuration file
        private static final String FACTORY_CONFIG_PROPERTY_NAME = "lida.factories.config";

        // XML Schema for factories configuration file
        private static final String DEFAULT_FACTORIES_CONFIG_SCHEMA = "edu/memphis/ccrg/lida/framework/initialization/config/LidaFactoriesXMLSchema.xsd";

        // Flag that turns XML schema validation on/off
        private static final boolean VALIDATE_XML = false;

        private final GlobalInitializer globalInitializer = GlobalInitializer.getInstance();

        // FactoryManager configuration filename
        private final String factoriesConfigFilename;

        // Map between factory aliases and the factory configurations that they
        // correspond
        private final Map<String, LidaFactoryDef> factoryAliasMap;

        public FactoryManagerInitializer(Properties p) {
            factoriesConfigFilename = p.getProperty(FACTORY_CONFIG_PROPERTY_NAME, null);

            factoryAliasMap = new HashMap<String, LidaFactoryDef>();
        }

        public void init() {
            if (factoriesConfigFilename == null || factoriesConfigFilename.isEmpty()) {
                throw new IllegalArgumentException("Factory configuration property "
                        + FACTORY_CONFIG_PROPERTY_NAME + " is null or missing.");
            }

            // Verify the factories configuration file conforms to the
            // expected layout
            if (VALIDATE_XML) {
                if (!XmlUtils.validateXmlFile(factoriesConfigFilename,
                        DEFAULT_FACTORIES_CONFIG_SCHEMA)) {

                    throw new IllegalArgumentException(
                            "LidaFactories configuration file violates the expected XML schema.");
                }
            }

            try {
                loadFactories();
            } catch (JAXBException e) {
                throw new IllegalArgumentException(
                        "Failed to load factory configuration.  Root cause: " + e);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(
                        "Failed to load factory configuration.  Root cause: " + e);
            }
        }

        private void loadFactories() throws FileNotFoundException, JAXBException {

            LidaFactoriesXmlDoc xmlDoc = new LidaFactoriesXmlDoc(factoriesConfigFilename);

            List<LidaFactoryDef> factories = xmlDoc.getFactories();

            populateFactoryAliasMap(factories);

            if (factories != null) {
                for (LidaFactoryDef factory : factories) {
                    initFactory(factory);
                }
            }
        }

        private void populateFactoryAliasMap(List<LidaFactoryDef> factories) {
            if (factories == null) {
                return;
            }

            for (LidaFactoryDef factoryDef : factories) {
                if (factoryDef != null) {
                    String factoryName = factoryDef.getFactoryName();

                    if (factoryName != null && !factoryName.isEmpty()) {
                        factoryAliasMap.put(factoryName, factoryDef);
                    }
                }
            }
        }

        private void initFactory(LidaFactoryDef factoryDef) {
            if (isFactoryInitialized(factoryDef)) {
                return;
            }

            String factoryName = factoryDef.getFactoryName();
            Class<? extends Factory> factoryInterface = getFactoryInterface(factoryDef);

            if (factories.containsKey(factoryName)) {
                logger.log(
                        Level.WARNING,
                        "Encountered multiple factories with name {1} and type {2}.  Only first will be retained.",
                        new Object[] { TaskManager.getCurrentTick(), factoryName,
                                factoryInterface });

                return;
            }

            // Initialize factory dependencies. If any of the factories this
            // factory is dependent on fail to initialize then this factory's
            // initialization will be aborted
            try {
                initDependencies(factoryDef);
            } catch (IllegalStateException e) {
                logger.log(
                        Level.WARNING,
                        "Failed to initialize dependencies for factory with name {1} and type {2}.",
                        new Object[] { TaskManager.getCurrentTick(), factoryName,
                                factoryInterface });
                return;
            }

            InitializableFactory factory = createFactory(factoryDef);
            if (factory == null) {
                logger.log(Level.WARNING,
                        "Failed to create factory with name {1} and type {2}.", new Object[] {
                                TaskManager.getCurrentTick(), factoryName, factoryInterface });
                return;
            }

            if (!factoryInterface.isAssignableFrom(factory.getClass())) {
                logger.log(
                        Level.WARNING,
                        "Type {1} is incompatible with implementing class {2}.",
                        new Object[] { TaskManager.getCurrentTick(), factoryInterface,
                                factory.getClass() });

                return;
            }

            try {
                // Initialize factory with factory data
                factory.init(factoryDef);

            } catch (Exception e) {
                logger.log(
                        Level.WARNING,
                        "Failed to initialize factory with name {1} and type {2}. Root cause was {3}.",
                        new Object[] { TaskManager.getCurrentTick(), factoryName,
                                factoryInterface, e });
                return;
            }

            // Add factory to FactoryManager registry
            factories.put(factoryName, factory);

            logger.log(
                    Level.INFO,
                    "Successfully registered factory with name {1} and type {2}.",
                    new Object[] { TaskManager.getCurrentTick(), factoryName, factoryInterface });

            // If factory was set as "default" for type, then add to
            // FactoryManager's default factory registry
            if (factoryDef.isDefault()) {
                if (defaultFactories.containsKey(factory.getClass())) {
                    logger.log(
                            Level.WARNING,
                            "Encountered multiple default factories for type type {1}.  Only first will be retained.",
                            new Object[] { TaskManager.getCurrentTick(), factoryInterface });
                } else {
                    defaultFactories.put(factoryInterface, factoryName);

                    logger.log(Level.INFO,
                            "Factory with name {1} has been marked as default for type {2}.",
                            new Object[] { TaskManager.getCurrentTick(), factoryName,
                                    factoryInterface });
                }
            }

            // Add to GlobalInitializer to resolve factory based on factory
            // alias -- this is useful for initializing factories that depend on
            // other factories
            globalInitializer.setAttribute(factoryName, factoryInterface);
        }

        private void initDependencies(LidaFactoryDef factoryDef) {
            if (factoryDef == null) {
                throw new IllegalArgumentException("Factory definition cannot be null");
            }

            Set<String> dependencies = factoryDef.getDependencies();
            if (dependencies == null || dependencies.isEmpty()) {
                return;
            }

            // Check for circular dependencies, and throw exception in one is
            // encountered
            if (hasCircularDependency(factoryDef, null)) {
                throw new IllegalStateException(
                        "Failed to initialize factory dependencies: circular dependency discovered");
            }

            // Initialize each of the factories that this factory listed as a
            // dependency if it has not already been initialized
            for (String factoryDependency : dependencies) {
                LidaFactoryDef dependDef = factoryAliasMap.get(factoryDependency);
                if (dependDef == null) {
                    throw new IllegalStateException(
                            "Failed to initialize factory dependencies: unable to find factory definition for "
                                    + factoryDependency);
                } else {
                    initFactory(dependDef);
                }
            }
        }

        private boolean hasCircularDependency(LidaFactoryDef factoryDef,
                Stack<String> predecessors) {
            if (factoryDef == null) {
                throw new IllegalArgumentException("Factory definition cannot be null");
            }

            if (predecessors == null) {
                predecessors = new Stack<String>();
            } else {
                if (predecessors.contains(factoryDef.getFactoryName())) {
                    return true;
                }
            }

            predecessors.push(factoryDef.getFactoryName());

            Set<String> dependencies = factoryDef.getDependencies();
            if (dependencies == null || dependencies.isEmpty()) {
                return false;
            } else {
                for (String dependency : dependencies) {
                    LidaFactoryDef dependDef = factoryAliasMap.get(dependency);
                    if (hasCircularDependency(dependDef, predecessors)) {
                        return true;
                    }
                }
            }
            predecessors.pop();

            return false;
        }

        @SuppressWarnings("unchecked")
        private Class<? extends Factory> getFactoryInterface(LidaFactoryDef factoryDef)
                throws IllegalArgumentException {
            if (factoryDef == null) {
                throw new IllegalArgumentException("Factory definition cannot be null");
            }

            String factoryType = factoryDef.getFactoryType();

            Class<? extends Factory> fInterface = null;
            try {
                fInterface = (Class<? extends Factory>) Class.forName(factoryType);
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Unable to find factory type {0}.",
                        new Object[] { factoryDef.getFactoryType() });
            }

            if (fInterface == null) {
                throw new IllegalArgumentException(
                        "Unable to resolve interface class for factory type " + factoryType);
            }
            return fInterface;
        }

        private boolean isFactoryInitialized(LidaFactoryDef factoryDef) {
            if (factoryDef == null) {
                throw new IllegalArgumentException("Factory definition cannot be null");
            }

            String factoryName = factoryDef.getFactoryName();
            Class<? extends Factory> factoryInterface = getFactoryInterface(factoryDef);

            if (factories.get(factoryName) != null) {
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        private InitializableFactory createFactory(LidaFactoryDef factoryDef) {
            if (factoryDef == null) {
                throw new IllegalArgumentException("Factory definition cannot be null");
            }

            InitializableFactory factory = null;

            try {
                Class<? extends InitializableFactory> clazz = (Class<? extends InitializableFactory>) Class
                        .forName(factoryDef.getFactoryImpl());

                Constructor<? extends InitializableFactory> constructor = clazz
                        .getDeclaredConstructor();

                // Factory may be a singleton with a protected or private
                // constructor. This bypasses the access modifier check
                constructor.setAccessible(true);

                factory = (InitializableFactory) constructor.newInstance();

            } catch (Exception e) {
                logger.log(
                        Level.WARNING,
                        "Unable to instantiate class {0} of factory type {1}. Factory will not be instantiated.",
                        new Object[] { factoryDef.getFactoryImpl(), factoryDef.getFactoryType() });
                return null;
            }

            return factory;
        }
    }
}