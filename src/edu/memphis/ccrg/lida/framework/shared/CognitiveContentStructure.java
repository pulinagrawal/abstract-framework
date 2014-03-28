/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import java.util.Collection;
import java.util.Iterator;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;

/**
 * A CognitiveContentStructure contains a collection of {@link CognitiveContent}
 * . It is used as the main conceptual representation among LIDA modules.
 * 
 * @author Sean Kugele
 * @author Javier Snaider
 * @param <T> The type of {@link CognitiveContent} contained by specific structures. 
 * 
 */
public interface CognitiveContentStructure<T extends CognitiveContent> extends Initializable {

    /**
     * Adds {@link CognitiveContent} to this CognitiveContentStructure.
     * 
     * @param content
     *            the {@link CognitiveContent} to add
     * @return the added {@link CognitiveContent}
     */
    public T addContent(T content);

    /**
     * Adds the {@link CognitiveContent} contained in the supplied Collection to
     * this CognitiveContentStructure.
     * 
     * @param content
     *            a Collection containing the {@link CognitiveContent} to add
     * @return a Collection containing the added {@link CognitiveContent}
     */
    public Collection<T> addContent(Collection<T> content);

    /**
     * Removes the specified {@link CognitiveContent} if present.
     * 
     * @param content
     *            {@link CognitiveContent} to remove.
     */
    public void removeContent(T content);

    /**
     * Returns whether this CognitiveContentStructure contains the specified
     * {@link CognitiveContent}.
     * 
     * @param content
     *            {@link CognitiveContent} to be checked for.
     * @return true if contains {@link CognitiveContent} with the same id
     */
    public boolean containsContent(T content);

    /**
     * Merges specified CognitiveContentStructure into this one.
     * 
     * @param cs
     *            CognitiveContentStructure
     */
    public void mergeWith(CognitiveContentStructure<T> cs);

    /**
     * Returns a deep copy of this CognitiveContentStructure.
     * 
     * @return CognitiveContentStructure
     */
    public CognitiveContentStructure<T> copy();

    /**
     * Returns the reduced representation of this CognitiveContentStructure.
     * 
     * @return the {@link CognitiveContent} that represents a reduced
     *         representation of this CognitiveContentStructure
     */
    public T getReducedRepresentation();

    /**
     * Returns an iterator over the {@code CognitiveContent} contained in this
     * {@code CognitiveContentStructure}.
     * 
     * @return an {@link Iterator}
     */
    public Iterator<T> getIterator();

    /**
     * Decays the {@link CognitiveContent} contained in this
     * {@link CognitiveContentStructure}.
     * 
     * @param ticks
     *            the number of ticks to decay for.
     */
    public void decay(long ticks);
}
