/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;

/**
 * An {@link Initializable} object e.g. an {@link FrameworkModule} that is
 * initialized by the AgentXmlFactory.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 * 
 * @see AgentXmlFactory
 * @see FrameworkModule
 */
public interface FullyInitializable extends Initializable {

	/**
	 * Sets an associated FrameworkModule.
	 * 
	 * @param m
	 *            the module to be associated.
	 */
	public void setAssociatedModule(FrameworkModule m);
	
    /**
     * Sets associated modules based on Xml factory configuration.
     * 
     * @param moduleMap
     *            a mapping from module names to module object references
     */
    public void initAssociatedModules(Map<ModuleName, FrameworkModule> moduleMap);
}

