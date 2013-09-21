/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Junit4 test cases for the FactoryManager class.
 * 
 * @author Sean Kugele
 * 
 */
public class FactoryManagerTest {

    private final FactoryManager manager = FactoryManager.getInstance();

    private static final Properties prop = new Properties();

    private static final String LIDA_FACTORIES_XML_FILE = "junit/edu/memphis/ccrg/lida/framework/factories/lidaFactoriesTest.xml";

    private static final Set<Class<?>> EXPECTED_FACTORIES = new HashSet<Class<?>>();

    public FactoryManagerTest() {
        prop.put("lida.factories.config", LIDA_FACTORIES_XML_FILE);

        EXPECTED_FACTORIES.add(FrameworkTaskFactory.class);
        EXPECTED_FACTORIES.add(StrategyFactory.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        manager.init(prop);
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
     * {@link edu.memphis.ccrg.lida.framework.factories.FactoryManager#getInstance()}
     * .
     */
    @Test
    public final void testGetInstance() {
        FactoryManager m1 = FactoryManager.getInstance();
        FactoryManager m2 = FactoryManager.getInstance();

        // Verify getInterface returns a Singleton
        assertEquals(m1, m2);
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.FactoryManager#init(java.util.Properties)}
     * .
     */
    @Test
    public final void testInit() {
        // Init is called in setUp
        Set<Class<? extends Factory>> factSet = manager.listFactories();

        // Assert that the factories were initialized
        assertFalse(factSet.isEmpty());
        for (Class<?> clazz : EXPECTED_FACTORIES) {
            // Specific factories which were defined in XML must exist
            assertTrue(factSet.contains(clazz));
        }
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.FactoryManager#clear()}
     * .
     */
    @Test
    public final void testClear() {
        Set<Class<? extends Factory>> factSet = null;

        // Manager should contain factory elements after init
        factSet = manager.listFactories();
        assertFalse(factSet.isEmpty());

        manager.clear();

        // Should be empty after clear()
        factSet = manager.listFactories();
        assertTrue(factSet.isEmpty());
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.FactoryManager#listFactories()}
     * .
     */
    @Test
    public final void testListFactories() {
        Set<Class<? extends Factory>> factSet = manager.listFactories();
        assertEquals(factSet.size(), EXPECTED_FACTORIES.size());
    }

    /**
     * Test method for
     * {@link edu.memphis.ccrg.lida.framework.factories.FactoryManager#getFactory(java.lang.Class)}
     * .
     */
    @Test
    public final void testGetFactory() {
        // Get StrategyFactory
        Factory f1 = manager.getFactory(StrategyFactory.class);
        assertTrue(f1 != null);
        assertTrue(f1 instanceof StrategyFactory);
        
        // Get FrameworkTaskFactory
        Factory f2 = manager.getFactory(FrameworkTaskFactory.class);
        assertTrue(f2 != null);
        assertTrue(f2 instanceof FrameworkTaskFactory);
    }

    @Test
    public final void testViolatedSchema() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testInvalidContent() {
        fail("Not yet implemented"); // TODO
    }
}
