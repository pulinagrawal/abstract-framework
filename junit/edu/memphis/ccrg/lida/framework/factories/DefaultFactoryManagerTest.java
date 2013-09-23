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
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;

/**
 * Junit4 test cases for the FactoryManager class.
 * 
 * @author Sean Kugele
 * 
 */
public class DefaultFactoryManagerTest {

    private final DefaultFactoryManager manager = DefaultFactoryManager.getInstance();

    private static final Properties LIDA_FACTORIES_PROPS = new Properties();

    private static final String LIDA_FACTORIES_XML_FILE = "junit/edu/memphis/ccrg/lida/framework/factories/lidaFactoriesTest.xml";

    public DefaultFactoryManagerTest() {
        LIDA_FACTORIES_PROPS.put("lida.factories.config", LIDA_FACTORIES_XML_FILE);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            manager.init(LIDA_FACTORIES_PROPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // Manager is a singleton, so we need to remove the state after each
        // test case
        manager.clear();
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultFactoryManager#getInstance()}
     * .
     */
    @Test
    public final void testGetInstance() {
        DefaultFactoryManager m1 = DefaultFactoryManager.getInstance();
        DefaultFactoryManager m2 = DefaultFactoryManager.getInstance();

        // Verify getInterface returns a Singleton
        assertEquals(m1, m2);
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultFactoryManager#init(java.util.Properties)}
     * .
     */
    @Test
    public final void testInit() {

        // Verify that null properties file results in IllegalArgumentException
        try {
            manager.init(null);
        } catch (IllegalArgumentException e) {
            // Correct exception
        } catch (Exception e) {
            fail("Incorrect exception generated: " + e);
        }

        // Verify that missing lida.factories.config results in
        // IllegalArgumentException
        try {
            Properties badProps = new Properties();
            manager.init(badProps);
        } catch (IllegalArgumentException e) {
            // Correct exception
        } catch (Exception e) {
            fail("Incorrect exception generated: " + e);
        }

        // Verify that non-existent filename referenced by
        // lida.factories.config generates FileNotFoundException
        try {
            Properties badProps = new Properties();
            badProps.put("lida.factories.config", "NOT_EXISTENT_FILENAME");

            manager.init(badProps);
        } catch (IllegalArgumentException e) {
            // Correct exception
        } catch (Exception e) {
            fail("Incorrect exception generated: " + e);
        }

        // Verify that good properties file results in no exceptions
        try {
            manager.init(LIDA_FACTORIES_PROPS);
        } catch (Exception e) {
            fail("Exception " + e + " encountered when no exception expected");
        }
    }

    @Test
    public final void testDefaultFactories() {
        fail("Not implemented");
    }

    @Test
    public final void testExternalFactoryConfig() {
        StrategyFactory f = manager.getFactory("altStrategyFactory", StrategyFactory.class);
        assertTrue(f != null);
        assertTrue(f instanceof StrategyFactory);

        Strategy s = null;
        
        s = f.getStrategy("defaultExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("defaultDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("slowExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("slowDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("pamDefaultExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("pamDefaultDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("noExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("noDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
    }
    
    @Test
    public final void testLocalFactoryConfig() {
        StrategyFactory f = manager.getFactory("defaultStrategyFactory", StrategyFactory.class);
        assertTrue(f != null);
        assertTrue(f instanceof StrategyFactory);

        Strategy s = null;
        
        s = f.getStrategy("defaultExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("defaultDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("slowExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("slowDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("pamDefaultExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("pamDefaultDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("noExciteStrategy", ExciteStrategy.class);
        assertTrue(s != null);
        
        s = f.getStrategy("noDecayStrategy", DecayStrategy.class);
        assertTrue(s != null);
    }

    @Test
    public final void testFactoryObjects() {
        fail("Not implemented");
    }

    @Test
    public final void testFactoryNameConflict() {
        fail("Not implemented");
    }

    @Test
    public final void testDependencies() {
        fail("Not implemented");
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.DefaultFactoryManager#clear()}
     * .
     */
    @Test
    public final void testClear() {
        Factory f = null;

        f = manager.getFactory(StrategyFactory.class);
        assertTrue(f != null);

        manager.clear();

        f = manager.getFactory(StrategyFactory.class);
        assertTrue(f == null);
    }

    @Test
    public final void testGetFactory() {
        // Get default StrategyFactory
        Factory f1 = manager.getFactory(StrategyFactory.class);
        assertTrue(f1 != null);
        assertTrue(f1 instanceof StrategyFactory);

        // Get default StrategyFactory factory using name
        Factory f2 = manager.getFactory("defaultStrategyFactory", StrategyFactory.class);
        assertTrue(f2 != null);
        assertTrue(f2 instanceof StrategyFactory);

        // Should be the same factory
        assertTrue(f1 == f2);

        // Get alternate StrategyFactory factory using name
        Factory f3 = manager.getFactory("altStrategyFactory", StrategyFactory.class);
        assertTrue(f3 != null);
        assertTrue(f3 instanceof StrategyFactory);

        // Default and alternate factories should be different
        assertTrue(f2 != f3);
    }
}