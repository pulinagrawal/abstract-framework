/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.commands;

import edu.memphis.ccrg.lida.framework.Agent;

/**
 * Resumes Tasks execution.
 * 
 * @author Javier Snaider
 * 
 */
public class ResumeRunningThreadsCommand extends CommandImpl {

	@Override
	public void execute(Agent agent) {
		agent.getTaskManager().resumeTasks();
	}

}

