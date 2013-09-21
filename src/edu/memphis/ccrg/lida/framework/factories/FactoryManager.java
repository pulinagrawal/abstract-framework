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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.XmlUtils;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoriesXmlDoc;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryDef;

/**
 * The FactoryManager provides a registry for storing and retrieving factory
 * objects.
 * 
 * @author Sean Kugele
 * @author Javier Snaider
 */
public class FactoryManager {

    private static final Logger logger = Logger.getLogger(FactoryManager.class
            .getCanonicalName());

    /*
     * Sole instance of this class that will be used.
     */
    private static FactoryManager instance = new FactoryManager();

    // TODO: A limitation of the current implementation is that we can only
    // register a single implementation for each factory interface. It seems to
    // be a completely reasonable use case to have multiple implementations for
    // the same factory (i.e., PamStrategyFactory, ProcMemoryStrategyFactory,
    // EpisodicMemoryStrategyFactory, etc.)

    /*
     * Map of Factory interfaces to registered implementations of those
     * interfaces
     */
    private Map<Class<? extends Factory>, InitializableFactory> factories = new HashMap<Class<? extends Factory>, InitializableFactory>();

    /*
     * Private constructor to prevent instantiation
     */
    private FactoryManager() {
    }

    /**
     * Returns the sole instance of this class. Implements the Singleton
     * pattern.
     * 
     * @return sole instance of this class
     */
    public static FactoryManager getInstance() {
        return instance;
    }

    /**
     * Returns the implementation for the specified {@link Factory} interface,
     * or {@code null} if no such implementation was found.
     * 
     * @param type
     *            a {@link Factory} interface for which an implementation will
     *            be returned
     * @return an implementation for the specified interface
     */
    public <T extends Factory> T getFactory(Class<T> type) {
        @SuppressWarnings("unchecked")
        T factory = (T) factories.get(type);

        if (factory == null) {
            logger.log(Level.WARNING, "Unable to find factory implementation for type {0}.",
                    new Object[] { type });
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
    public void init(Properties p) {
        FactoryManagerInitializer initializer = new FactoryManagerInitializer(p);
        initializer.init();
    }

    /**
     * Reset the FactoryManager to an uninitialized state.
     */
    public void clear() {
        factories = new HashMap<Class<? extends Factory>, InitializableFactory>();
    }

    /**
     * Returns a {@link Set} containing all of the {@link Factory} interfaces
     * that have implementations registered with the FactoryManager.
     * 
     * @return the {@code Set} of {@link Factory} interfaces
     */
    public Set<Class<? extends Factory>> listFactories() {
        if (factories == null) {
            return new HashSet<Class<? extends Factory>>();
        }

        return new HashSet<Class<? extends Factory>>(factories.keySet());
    }

    // An inner class to initialize the FactoryManager and its factories
    private class FactoryManagerInitializer {
        private static final String FACTORY_CONFIG_PROPERTY_NAME = "lida.factories.config";
        private static final String DEFAULT_FACTORY_CONFIG_XML_FILE_PATH = "configs/lidaFactoryConfig.xml";
        private static final String DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH = "edu/memphis/ccrg/lida/framework/initialization/config/LidaFactoriesXMLSchema.xsd";

        private final GlobalInitializer globalInitializer = GlobalInitializer.getInstance();

        private final String factoryConfigFilename;

        private final FactoryManager manager = FactoryManager.this;

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
                // Verify the Lida factories configuration file conforms to the
                // expected layout
//                if (!XmlUtils.validateXmlFile(factoryConfigFilename,
//                        DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH)) {
//                    logger.log(
//                            Level.WARNING,
//                            "LidaFactories configuration file {0} violates the expected XML schema defined in {1}",
//                            new Object[] { factoryConfigFilename,
//                                    DEFAULT_FACTORIES_CONFIG_SCHEMA_FILE_PATH });
//                    return;
//                }

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
//
//            if (xmlDoc == null || !xmlDoc.hasValidContent()) {
//                logger.log(
//                        Level.WARNING,
//                        "Failed to load LidaFactories configuration file {0}.  Document was unparseable or has invalid content.",
//                        new Object[] { factoryConfigFilename });
//            }

            List<LidaFactoryDef> factories = xmlDoc.getFactories();

            populateFactoryAliasMap(factories);

            for (LidaFactoryDef factory : factories) {
                initFactory(factory);
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

            Set<String> dependencies = factoryDef.getDependencies();
            initDependencies(factoryDef, dependencies);

            InitializableFactory factory = createFactory(factoryDef);
            Class<? extends Factory> factoryInterface = getFactoryInterface(factoryDef);

            if (factory == null || factoryInterface == null) {
                logger.log(Level.WARNING, "Failed to initialize factory with name {0}.",
                        new Object[] { factoryDef.getFactoryName() });
                return;
            }

            // Initialize factory with factory data
            factory.init(factoryDef);

            // Add factory to FactoryManager registry
            manager.factories.put(factoryInterface, factory);

            // Add to GlobalInitializer to resolve factory based on factory
            // alias -- this is useful for initializing factories that depend on
            // other factories
            globalInitializer.setAttribute(factory.getName(), factoryInterface);
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
                        Level.SEVERE,
                        "Unable to instantiate class {0} of factory type {1}. Factory will not be instantiated.",
                        new Object[] { factoryDef.getFactoryImpl(), factoryDef.getFactoryType() });
                return null;
            }

            return factory;
        }
    }
}