/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Map of Factory interfaces to registered implementations of those
     * interfaces
     */
    private Map<FactoryKey, InitializableFactory> factories = new HashMap<FactoryKey, InitializableFactory>();

    /*
     * Factories that were registered as defaults. This map is used when a
     * "factory name" is not provided to getFactory. A FactoryKey is returned so
     * that it can then be used to interrogate the factories map
     */
    private Map<Class<? extends InitializableFactory>, FactoryKey> defaultFactories = new HashMap<Class<? extends InitializableFactory>, FactoryKey>();

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

        FactoryKey key = defaultFactories.get(type);

        if (key == null) {
            logger.log(Level.WARNING,
                    "FactoryManager does not contain a default factory for type {1}",
                    new Object[] { TaskManager.getCurrentTick(), type });

            return null;
        }

        return getFactory(key.getAlias(), type);
    }

    /**
     * Returns a {@link Factory} implementation matching the specified factory
     * name and interface. The factory name should correspond to a "name"
     * attribute inthe XML configuration file used to initialize the
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

        FactoryKey key = new FactoryKey(name, type);

        @SuppressWarnings("unchecked")
        T factory = (T) factories.get(key);

        if (factory == null) {
            logger.log(Level.WARNING,
                    "FactoryManager does not contain a factory with alias {1} and type {2}",
                    new Object[] { TaskManager.getCurrentTick(), name, type });
        }

        return factory;
    }

    /**
     * Initializes the FactoryManager and its factories based on the contents of
     * the supplied properties file.
     * 
     * @param p
     *            a properties file containing factory config information
     */
    @Override
    public void init(Properties p) {
        FactoryManagerInitializer initializer = new FactoryManagerInitializer(p);
        initializer.init();
    }

    /**
     * Reset the FactoryManager to an uninitialized state.
     */
    public void clear() {
        factories = new HashMap<FactoryKey, InitializableFactory>();
    }

    // An inner class to initialize the FactoryManager and its factories
    private class FactoryManagerInitializer {
        private static final String FACTORY_CONFIG_PROPERTY_NAME = "lida.factories.config";
        private static final String DEFAULT_FACTORY_CONFIG_XML_FILE_PATH = "configs/lidaFactories.xml";
        private static final String DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH = "edu/memphis/ccrg/lida/framework/initialization/config/LidaFactoriesXMLSchema.xsd";

        private static final boolean VALIDATE_XML = false;

        private final GlobalInitializer globalInitializer = GlobalInitializer.getInstance();

        private final String factoryConfigFilename;

        private final DefaultFactoryManager manager = DefaultFactoryManager.this;

        // Map between factory aliases and the factory configurations that they
        // correspond
        private final Map<String, LidaFactoryDef> factoryAliasMap;

        public FactoryManagerInitializer(Properties p) {
            factoryConfigFilename = p.getProperty(FACTORY_CONFIG_PROPERTY_NAME,
                    DEFAULT_FACTORY_CONFIG_XML_FILE_PATH);

            factoryAliasMap = new HashMap<String, LidaFactoryDef>();
        }

        public void init() {
            try {
                if (VALIDATE_XML) {

                    // Verify the Lida factories configuration file conforms to
                    // the expected layout
                    if (!XmlUtils.validateXmlFile(factoryConfigFilename,
                            DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH)) {
                        logger.log(
                                Level.WARNING,
                                "LidaFactories configuration file {0} violates the expected XML schema defined in {1}",
                                new Object[] { factoryConfigFilename,
                                        DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH });
                        return;
                    }
                }

                loadFactories();
            } catch (Exception e) {
                logger.log(
                        Level.WARNING,
                        "Failed to load LidaFactories configuration file {0}.  Encountered exception {1}.",
                        new Object[] { factoryConfigFilename, e });
            }
        }

        private void loadFactories() {

            LidaFactoriesXmlDoc xmlDoc = new LidaFactoriesXmlDoc(factoryConfigFilename);

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

            FactoryKey key = new FactoryKey(factoryName, factoryInterface);
            if (factories.containsKey(key)) {
                logger.log(
                        Level.WARNING,
                        "Encountered multiple factories with name {1} and type {2}.  Only first will be retained.",
                        new Object[] { TaskManager.getCurrentTick(), factoryName,
                                factoryInterface.getCanonicalName() });
            }

            Set<String> dependencies = factoryDef.getDependencies();
            initDependencies(factoryDef, dependencies);

            InitializableFactory factory = createFactory(factoryDef);
            if (factory == null) {
                logger.log(Level.WARNING,
                        "Failed to create factory with name {1} and type {2}.",
                        new Object[] { TaskManager.getCurrentTick(), factoryName,
                                factoryInterface.getCanonicalName() });
                return;
            }

            try {
                // Initialize factory with factory data
                factory.init(factoryDef);

            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING,
                        "Failed to initialize factory with name {1} and type {2}: {3}",
                        new Object[] { TaskManager.getCurrentTick(), key.getAlias(),
                                key.getType().getCanonicalName(), e });
                return;
            }

            // Add factory to FactoryManager registry
            manager.factories.put(key, factory);

            logger.log(Level.INFO,
                    "Successfully registered factory with name {1} and type {2}.",
                    new Object[] { TaskManager.getCurrentTick(), key.getAlias(),
                            key.getType().getCanonicalName() });
            
            if (factoryDef.isDefault()) {
                if (defaultFactories.containsKey(factory.getClass())) {
                    logger.log(
                            Level.WARNING,
                            "Encountered multiple default factories for type type {1}.  Only first will be retained.",
                            new Object[] { TaskManager.getCurrentTick(),
                                    key.getType().getCanonicalName() });
                } else {
                    defaultFactories.put(factory.getClass(), key);
                    
                    logger.log(Level.INFO,
                            "Factory with name {1} has been marked as default for type {2}.",
                            new Object[] { TaskManager.getCurrentTick(), key.getAlias(),
                                    key.getType().getCanonicalName() });
                }
            }

            // Add to GlobalInitializer to resolve factory based on factory
            // alias -- this is useful for initializing factories that depend on
            // other factories
            globalInitializer.setAttribute(factoryDef.getFactoryName(), factoryInterface);
        }

        private void initDependencies(LidaFactoryDef factoryDef, Set<String> dependencies) {
            if (dependencies == null || dependencies.isEmpty()) {
                return;
            }

            if (hasCircularDependency(factoryDef, null)) {
                logger.log(
                        Level.WARNING,
                        "Circular dependency discovered for factory {0}.  Factory will not be initialized.",
                        new Object[] { factoryDef.getFactoryName() });
                return;
            }

            for (String factoryDependency : dependencies) {
                LidaFactoryDef dependDef = factoryAliasMap.get(factoryDependency);
                if (dependDef == null) {
                    logger.log(
                            Level.WARNING,
                            "Unable to resolve factory dependency definition {0} for factory {1}.",
                            new Object[] { factoryDependency, factoryDef.getFactoryName() });
                    continue;
                } else {
                    initFactory(dependDef);
                }
            }
        }

        private boolean hasCircularDependency(LidaFactoryDef factoryDef,
                Stack<String> predecessors) {
            if (factoryDef == null) {
                return false;
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
        private Class<? extends Factory> getFactoryInterface(LidaFactoryDef factoryDef) {
            if (factoryDef == null) {
                return null;
            }

            String factoryType = factoryDef.getFactoryType();

            Class<? extends Factory> fInterface = null;
            try {
                fInterface = (Class<? extends Factory>) Class.forName(factoryType);
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Unable to find factory type {0}.",
                        new Object[] { factoryDef.getFactoryType() });
            }

            return fInterface;
        }

        private boolean isFactoryInitialized(LidaFactoryDef factoryDef) {
            Class<? extends Factory> factoryInterface = getFactoryInterface(factoryDef);
            if (manager.getFactory(factoryInterface) != null) {
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        private InitializableFactory createFactory(LidaFactoryDef factoryDef) {
            if (factoryDef == null) {
                return null;
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