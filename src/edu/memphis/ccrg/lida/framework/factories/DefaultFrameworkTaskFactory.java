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

import org.w3c.dom.Element;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.initialization.FrameworkTaskDef;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.initialization.XmlUtils;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryObject;

/**
 * A default implementation of {@link FrameworkTaskFactory} interface.
 * 
 * @author Sean Kugele
 * 
 */
// TODO: This factory needs to be overhauled to use the same style
// interface as the Strategy factory; i.e., lookup by type + interface class.
public class DefaultFrameworkTaskFactory extends InitializableImpl implements
        FrameworkTaskFactory {
    private static final Logger logger = Logger.getLogger(DefaultFrameworkTaskFactory.class
            .getCanonicalName());

    /*
     * Map of between FrameworkTask types and the FrameworkTaskDef objects that
     * define them
     */
    private Map<String, FrameworkTaskDef> tasks = new HashMap<String, FrameworkTaskDef>();

    // Package private. Should be instantiated in the FactoryManager
    DefaultFrameworkTaskFactory() {
        
    }

    @Override
    public FrameworkTask getFrameworkTask(String type) {
        return getFrameworkTask(type, null);
    }

    @Override
    public FrameworkTask getFrameworkTask(String taskType, Map<String, ? extends Object> params) {
        return getFrameworkTask(taskType, params, null);
    }

    @Override
    // TODO: Can this stuff be configured in the XML file, or does it need to be dynamic?
    public FrameworkTask getFrameworkTask(String taskType,
            Map<String, ? extends Object> params, Map<ModuleName, FrameworkModule> modules) {
        FrameworkTask task = null;
        try {
            FrameworkTaskDef taskDef = tasks.get(taskType);
            if (taskDef == null) {
                logger.log(Level.WARNING, "Factory does not contain FrameworkTask type {1}",
                        new Object[] { TaskManager.getCurrentTick(), taskType });
                return null;
            }

            String className = taskDef.getClassName();
            task = (FrameworkTask) Class.forName(className).newInstance();
            task.setTicksPerRun(taskDef.getTicksPerRun());

            // Associate specified modules to task
            if (modules != null) {
                Map<ModuleName, String> associatedModules = taskDef.getAssociatedModules();
                for (ModuleName mName : associatedModules.keySet()) {
                    FrameworkModule module = modules.get(mName);
                    if (module != null) {
                        task.setAssociatedModule(module, associatedModules.get(mName));
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Could not associate module {1} to FrameworkTask {2}. Module was not found in 'modules' map",
                                new Object[] { TaskManager.getCurrentTick(), mName, task });
                    }
                }
            }

            // Call task's init with parameters
            Map<String, Object> mergedParams = new HashMap<String, Object>();
            Map<String, Object> defParams = taskDef.getParams();
            if (defParams != null) {
                mergedParams.putAll(defParams);
            }
            if (params != null) { // Order matters! Overwrite defParams with
                                  // argument parameters
                mergedParams.putAll(params);
            }
            task.init(mergedParams);
        } catch (InstantiationException e) {
            logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", new Object[] {
                    TaskManager.getCurrentTick(), e, taskType });
        } catch (IllegalAccessException e) {
            logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", new Object[] {
                    TaskManager.getCurrentTick(), e, taskType });
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", new Object[] {
                    TaskManager.getCurrentTick(), e, taskType });
        }
        return task;
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

        public Initializer(LidaFactoryDef factoryDef) {
            super(DefaultFrameworkTaskFactory.this, factoryDef);
        }

        @Override
        public void loadData() {
            
        }

//        private void fillTasks(Map<String, LidaFactoryObject> tasks) {
//            for (FrameworkTaskDef cd : tasks.values()) {
//                factory.addFrameworkTaskType(cd);
//            }
//        }
//
//        private Map<String, FrameworkTaskDef> getTasks(Element element) {
//            Map<String, FrameworkTaskDef> tasks = new HashMap<String, FrameworkTaskDef>();
//            List<Element> list = XmlUtils.getChildrenInGroup(element, "tasks", "task");
//            if (list != null && !list.isEmpty()) {
//                for (Element e : list) {
//                    FrameworkTaskDef taskDef = getTaskDef(e);
//                    if (taskDef != null) {
//                        tasks.put(taskDef.getName(), taskDef);
//                    }
//                }
//            }
//            return tasks;
//        }
//
//        private FrameworkTaskDef getTaskDef(Element e) {
//            FrameworkTaskDef taskDef = null;
//            String className = XmlUtils.getTextValue(e, "class");
//            String name = e.getAttribute("name");
//            int ticksPerRun = XmlUtils.getIntegerValue(e, "ticksperrun");
//            Map<String, Object> params = XmlUtils.getTypedParams(e);
//
//            Map<ModuleName, String> associatedModules = getAssociatedModules(e);
//            taskDef = new FrameworkTaskDef();
//            taskDef.setClassName(className.trim());
//            taskDef.setName(name.trim());
//            taskDef.setParams(params);
//            taskDef.setTicksPerRun(ticksPerRun);
//            taskDef.setAssociatedModules(associatedModules);
//            return taskDef;
//        }
//
//        private Map<ModuleName, String> getAssociatedModules(Element element) {
//            Map<ModuleName, String> associatedModules = new HashMap<ModuleName, String>();
//            List<Element> nl = XmlUtils.getChildren(element, "associatedmodule");
//            String elementName = element.getAttribute("name");
//            if (nl != null && ! nl.isEmpty()) {
//                for (Element assocModuleElement : nl) {
//                    String assocMod = XmlUtils.getValue(assocModuleElement);
//                    String function = assocModuleElement.getAttribute("function").trim();
//                    ModuleName name = ModuleName.getModuleName(assocMod);
//                    if (name == null) {
//                        name = ModuleName.addModuleName(assocMod);
//                        logger.log(
//                                Level.INFO,
//                                "{1} is not a pre-defined ModuleName so a new ModuleName was created for element: {2}",
//                                new Object[] { 0L, assocMod, elementName });
//                    }
//                    associatedModules.put(name, function);
//                }
//            }
//            return associatedModules;
//        }
    }
}
