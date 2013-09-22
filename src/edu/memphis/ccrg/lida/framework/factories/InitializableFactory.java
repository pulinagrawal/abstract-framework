/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;

/**
 * An interface for an initializable {@link Factory}. All factories that are
 * initialized by the {@link FactoryManager} must implement this interface.
 * 
 * @author Sean Kugele
 * 
 */
public interface InitializableFactory extends Factory {
    
    /**
     * Initializes this factory based on the details specified in the supplied
     * {@link LidaFactoryDef}.
     * 
     * @param factoryDef
     *            a {@link LidaFactoryDef} containing initialization details
     * @throws IllegalArgumentException
     *             if malformed factory configuration is supplied
     */
    public void init(LidaFactoryDef factoryDef) throws IllegalArgumentException;
}
