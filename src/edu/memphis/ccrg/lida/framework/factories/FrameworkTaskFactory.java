/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.initialization.FrameworkTaskDef;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;

/**
 * The interface for a FrameworkTaskFactory. A FrameworkTaskFactory supplies
 * objects implementing the {@link FrameworkTask} interface. The details of the
 * object creation may be parameterized based on details supplied by the caller. <br>
 * <br>
 * Classes implementing the FrameworkTaskFactory interface must implement the
 * {@link InitializableFactory}.
 * 
 * @author Sean Kugele
 * 
 */
public interface FrameworkTaskFactory extends InitializableFactory {

    /**
     * Returns whether this factory contains specified {@link FrameworkTask}
     * type.
     * 
     * @param type
     *            String
     * @return true if factory contains type or false if not
     */
    public boolean containsTaskType(String type);

    /**
     * Returns a default implementation matching the specified
     * {@link FrameworkTask} interface. If no match is found for the specified
     * interface, or no framework task was registered as the default for that
     * type, then {@code null} will be returned.
     * 
     * @param type
     *            a {@link FrameworkTask} interface for which an implementation
     *            will be returned
     * @return an implementation for the specified interface
     */
    public <T extends FrameworkTask> T getFrameworkTask(Class<T> type);

    /**
     * Returns an implementation matching the requested alias and type, or
     * {@code null} if the factory does not contain a matching
     * {@link FrameworkTask}.
     * 
     * @param alias
     *            an alias for the framework task (set in the XML factory
     *            configuration)
     * 
     * @param type
     *            the interface corresponding to the desired
     *            {@code FrameworkTask} implementation
     * 
     * @return an object that implements the desired type and is parameterized
     *         based on the corresponding alias in the LidaFactories XML
     *         configuration.
     */
    public <T extends FrameworkTask> T getFrameworkTask(String alias, Class<T> type);

}
