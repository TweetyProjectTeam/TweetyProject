/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.AfExtensionScheme;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.serialisability.SelectionFunction;
import org.tweetyproject.serialisability.SerialisationGraph;
import org.tweetyproject.serialisability.SerialisationSequence;
import org.tweetyproject.serialisability.SerialisationSystem;
import org.tweetyproject.serialisability.TerminationFunction;

/**
 * Reasoner for serialisable semantics
 * <p>
 * A semantics is serialisable iff its extensions are exactly the results of
 * the serialisation system given by a selection function α, which selects the
 * initial sets to add next, and a termination function β, which decides
 * whether the current state yields an extension. The reasoner delegates to a
 * {@link SerialisationSystem} over the {@link AfExtensionScheme}.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 *
 * @author Lars Bengel
 */
public class SerialisedExtensionReasoner extends AbstractExtensionReasoner {
    /** the serialisation system of the reasoner */
    private final SerialisationSystem<DungTheory, Extension<DungTheory>> system;
    /** the semantics of the reasoner, or null for custom functions */
    private final Semantics semantics;

    /**
     * Initializes a serialisation reasoner with the given selection and termination
     * functions
     *
     * @param alpha some selection function
     * @param beta  some termination function
     */
    public SerialisedExtensionReasoner(SelectionFunction<DungTheory, Extension<DungTheory>> alpha,
            TerminationFunction<DungTheory, Extension<DungTheory>> beta) {
        this(alpha, beta, null);
    }

    /**
     * Initializes a serialisation reasoner for the given semantics
     *
     * @param semantics some serialisable semantics
     * @throws IllegalArgumentException if the semantics is not serialisable
     */
    public SerialisedExtensionReasoner(Semantics semantics) {
        this(getSelectionFunction(semantics), getTerminationFunction(semantics), semantics);
    }

    /**
     * Initializes a serialisation reasoner
     *
     * @param alpha     some selection function
     * @param beta      some termination function
     * @param semantics the semantics, or null
     */
    private SerialisedExtensionReasoner(SelectionFunction<DungTheory, Extension<DungTheory>> alpha,
            TerminationFunction<DungTheory, Extension<DungTheory>> beta, Semantics semantics) {
        this.system = new SerialisationSystem<>(new AfExtensionScheme(), alpha, beta);
        this.semantics = semantics;
    }

    /**
     * @param semantics some semantics
     * @return the selection function of the semantics
     * @throws IllegalArgumentException if the semantics is not serialisable
     */
    private static SelectionFunction<DungTheory, Extension<DungTheory>> getSelectionFunction(Semantics semantics) {
        switch (semantics) {
            case ADM: case CO: case PR: case ST:
                return SelectionFunction.all();
            case GR: case SAD:
                return SelectionFunction.unattacked();
            case UC:
                return SelectionFunction.unattackedOrUnchallenged();
            default:
                throw new IllegalArgumentException("Semantics is not serialisable: " + semantics);
        }
    }

    /**
     * @param semantics some semantics
     * @return the termination function of the semantics
     * @throws IllegalArgumentException if the semantics is not serialisable
     */
    private static TerminationFunction<DungTheory, Extension<DungTheory>> getTerminationFunction(Semantics semantics) {
        switch (semantics) {
            case ADM: case SAD:
                return TerminationFunction.always();
            case CO: case GR:
                return TerminationFunction.noUnattackedInitialModels();
            case PR:
                return TerminationFunction.noInitialModels();
            case ST:
                return TerminationFunction.emptyFramework();
            case UC:
                return TerminationFunction.noUnattackedOrUnchallengedInitialModels();
            default:
                throw new IllegalArgumentException("Semantics is not serialisable: " + semantics);
        }
    }

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        return this.system.getModels(bbase);
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        Iterator<Extension<DungTheory>> models = this.getModels(bbase).iterator();
        return models.hasNext() ? models.next() : null;
    }

    /**
     * Computes the set of serialisation sequences wrt. the semantics
     *
     * @param bbase some argumentation framework
     * @return the set of serialisation sequences
     */
    public Set<SerialisationSequence<Extension<DungTheory>>> getSequences(DungTheory bbase) {
        return this.system.getSequences(bbase);
    }

    /**
     * Computes the serialisation graph of the framework wrt. the semantics
     *
     * @param bbase some argumentation framework
     * @return the serialisation graph
     */
    public SerialisationGraph<DungTheory, Extension<DungTheory>> getSerialisationGraph(DungTheory bbase) {
        return this.system.getGraph(bbase);
    }

    /**
     * @return the serialisation system of this reasoner
     */
    public SerialisationSystem<DungTheory, Extension<DungTheory>> getSystem() {
        return this.system;
    }

    /**
     * @return the semantics of this reasoner, or null if it was created from custom functions
     */
    public Semantics getSemantics() {
        return this.semantics;
    }
}
