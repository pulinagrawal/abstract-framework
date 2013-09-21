/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;

/**
 * The interface for a StrategyFactory. A StrategyFactory supplies objects
 * implementing the {@link Strategy} interface. The details of the object
 * creation may be parameterized based on details in an XML configuration file
 * for the factory. <br>
 * <br>
 * Classes implementing the StrategyFactory interface must implement the
 * {@link InitializableFactory}.
 * 
 * @author Sean Kugele
 * 
 */
public interface StrategyFactory extends InitializableFactory {

    /**
     * Returns an implementation matching the requested alias and type, or
     * {@code null} if the factory does not contain a matching {@link Strategy}.
     * 
     * @param alias
     *            an alias for the strategy (set in the XML factory
     *            configuration)
     * 
     * @param type
     *            the interface corresponding to the desired {@code Strategy}
     *            implementation
     * 
     * @return an object that implements the desired type and is parameterized
     *         based on the corresponding alias in the LidaFactories XML
     *         configuration.
     */
    public <T extends Strategy> T getStrategy(String alias, Class<T> type);

    /**
     * Returns whether this factory contains type specified {@link Strategy}
     * type.
     * 
     * @param alias
     *            an alias for the strategy (set in the XML factory
     *            configuration)
     * 
     * @param type
     *            the interface corresponding to the desired {@code Strategy}
     *            implementation
     * 
     * @return true if factory contains an implementation corresponding to the
     *         specified alias and type
     */
    public <T extends Strategy> boolean containsStrategy(String alias, Class<T> type);
}