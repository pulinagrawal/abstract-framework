/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.events;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.ModuleName;

/**
 * Event generated by the {@link Agent} to be handled by the GUI.
 * 
 * @author Javier Snaider
 * 
 */
public class FrameworkGuiEvent {

	/**
	 * Module sending the event
	 */
	private ModuleName module;

	/**
	 * Message describing the nature of the event's content
	 */
	private String message;

	/**
	 * Content being sent
	 */
	private Object content;

	/**
	 * @param name
	 *            ModuleName of module where event is coming from
	 * @param message
	 *            optional message, differentiate from various events
	 * @param content
	 *            sent content
	 */
	public FrameworkGuiEvent(ModuleName name, String message, Object content) {
		this.module = name;
		this.message = message;
		this.content = content;
	}

	/**
	 * sets module
	 * 
	 * @param m
	 *            module sending the event
	 */
	public void setModule(ModuleName m) {
		module = m;
	}

	/**
	 * @return the moduleId
	 */
	public ModuleName getModule() {
		return module;
	}

	/**
	 * sets events message
	 * 
	 * @param m
	 *            event's message
	 */
	public void setMessage(String m) {
		message = m;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param o
	 *            content
	 */
	public void setContent(Object o) {
		content = o;
	}

	/**
	 * @return the data
	 */
	public Object getContent() {
		return content;
	}
}

