/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import java.util.Properties;

/**
 * The FactoryManager interface provides a registry for storing and retrieving
 * {@link Factory} objects.
 * 
 * @author Sean Kugele
 * @author Javier Snaider
 */
public interface FactoryManager {

    /**
     * Returns a default implementation matching the specified {@link Factory}
     * interface. If no match is found for the specified interface, or no
     * factory was registered as the default for that type, then {@code null}
     * will be returned.
     * 
     * @param type
     *            a {@link Factory} interface for which an implementation will
     *            be returned
     * @return an implementation for the specified interface
     */
    public <T extends Factory> T getFactory(Class<T> type);

    /**
     * Returns a {@link Factory} implementation matching the specified factory
     * name and interface. If no match is found for the specified criteria then
     * {@code null} will be returned.
     * 
     * @param name
     *            a name for the factory (set in the factory configuration)
     * @param type
     *            a {@link Factory} interface for which an implementation will
     *            be returned
     * @return an implementation for the specified interface
     */
    public <T extends Factory> T getFactory(String name, Class<T> type);

    /**
     * Initializes the FactoryManager and its factories based on the contents of
     * the supplied properties file.
     * 
     * @param p
     *            a properties file containing factory configuration information
     */
    public void init(Properties p);
}