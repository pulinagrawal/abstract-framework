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

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryObject;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaParam;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories.FactoryObjectContextType;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A default implementation of {@link FrameworkTaskFactory} interface.
 * 
 * @author Sean Kugele
 * 
 */
public class DefaultFrameworkTaskFactory extends InitializableImpl implements
        FrameworkTaskFactory {
    private static final Logger logger = Logger.getLogger(DefaultFrameworkTaskFactory.class
            .getCanonicalName());

    /**
     * Map of between FrameworkTask types and the FrameworkTask objects that
     * define them
     */
    protected final Map<String, FrameworkTask> tasks = new HashMap<String, FrameworkTask>();

    /**
     * FrameworkTasks that were registered as defaults. This map is used when a
     * task "name" is not provided to getFrameworkTask.
     */
    protected final Map<Class<? extends FrameworkTask>, String> defaultTasks = new HashMap<Class<? extends FrameworkTask>, String>();

    /**
     * Map to determine if the retrieved instance will be a singleton or
     * allocated its own new memory location
     */
    protected final Map<String, FactoryObjectContextType> contexts = new HashMap<String, FactoryObjectContextType>();

    // Package private. Should be instantiated in the FactoryManager
    DefaultFrameworkTaskFactory() {

    }

    @Override
    public <T extends FrameworkTask> T getFrameworkTask(Class<T> type) {
        if (type == null) {
            return null;
        }

        String name = defaultTasks.get(type);

        if (name == null) {
            logger.log(Level.WARNING,
                    "FrameworkTaskFactory does not contain a default strategy for type {1}",
                    new Object[] { TaskManager.getCurrentTick(), type });

            return null;
        }

        return getFrameworkTask(name, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends FrameworkTask> T getFrameworkTask(String name, Class<T> type) {
        if (!tasks.containsKey(name)) {
            logger.log(Level.WARNING,
                    "Factory does not contain a framework task with name {1} and type {2}",
                    new Object[] { TaskManager.getCurrentTick(), name, type });

            return null;
        }

        return (T) createTask(name, type);
    }

    @SuppressWarnings("unchecked")
    private <T extends FrameworkTask> T createTask(String name, Class<T> type) {
        FrameworkTask task = tasks.get(name);

        FactoryObjectContextType createMode = contexts.get(name);
        if (FactoryObjectContextType.SINGLETON == createMode) {
            return (T) task;
        }

        try {
            Class<?> clazz = task.getClass();
            Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructor();

            FrameworkTask newTask = constructor.newInstance();
            newTask.init(task.getParameters());

            task = newTask;

        } catch (Exception e) {
            logger.log(Level.WARNING,
                    "Unable to create FrameworkTask with name {1} and type {2}: {3}",
                    new Object[] { TaskManager.getCurrentTick(), name, type, e });
            task = null;
        }

        if (task != null) {
            GlobalInitializer initializer = GlobalInitializer.getInstance();
            Map<ModuleName, FrameworkModule> moduleMap = (Map<ModuleName, FrameworkModule>) initializer
                    .getAttribute("modulesMap");
            task.initAssociatedModules(moduleMap);
        }

        return (T) task;
    }

    @Override
    public boolean containsTaskType(String typeName) {
        return tasks.containsKey(typeName);
    }

    @Override
    public void init(LidaFactoryDef factoryDef) {
        FactoryInitializer<FrameworkTaskFactory> initializer = new Initializer(factoryDef);
        initializer.init();
    }

    /*
     * A private implementation of FactoryInitializer for this factory.
     */
    private class Initializer extends AbstractFactoryInitializer<FrameworkTaskFactory> {

        private final List<LidaParam> params;
        private final List<LidaFactoryObject> objects;

        public Initializer(LidaFactoryDef factoryDef) {
            super(DefaultFrameworkTaskFactory.this, factoryDef);

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

            // No parameters are accepted for DefaultFrameworkTaskFactory at the
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
                String taskName = obj.getName();
                String taskType = obj.getObjectType();

                if (tasks.containsKey(taskName)) {
                    logger.log(
                            Level.WARNING,
                            "Encountered multiple framework tasks with name {1}.  Only first will be retained.",
                            new Object[] { TaskManager.getCurrentTick(), taskName });
                }

                FrameworkTask task = createFactoryObject(obj, FrameworkTask.class);

                // Initialize framework task parameters
                initTask(task, obj);

                // Add to strategy map in outer class
                tasks.put(taskName, task);

                logger.log(Level.INFO,
                        "Successfully registered framework task with name {1} and type {2}.",
                        new Object[] { TaskManager.getCurrentTick(), taskName, taskType });

                // If framework task was set as "default" for type, then add to
                // FrameworkTaskFactory's default tasks registry
                if (obj.isDefault()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends FrameworkTask> taskInterface = (Class<? extends FrameworkTask>) getInterface(obj);
                    if (defaultTasks.containsKey(taskInterface)) {
                        logger.log(
                                Level.WARNING,
                                "Encountered multiple default framework tasks for type {1}.  Only first will be retained.",
                                new Object[] { TaskManager.getCurrentTick(), taskInterface });
                    } else {
                        defaultTasks.put(taskInterface, taskName);

                        logger.log(
                                Level.INFO,
                                "Framework task with name {1} has been marked as default for type {2}.",
                                new Object[] { TaskManager.getCurrentTick(), taskName,
                                        taskInterface });
                    }
                }
            }
        }

        private void initTask(FrameworkTask task, LidaFactoryObject factoryObject) {
            List<LidaParam> params = factoryObject.getObjectParams();

            if (params == null || params.isEmpty()) {
                return;
            }

            Map<String, Object> typedParams = getTypedParams(params);
            task.init(typedParams);
        }
    }
}
