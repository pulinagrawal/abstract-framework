/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.attentioncodelets;

import edu.memphis.ccrg.lida.framework.shared.CognitiveContentStructure;
import edu.memphis.ccrg.lida.framework.shared.RefractoryPeriod;
import edu.memphis.ccrg.lida.framework.tasks.Codelet;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * A kind of {@link Codelet} that checks {@link WorkspaceBuffer} for its desired
 * content and possibly adds {@link Coalition} to the {@link GlobalWorkspace}
 * 
 * @author Ryan J. McCall
 * 
 */
public interface AttentionCodelet extends Codelet {
    /**
     * @return the sought content
     */
    public CognitiveContentStructure<?> getSoughtContent();

    /**
     * @param content
     *            the content the codelet looks for.
     */
    public void setSoughtContent(CognitiveContentStructure<?> content);

    /**
     * Returns true if specified WorkspaceBuffer contains this codelet's sought
     * content.
     * 
     * @param buffer
     *            the WorkspaceBuffer to be checked for content
     * @return true, if successful
     */
    public boolean bufferContainsSoughtContent(WorkspaceBuffer buffer);

    /**
     * Returns sought content and related content from specified
     * WorkspaceBuffer.
     * 
     * @param buffer
     *            the buffer
     * @return the workspace content
     */
    public CognitiveContentStructure<?> retrieveWorkspaceContent(WorkspaceBuffer buffer);
}
