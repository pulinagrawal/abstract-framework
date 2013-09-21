/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.factories;

/**
 * @author Sean Kugele
 * 
 */
public class FactoryKey {

    private final String alias;
    private final Class<?> type;

    public FactoryKey(String alias, Class<?> type) {
        this.alias = alias;
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((type == null) ? 0 : type.getCanonicalName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FactoryKey other = (FactoryKey) obj;
        if (alias == null) {
            if (other.alias != null)
                return false;
        } else if (!alias.equals(other.alias))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (type != other.type)
            return false;
        return true;
    }

}
