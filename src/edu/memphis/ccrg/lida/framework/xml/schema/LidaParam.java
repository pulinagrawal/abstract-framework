/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.xml.schema;

import edu.memphis.ccrg.lida.framework.xml.schema.generated.lida.Param;

/**
 * @author Sean Kugele
 *
 */
public class LidaParam {

    private final String type;
    private final String name;
    private final String value;
    
    public LidaParam(Param param) {
        type = param.getType();
        name = param.getName();
        value = param.getValue();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
