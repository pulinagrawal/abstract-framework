/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization.config.xml.schema;

import java.util.ArrayList;
import java.util.List;

import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lida.Param;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories.FactoryObject;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories.FactoryObjectContextType;

/**
 * @author Sean Kugele
 * 
 */
public class LidaFactoryObject {

    private final String name;
    private final boolean isDefault;

    private final String objectType;
    private final String objectImpl;
    private final FactoryObjectContextType objectContextType;

    private final List<LidaParam> objectParams;

    public LidaFactoryObject(FactoryObject factoryObject) {
        name = factoryObject.getName();
        isDefault = factoryObject.isDefault();

        objectType = factoryObject.getType();
        objectImpl = factoryObject.getImpl();
        objectContextType = factoryObject.getContext();

        objectParams = transformParams(factoryObject.getParams());
    }

    public String getName() {
        return name;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getObjectImpl() {
        return objectImpl;
    }

    public List<LidaParam> getObjectParams() {
        return objectParams;
    }

    public boolean isDefault() {
        return isDefault;
    }
    
    public FactoryObjectContextType getObjectContextType() {
        return objectContextType;
    }

    private static List<LidaParam> transformParams(List<Param> params) {
        List<LidaParam> resultList = new ArrayList<LidaParam>();

        for (Param p : params) {
            if (p != null) {
                resultList.add(new LidaParam(p));
            }
        }
        return resultList;
    }
}