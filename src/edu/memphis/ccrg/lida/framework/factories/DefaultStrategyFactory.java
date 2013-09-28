/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryObject;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaParam;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A default implementation of the {@link StrategyFactory} interface. Instances
 * of this class will be accessible via the {@link DefaultFactoryManager}, and
 * will be configured using the Lida XML configuration files.
 * 
 * @author Sean Kugele
 */
public class DefaultStrategyFactory implements StrategyFactory {
    private static final Logger logger = Logger.getLogger(DefaultStrategyFactory.class
            .getCanonicalName());

    /**
     * Map of all the strategies (of any type) available to this factory
     */
    protected final Map<String, Strategy> strategies = new HashMap<String, Strategy>();

    /**
     * Strategies that were registered as defaults. This map is used when a
     * strategy "name" is not provided to getStrategy.
     */
    protected Map<Class<? extends Strategy>, String> defaultStrategies = new HashMap<Class<? extends Strategy>, String>();

    // Package private. Should be instantiated in the FactoryManager
    DefaultStrategyFactory() {

    }

    @Override
    public <T extends Strategy> T getStrategy(Class<T> type) {
        if (type == null) {
            return null;
        }

        String name = defaultStrategies.get(type);

        if (name == null) {
            logger.log(Level.WARNING,
                    "StrategyFactory does not contain a default strategy for type {1}",
                    new Object[] { TaskManager.getCurrentTick(), type });

            return null;
        }

        return getStrategy(name, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Strategy> T getStrategy(String name, Class<T> type) {
        if (!strategies.containsKey(name)) {
            logger.log(Level.WARNING,
                    "Factory does not contain a strategy with name {1} and type {2}",
                    new Object[] { TaskManager.getCurrentTick(), name, type });

            return null;
        }

        return (T) strategies.get(name);
    }

    @Override
    public void init(LidaFactoryDef factoryDef) {
        FactoryInitializer<StrategyFactory> initializer = new Initializer(factoryDef);
        initializer.init();
    }

    /*
     * A private implementation of FactoryInitializer for this factory.
     */
    private class Initializer extends AbstractFactoryInitializer<StrategyFactory> {

        private final List<LidaParam> params;
        private final List<LidaFactoryObject> objects;

        public Initializer(LidaFactoryDef factoryDef) {
            super(DefaultStrategyFactory.this, factoryDef);

            params = super.getFactoryParams();
            objects = super.getFactoryObjects();
        }

        @Override
        public void loadData() {
            loadFactoryParams();
            loadFactoryObjects();
        }

        private void loadFactoryParams() {
            if (params == null || params.isEmpty()) {
                return;
            }

            // No parameters are accepted for DefaultStrategyFactory at the
            // moment so just log if one was set in XML
            for (LidaParam param : params) {
                logger.log(Level.WARNING, "Unrecognized factory parameter in initializer: {1}",
                        new Object[] { TaskManager.getCurrentTick(), param.getName() });
            }
        }

        private void loadFactoryObjects() {
            if (objects == null || objects.isEmpty()) {
                return;
            }

            for (LidaFactoryObject obj : objects) {
                String strategyName = obj.getName();
                String strategyType = obj.getObjectType();

                if (strategies.containsKey(strategyName)) {
                    logger.log(
                            Level.WARNING,
                            "Encountered multiple strategies with name {1}.  Only first will be retained.",
                            new Object[] { TaskManager.getCurrentTick(), strategyName });
                }

                Strategy strategy = createFactoryObject(obj, Strategy.class);

                // Initialize strategy parameters
                initStrategy(strategy, obj);

                // Add to strategy map in outer class
                strategies.put(strategyName, strategy);

                logger.log(
                        Level.INFO,
                        "Successfully registered strategy with name {1} and type {2}.",
                        new Object[] { TaskManager.getCurrentTick(), strategyName, strategyType });

                // If strategy was set as "default" for type, then add to
                // StrategyFactory's default strategies registry
                if (obj.isDefault()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Strategy> strategyInterface = (Class<? extends Strategy>) getInterface(obj);
                    if (defaultStrategies.containsKey(strategyInterface)) {
                        logger.log(
                                Level.WARNING,
                                "Encountered multiple default strategies for type {1}.  Only first will be retained.",
                                new Object[] { TaskManager.getCurrentTick(), strategyInterface });
                    } else {
                        defaultStrategies.put(strategyInterface, strategyName);

                        logger.log(
                                Level.INFO,
                                "Strategy with name {1} has been marked as default for type {2}.",
                                new Object[] { TaskManager.getCurrentTick(), strategyName,
                                        strategyInterface });
                    }
                }
            }
        }

        private void initStrategy(Strategy strategy, LidaFactoryObject factoryObject) {
            List<LidaParam> params = factoryObject.getObjectParams();

            if (params == null || params.isEmpty()) {
                return;
            }

            Map<String, Object> typedParams = getTypedParams(params);
            strategy.init(typedParams);
        }
    }
}