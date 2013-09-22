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
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryObject;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaParam;

/**
 * A default implementation of the {@link StrategyFactory} interface. Instances
 * of this class will be accessible via the {@link FactoryManager}, and will be
 * configured using the Lida XML configuration files.
 * 
 * @author Sean Kugele
 */
public class DefaultStrategyFactory implements StrategyFactory {
    private static final Logger logger = Logger.getLogger(DefaultStrategyFactory.class
            .getCanonicalName());

    /*
     * Map of all the strategies (of any type) available to this factory
     */
    private final Map<FactoryKey, Strategy> strategies = new HashMap<FactoryKey, Strategy>();

    // Package private. Should be instantiated in the FactoryManager
    DefaultStrategyFactory() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Strategy> T getStrategy(String name, Class<T> type) {
        if (!containsStrategy(name, type)) {
            logger.log(Level.WARNING,
                    "Factory does not contain a strategy with name {1} and type {2}",
                    new Object[] { TaskManager.getCurrentTick(), name, type });

            return null;
        }

        FactoryKey key = new FactoryKey(name, type);
        return (T) strategies.get(key);
    }

    @Override
    public <T extends Strategy> boolean containsStrategy(String name, Class<T> type) {
        FactoryKey key = new FactoryKey(name, type);
        return strategies.containsKey(key);
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
                FactoryKey key = createFactoryObjectKey(obj);
                if (strategies.containsKey(key)) {
                    logger.log(
                            Level.WARNING,
                            "Encountered multiple strategies with name {1} and type {2}.  Only first will be retained.",
                            new Object[] { TaskManager.getCurrentTick(), key.getAlias(), key.getType().getCanonicalName() });
                }

                Strategy strategy = createStrategy(obj);

                // Initialize strategy parameters
                initStrategy(strategy, obj);

                // Add to strategy map in outer class
                strategies.put(key, strategy);
            }
        }

        private FactoryKey createFactoryObjectKey(LidaFactoryObject factoryObject) {
            if (factoryObject == null) {
                return null;
            }

            String name = factoryObject.getName();
            String type = factoryObject.getObjectType();

            Class<?> clazz = null;
            try {
                clazz = Class.forName(type);
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Unable to resolve interface {1}.", new Object[] {
                        TaskManager.getCurrentTick(), type });

                return null;
            }

            return new FactoryKey(name, clazz);
        }

        private Strategy createStrategy(LidaFactoryObject factoryObject) {
            Strategy strategy = null;

            try {
                @SuppressWarnings("unchecked")
                Class<? extends Strategy> clazz = (Class<? extends Strategy>) Class
                        .forName(factoryObject.getObjectImpl());

                Constructor<? extends Strategy> constructor = clazz.getDeclaredConstructor();

                strategy = constructor.newInstance();

            } catch (Exception e) {
                logger.log(Level.WARNING,
                        "Unable to instantiate class {1} of strategy type {2}.", new Object[] {
                                TaskManager.getCurrentTick(), factoryObject.getObjectImpl(),
                                factoryObject.getObjectType() });

                return null;
            }

            return strategy;
        }

        private void initStrategy(Strategy strategy, LidaFactoryObject factoryObject) {
            List<LidaParam> params = factoryObject.getObjectParams();

            if (params == null || params.isEmpty()) {
                return;
            }

            Map<String, Object> typedParams = getTypedParams(params);
            strategy.init(typedParams);
        }

        // TODO: Move this elsewhere so that all factories can share
        public Map<String, Object> getTypedParams(List<LidaParam> params) {
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
    }
}