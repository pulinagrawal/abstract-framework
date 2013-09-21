/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoriesXmlDoc;
import edu.memphis.ccrg.lida.framework.xml.schema.LidaFactoryDef;

/**
 * @author Sean Kugele
 * 
 */
public class DefaultStrategyFactoryTest {

    private StrategyFactory factory;

    private static final Properties prop = new Properties();

    private static final String LIDA_FACTORIES_XML_FILE = "junit/edu/memphis/ccrg/lida/framework/factories/lidaFactoriesTest.xml";

    private LidaFactoriesXmlDoc xmlDoc;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Unmarshall lidaFactories XML
        xmlDoc = new LidaFactoriesXmlDoc(LIDA_FACTORIES_XML_FILE);

        factory = new DefaultStrategyFactory();

        List<LidaFactoryDef> factories = xmlDoc.getFactories();
        LidaFactoryDef strategyFactory = null;
        for (LidaFactoryDef f : factories) {
            if ("StrategyFactory".equals(f.getFactoryName())) {
                strategyFactory = f;
                break;
            }
        }

        factory.init(strategyFactory);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        factory = null;
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultStrategyFactory#getStrategy(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public final void testGetStrategy() {
        List<Strategy> strategies = new ArrayList<Strategy>();

        strategies.add(factory.getStrategy("defaultExcite", ExciteStrategy.class));
        strategies.add(factory.getStrategy("defaultDecay", DecayStrategy.class));
        strategies.add(factory.getStrategy("slowExcite", ExciteStrategy.class));
        strategies.add(factory.getStrategy("slowDecay", DecayStrategy.class));
        strategies.add(factory.getStrategy("pamDefaultExcite", ExciteStrategy.class));
        strategies.add(factory.getStrategy("pamDefaultDecay", DecayStrategy.class));
        strategies.add(factory.getStrategy("noExcite", ExciteStrategy.class));
        strategies.add(factory.getStrategy("noDecay", DecayStrategy.class));

        for (Strategy s : strategies) {
            assertTrue(s != null);
        }
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultStrategyFactory#containsStrategy(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public final void testContainsStrategy() {
        assertTrue(factory.containsStrategy("defaultExcite", ExciteStrategy.class));
        assertTrue(factory.containsStrategy("defaultDecay", DecayStrategy.class));
        assertTrue(factory.containsStrategy("slowExcite", ExciteStrategy.class));
        assertTrue(factory.containsStrategy("slowDecay", DecayStrategy.class));
        assertTrue(factory.containsStrategy("pamDefaultExcite", ExciteStrategy.class));
        assertTrue(factory.containsStrategy("pamDefaultDecay", DecayStrategy.class));
        assertTrue(factory.containsStrategy("noExcite", ExciteStrategy.class));
        assertTrue(factory.containsStrategy("noDecay", DecayStrategy.class));
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultStrategyFactory#getName()}
     * .
     */
    @Test
    public final void testGetName() {
        assertEquals("StrategyFactory", factory.getName());
    }
   
    /**
     * Test to validate that parameters were set properly.
     */
    @Test
    public final void testStrategyInit() {
        Strategy strategy = factory.getStrategy("defaultExcite", ExciteStrategy.class);
        assertTrue(strategy != null);
        
        Map<String, ?> params = strategy.getParameters();
        assertTrue(params != null);
        assertTrue(params.size() == 2);
        
        Object slope = params.get("m");
        assertTrue(slope instanceof Double);
        double slopeAsDouble = (Double) slope;
        assertTrue(slopeAsDouble == 1.0);
        
        Object flyweight = params.get("flyweight");
        assertTrue(flyweight instanceof Boolean);
        boolean flyweightAsBoolean = (Boolean) flyweight;
        assertTrue(flyweightAsBoolean);
    }
}