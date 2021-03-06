/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.attentioncodelets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.PreafferenceListener;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.factories.DefaultFactoryManager;
import edu.memphis.ccrg.lida.framework.factories.FactoryManager;
import edu.memphis.ccrg.lida.framework.factories.FrameworkTaskFactory;
import edu.memphis.ccrg.lida.framework.shared.CognitiveContentStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.workspace.Workspace;

/**
 * {@link FrameworkModule} which creates and manages all
 * {@link AttentionCodelet}.
 * 
 * @author Ryan J. McCall
 * 
 */
public class AttentionCodeletModule extends FrameworkModuleImpl implements BroadcastListener,
        PreafferenceListener {

    private static final Logger logger = Logger.getLogger(AttentionCodeletModule.class
            .getCanonicalName());
    private static FactoryManager factoryManager = DefaultFactoryManager.getInstance();
    private static FrameworkTaskFactory taskFactory = factoryManager
            .getFactory(FrameworkTaskFactory.class);

    private static final String DEFAULT_CODELET_TYPE = "";
    private String defaultCodeletType = DEFAULT_CODELET_TYPE;

    private static final double DEFAULT_CODELET_ACTIVATION = 1.0;
    private double codeletActivation = DEFAULT_CODELET_ACTIVATION;

    private static final double DEFAULT_CODELET_REMOVAL_THRESHOLD = -1.0;
    private double codeletRemovalThreshold = DEFAULT_CODELET_REMOVAL_THRESHOLD;

    private static final double DEFAULT_CODELET_REINFORCEMENT = 0.5;
    private double codeletReinforcement = DEFAULT_CODELET_REINFORCEMENT;

    private Map<ModuleName, FrameworkModule> modulesMap = new HashMap<ModuleName, FrameworkModule>();

    /**
     * Default constructor
     */
    public AttentionCodeletModule() {
    }

    /**
     * Will set parameters with the following names:<br/>
     * <br/>
     * 
     * <b>attentionModule.defaultCodeletType</b> type of attention codelets
     * obtained from this module<br/>
     * <b>attentionModule.codeletActivation</b> initial activation of codelets
     * obtained from this module<br/>
     * <b>attentionModule.codeletRemovalThreshold</b> initial removal threshold
     * for codelets obtained from this module<br/>
     * <b>attentionModule.codeletReinforcement</b> amount of reinforcement
     * codelets' base-level activation receives during learning<br/>
     */
    @Override
    public void init() {
        defaultCodeletType = (String) getParam("attentionModule.defaultCodeletType",
                DEFAULT_CODELET_TYPE);
        codeletActivation = (Double) getParam("attentionModule.codeletActivation",
                DEFAULT_CODELET_ACTIVATION);
        codeletRemovalThreshold = (Double) getParam("attentionModule.codeletRemovalThreshold",
                DEFAULT_CODELET_REMOVAL_THRESHOLD);
        codeletReinforcement = (Double) getParam("attentionModule.codeletReinforcement",
                DEFAULT_CODELET_REINFORCEMENT);
    }

    @Override
    public void setAssociatedModule(FrameworkModule module) {
        if (module instanceof Workspace) {
            FrameworkModule m = module.getSubmodule(ModuleName.CurrentSituationalModel);
            modulesMap.put(m.getModuleName(), m);
        } else if (module instanceof GlobalWorkspace) {
            modulesMap.put(module.getModuleName(), module);
        }
    }

    public void setDefaultCodeletType(String type) {
        if (taskFactory.containsTaskType(type)) {
            defaultCodeletType = type;
        } else {
            logger.log(Level.WARNING,
                    "Cannot set default codelet type, factory does not have type {1}",
                    new Object[] { TaskManager.getCurrentTick(), type });
        }
    }

    @Override
    public void receiveBroadcast(Coalition coalition) {
        learn(coalition);
    }

    public AttentionCodelet getDefaultCodelet(Map<String, Object> params) {
        return getCodelet(defaultCodeletType, params);
    }

    public AttentionCodelet getDefaultCodelet() {
        return getCodelet(defaultCodeletType, null);
    }

    public AttentionCodelet getCodelet(String type) {
        return getCodelet(type, null);
    }

    public AttentionCodelet getCodelet(String type, Map<String, Object> params) {
        /*
         * AttentionCodelet codelet = (AttentionCodelet)
         * taskFactory.getFrameworkTask( type, params, modulesMap);
         */
        AttentionCodelet codelet = taskFactory.getFrameworkTask(type, AttentionCodelet.class);
        if (codelet == null) {
            logger.log(
                    Level.WARNING,
                    "Specified type does not exist in the factory. Attention codelet not created.",
                    TaskManager.getCurrentTick());
            return null;
        }
        codelet.setActivation(codeletActivation);
        codelet.setActivatibleRemovalThreshold(codeletRemovalThreshold);
        return codelet;
    }

    public void addCodelet(AttentionCodelet codelet) {
        if (codelet instanceof AttentionCodelet) {
            taskSpawner.addTask(codelet);
            logger.log(Level.FINER, "New attention codelet: {1} added to run.", new Object[] {
                    TaskManager.getCurrentTick(), codelet });
        } else {
            logger.log(Level.WARNING, "Can only add an AttentionCodelet",
                    TaskManager.getCurrentTick());
        }
    }

    @Override
    public void receivePreafference(CognitiveContentStructure<?> addSet,
            CognitiveContentStructure<?> deleteSet) {
        // TODO Receive results from Action Selection and create Attention
        // Codelets. We need
        // to figure out how to create coalitions and detect that something was
        // "deleted"
    }

    /**
     * Performs learning based on the {@link AttentionCodelet} that created the
     * current<br/>
     * winning {@link Coalition}
     * 
     * @param winningCoalition
     *            current {@link Coalition} winning competition for
     *            consciousness
     */
    @Override
    public void learn(Coalition winningCoalition) {
        // TODO not yet implemented
    }

    @Override
    public Object getModuleContent(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof String) {
            if ("GlobalWorkspace".equalsIgnoreCase((String) params[0])) {
                return modulesMap.get(ModuleName.GlobalWorkspace);
            }
        }
        return null;
    }

    @Override
    public void decayModule(long ticks) {
        // TODO not yet implemented
    }

}
