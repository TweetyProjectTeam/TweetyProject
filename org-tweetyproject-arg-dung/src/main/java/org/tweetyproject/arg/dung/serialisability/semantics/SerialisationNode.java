/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.semantics;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Node;

/**
 * Representation of a 'state' in the serialisation process, consisting of the current {@link DungTheory}
 * and a (partial) {@link Extension}.
 *
 * @see "Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument and Computation 13 (2022) 325–360"
 *
 * @author Lars Bengel
 */
public class SerialisationNode implements Node {
    /** theory of this state */
    private final DungTheory theory;
    /** extension of this state */
    private final Extension<DungTheory> extension;
    /** true if this state represents a valid extension */
    private final boolean terminal;

    /**
     * Initializes a new serialisation state for the given theory and extension and terminal value
     * @param theory a dung theory
     * @param extension an extension
     * @param terminal if the corresponding extension is valid
     */
    public SerialisationNode(DungTheory theory, Extension<DungTheory> extension, boolean terminal) {
        this.theory = theory;
        this.extension = extension;
        this.terminal = terminal;
    }

    /**
     * Initializes a serialisation state for the given theory and extension with {@code terminal} set to {@code false}
     * @param theory a dung theory
     * @param extension an extension
     */
    public SerialisationNode(DungTheory theory, Extension<DungTheory> extension) {
        this(theory, extension, false);
    }

    /**
     * Return the AF corresponding to this state
     * @return the theory
     */
    public DungTheory getTheory() {
        return theory;
    }

    /**
     * Return the extension corresponding to this state
     * @return the associated serialisation sequence
     */
    public Extension<DungTheory> getExtension() {
        return extension;
    }

    /**
     * Return whether this state is considered terminal
     * @return true, if the set of arguments of this node is an extension
     */
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof SerialisationNode))
            return false;

        var other = (SerialisationNode) obj;
        return this.extension.equals(other.extension);
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int result = 1;
        result = prime * result
                + ((theory == null) ? 0 : theory.hashCode());
        result = prime * result
                + ((extension == null) ? 0 : extension.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return extension.toString();
    }
}
