/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.helper;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * A utility class to remove leading and trailing whitespace characters when
 * marshalling or unmarshalling elements to/from an XML document.
 * 
 * @author Sean Kugele
 * 
 */
public class StringTrimAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String element) throws Exception {
        if (element == null) {
            return null;
        }
        return element.trim();
    }

    @Override
    public String marshal(String element) throws Exception {
        if (element == null) {
            return null;
        }
        return element.trim();
    }
}