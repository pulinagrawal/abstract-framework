/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.CognitiveContent;
import edu.memphis.ccrg.lida.framework.shared.CognitiveContentStructure;

/**
 * 
 * Interface for the PerceptualAssociativeMemory module. The interface supports
 * top-down and bottom-up input modes, the registering of listeners (observer
 * pattern), and learning via the learn method.
 * 
 * @author Sean Kugele
 * @param <T>
 *            a {@link CognitiveContent} type parameter
 * 
 */
public interface PerceptualAssociativeMemory<T extends CognitiveContent> extends
        FrameworkModule {

    /**
     * An enum that specifies the supported "mode" parameters for the receive
     * method.
     */
    public enum ReceiveMode {

        /**
         * Top-Down
         */
        TOP_DOWN,

        /**
         * Bottom-Up
         */
        BOTTOM_UP
    }

    /**
     * Receives {@link CognitiveContentStructure} in the specified mode.
     * 
     * @param mode
     *            a supported {@link ReceiveMode} value
     * @param content
     *            the cognitive content to be received
     */
    public void receive(ReceiveMode mode, CognitiveContentStructure<T> content);

    /**
     * Update associations in PAM based on the supplied
     * {@link CognitiveContentStructure}.
     * 
     * @param content
     *            the cognitive content to be learned
     */
    public void learn(CognitiveContentStructure<T> content);
}
