/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoriesXmlDoc;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.LidaFactoryDef;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;

/**
 * @author Sean Kugele
 * 
 */
public class DefaultStrategyFactoryTest {

	private StrategyFactory factory;

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
			if ("defaultStrategyFactory".equals(f.getFactoryName())) {
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

		strategies.add(factory.getStrategy("defaultExciteStrategy",
				ExciteStrategy.class));
		strategies.add(factory.getStrategy("defaultDecayStrategy",
				DecayStrategy.class));
		strategies.add(factory.getStrategy("slowExciteStrategy",
				ExciteStrategy.class));
		strategies.add(factory.getStrategy("slowDecayStrategy",
				DecayStrategy.class));
		strategies.add(factory.getStrategy("pamDefaultExciteStrategy",
				ExciteStrategy.class));
		strategies.add(factory.getStrategy("pamDefaultDecayStrategy",
				DecayStrategy.class));
		strategies.add(factory.getStrategy("noExciteStrategy",
				ExciteStrategy.class));
		strategies.add(factory.getStrategy("noDecayStrategy",
				DecayStrategy.class));

		for (Strategy s : strategies) {
			assertTrue(s != null);
		}

		Strategy s1, s2 = null;

		// Test default excite strategy
		s1 = factory.getStrategy(ExciteStrategy.class);
		assertTrue(s1 != null);
		s2 = factory.getStrategy("defaultExciteStrategy", ExciteStrategy.class);
		assertTrue(s1 == s2);

		// Test default decay strategy
		s1 = factory.getStrategy(DecayStrategy.class);
		assertTrue(s1 != null);
		s2 = factory.getStrategy("defaultDecayStrategy", DecayStrategy.class);
		assertTrue(s1 == s2);
		s2 = factory.getStrategy("slowExciteStrategy", ExciteStrategy.class);
		assertTrue(s1 != s2);
		s2 = factory.getStrategy("slowDecayStrategy", DecayStrategy.class);
		assertTrue(s1 != s2);
		s2 = factory.getStrategy("pamDefaultExciteStrategy",
				ExciteStrategy.class);
		assertTrue(s1 != s2);
		s2 = factory
				.getStrategy("pamDefaultDecayStrategy", DecayStrategy.class);
		assertTrue(s1 != s2);
		s2 = factory.getStrategy("noExciteStrategy", ExciteStrategy.class);
		assertTrue(s1 != s2);
		s2 = factory.getStrategy("noDecayStrategy", DecayStrategy.class);
		assertTrue(s1 != s2);
	}

	/**
	 * Test to validate that parameters were set properly.
	 */
	@Test
	public final void testStrategyInit() {
		Strategy strategy = factory.getStrategy("defaultExciteStrategy",
				ExciteStrategy.class);
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

	// TODO: Add error handling test cases
}