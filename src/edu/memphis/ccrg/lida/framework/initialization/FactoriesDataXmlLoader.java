/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.factories.DefaultFrameworkTaskFactory;
import edu.memphis.ccrg.lida.framework.factories.DefaultStrategyFactory;
import edu.memphis.ccrg.lida.framework.factories.Factory;
import edu.memphis.ccrg.lida.framework.factories.FactoryManager;
import edu.memphis.ccrg.lida.framework.factories.FrameworkTaskFactory;
import edu.memphis.ccrg.lida.framework.factories.StrategyFactory;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Loads the factoriesData.xml file which configures the factories of the
 * framework i.e. what strategies are used by the objects created by the
 * factory, the types of node, links, and {@link FrameworkTask} that can be
 * created as well.
 * 
 * @author Javier Snaider
 * 
 */
public class FactoriesDataXmlLoader {

    private static final Logger logger = Logger.getLogger(FactoriesDataXmlLoader.class
            .getCanonicalName());

    private static final FactoryManager factoryManager = FactoryManager.getInstance();

    /**
     * Loads factories with object types specified in {@link Properties}
     * 
     * @param properties
     *            {@link Properties}
     */
    public static void loadFactoriesData(Properties properties) {
        if (properties == null) {
            logger.log(Level.WARNING, "Unable to init factories: properties file is null.");
        } else {
            factoryManager.init(properties);
        }
    }
}

