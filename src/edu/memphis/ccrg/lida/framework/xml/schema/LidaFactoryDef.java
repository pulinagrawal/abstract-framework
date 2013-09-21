/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.xml.schema;

import java.util.HashSet;
import java.util.Set;

import edu.memphis.ccrg.lida.framework.xml.schema.generated.lidafactories.Factory;

/**
 * @author Sean Kugele
 * 
 */
public class LidaFactoryDef {

    private final String factoryName;

    private final String factoryType;
    private final String factoryImpl;

    private final Set<String> dependencies;

    private final LidaFactoryConfig factoryConfig;

    // Character that separates the factory dependencies in the "depends"
    // attribute
    private static final String DEPENDS_SEPARATOR = ",";

    public LidaFactoryDef(Factory factory) {
        factoryName = factory.getName();

        factoryType = factory.getFactoryType();
        factoryImpl = factory.getFactoryImpl();

        dependencies = splitDepends(factory.getDepends());

        factoryConfig = new LidaFactoryConfig(factory.getFactoryConfig());
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public String getFactoryImpl() {
        return factoryImpl;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public LidaFactoryConfig getFactoryConfig() {
        return factoryConfig;
    }

    private static Set<String> splitDepends(String dependsAttr) {
        Set<String> resultSet = new HashSet<String>();
        if (dependsAttr == null) {
            return resultSet;
        }

        String[] tokens = dependsAttr.split(DEPENDS_SEPARATOR);
        for (String t : tokens) {
            resultSet.add(t);
        }

        return resultSet;
    }
}