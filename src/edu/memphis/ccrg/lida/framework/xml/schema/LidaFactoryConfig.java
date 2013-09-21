/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.xml.schema;

import java.util.ArrayList;
import java.util.List;

import edu.memphis.ccrg.lida.framework.xml.schema.generated.lida.Param;
import edu.memphis.ccrg.lida.framework.xml.schema.generated.lidafactories.FactoryConfig;
import edu.memphis.ccrg.lida.framework.xml.schema.generated.lidafactories.FactoryConfigType;
import edu.memphis.ccrg.lida.framework.xml.schema.generated.lidafactories.FactoryObject;

/**
 * @author Sean Kugele
 * 
 */
public class LidaFactoryConfig {

    private final List<LidaParam> factoryParams;
    private final List<LidaFactoryObject> factoryObjects;

    public LidaFactoryConfig(FactoryConfig factoryConfig) {
        FactoryConfigType factoryConfigType = factoryConfig.getType();

        if (FactoryConfigType.EXTERNAL == factoryConfigType) {
            LidaFactoryConfig externConfig = processExternalFile(factoryConfig.getFilename());

            factoryParams = externConfig.factoryParams;
            factoryObjects = externConfig.factoryObjects;
        } else {
            factoryParams = transformParams(factoryConfig.getParams());
            factoryObjects = transformObjects(factoryConfig.getFactoryObjects());
        }
    }

    
    public List<LidaParam> getFactoryParams() {
        return factoryParams;
    }


    public List<LidaFactoryObject> getFactoryObjects() {
        return factoryObjects;
    }

    // Populates the factory configuration from the details in the external
    // file referenced in this factory config
    private LidaFactoryConfig processExternalFile(String xmlConfigFile) {
        LidaFactoryConfigFileXmlDoc doc = new LidaFactoryConfigFileXmlDoc(xmlConfigFile);
        return doc.getFactoryConfig();
    }

    // Convert params from instances of the Jaxb generated Param classes to
    // instances of the LidaParam wrapper classes
    private static List<LidaParam> transformParams(List<Param> params) {
        List<LidaParam> resultList = new ArrayList<LidaParam>();

        if (params == null) {
            return resultList;
        }
        
        for (Param p : params) {
            if (p != null) {
                resultList.add(new LidaParam(p));
            }
        }
        return resultList;
    }

    // Convert factory objects from instances of the Jaxb generated
    // FactoryObject classes to instances of the LidaFactoryObject wrapper
    // classes
    private static List<LidaFactoryObject> transformObjects(List<FactoryObject> objects) {
        List<LidaFactoryObject> resultList = new ArrayList<LidaFactoryObject>();

        if (objects == null) {
            return resultList;
        }
        
        for (FactoryObject f : objects) {
            if (f != null) {
                resultList.add(new LidaFactoryObject(f));
            }
        }
        return resultList;
    }
}