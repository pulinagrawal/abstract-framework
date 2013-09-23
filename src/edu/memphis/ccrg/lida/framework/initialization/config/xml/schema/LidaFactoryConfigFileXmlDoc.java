/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization.config.xml.schema;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import edu.memphis.ccrg.lida.framework.initialization.XmlUtils;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactoryconfig.LidaFactoryConfigFile;

/**
 * @author Sean Kugele
 * 
 */
public class LidaFactoryConfigFileXmlDoc {

    private final LidaFactoryConfig factoryConfig;

    public LidaFactoryConfigFileXmlDoc(String xmlFilename) throws FileNotFoundException,
            JAXBException {
        LidaFactoryConfigFile docAsObj = XmlUtils.unmarshalXmlToObject(xmlFilename,
                LidaFactoryConfigFile.class);

        factoryConfig = new LidaFactoryConfig(docAsObj.getFactoryConfig());
    }

    public LidaFactoryConfig getFactoryConfig() {
        return factoryConfig;
    }
}