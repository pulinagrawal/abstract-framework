/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.episodicmemory.sdm;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.shared.CognitiveContentStructure;

/**
 * A translator between {@link BitVector} used in
 * {@link SparseDistributedMemory}, and {@link CognitiveContentStructure} used in many other
 * LIDA modules.
 * 
 * @author Javier Snaider
 */
public interface Translator {

	/**
	 * Translates a {@link BitVector} into a {@link CognitiveContentStructure}.
	 * 
	 * @param v
	 *            a {@link BitVector} containing the boolean vector to be
	 *            translated
	 * @return the {@link CognitiveContentStructure} associated with the address
	 */
	public CognitiveContentStructure translate(BitVector v);

	/**
	 * Translates a {@link CognitiveContentStructure} into a {@link BitVector}.
	 * 
	 * @param ns
	 *            the {@link CognitiveContentStructure} to be translated
	 * @return a {@link BitVector} with the boolean address associated with the
	 *         {@link CognitiveContentStructure}
	 */
	public BitVector translate(CognitiveContentStructure ns);

}