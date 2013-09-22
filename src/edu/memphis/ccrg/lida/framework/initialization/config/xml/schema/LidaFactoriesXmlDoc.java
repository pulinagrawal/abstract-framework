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

import edu.memphis.ccrg.lida.framework.initialization.XmlUtils;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories.Factory;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories.LidaFactories;

/**
 * @author Sean Kugele
 * 
 */
public class LidaFactoriesXmlDoc {

    private final List<LidaFactoryDef> factories;

    public LidaFactoriesXmlDoc(String xmlFilename) {
        LidaFactories docAsObj = XmlUtils
                .unmarshalXmlToObject(xmlFilename, LidaFactories.class);

        factories = transformFactories(docAsObj.getFactory());
    }

    /**
     * Validates the content of this LidaFactories XML document. Returns false
     * if the content in the document does not make sense for the intended use,
     * and true otherwise. <br>
     * Note: This method does not validate the document against an XML schema.
     * The intent of this method is to detect issues with the values that would
     * not be detected as syntax errors.
     * 
     * @return a boolean value
     */
    public boolean hasValidContent() {
        boolean hasValidContent = true;

        // TODO: Add validation logic
        return hasValidContent;
    }

    /**
     * Returns a list of the factories configured in the LidaFactories XML
     * config file.
     * 
     * @return a {@code List} of {@link LidaFactoryDef} objects
     */
    public List<LidaFactoryDef> getFactories() {
        return factories;
    }

    // Convert factories from instances of the Jaxb generated
    // Factory classes to instances of the LidaFactoryDef wrapper
    // classes
    private static List<LidaFactoryDef> transformFactories(List<Factory> factories) {
        List<LidaFactoryDef> resultList = new ArrayList<LidaFactoryDef>();

        for (Factory f : factories) {
            if (f != null) {
                resultList.add(new LidaFactoryDef(f));
            }
        }
        return resultList;
    }
}