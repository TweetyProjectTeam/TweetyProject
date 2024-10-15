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
package org.tweetyproject.arg.dung.reasoner.serialisable;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner.Initial.*;

/**
 * Reasoner for serialisable semantics
 * <p>
 * A semantics is serialisable iff it can be characterised by two functions
 * <br>
 * <ul>
 *   <li>{@link SelectionFunction} a(UA, UC, C):     Return a subset of the initial sets
 *   <li>{@link TerminationFunction} b(AF, S):     True, iff S is an extension
 * </ul>
 * which describe a transition system where {@code a(UA,UC,C)} provides the possible transitions to new states
 * and {@code b(AF,S)} determines whether the current state is an extension of the semantics.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument & Computation (2022)"
 *
 * @author Lars Bengel
 */
public class SerialisableReasoner extends AbstractExtensionReasoner {
    /** Selection function of the reasoner */
    private final SelectionFunction selectionFunction;
    /** Termination function of the reasoner */
    private final TerminationFunction terminationFunction;
    /** Semantics of the reasoner */
    private final Semantics semantics;

    /**
     * Initializes a serialisation reasoner with the given selection and termination functions
     * @param alpha some selection function
     * @param beta some termination function
     */
    public SerialisableReasoner(SelectionFunction alpha, TerminationFunction beta) {
        this(alpha, beta, null);
    }

    /**
     * Initializes a serialisation reasoner with the given selection and termination functions
     * and sets the semantics
     * @param alpha some selection function
     * @param beta some termination function
     * @param semantics some semantics
     */
    public SerialisableReasoner(SelectionFunction alpha, TerminationFunction beta, Semantics semantics) {
        this.selectionFunction = alpha;
        this.terminationFunction = beta;
        this.semantics = semantics;
    }

    /**
     * Initializes a serialisation reasoner for the given semantics
     * @param semantics some selection function
     */
    public SerialisableReasoner(Semantics semantics) {
        this.semantics = semantics;
        switch (semantics) {
            case ADM -> {
                selectionFunction = SelectionFunction.ADMISSIBLE;
                terminationFunction = TerminationFunction.ADMISSIBLE;
            } case CO -> {
                selectionFunction = SelectionFunction.ADMISSIBLE;
                terminationFunction = TerminationFunction.COMPLETE;
            } case PR -> {
                selectionFunction = SelectionFunction.ADMISSIBLE;
                terminationFunction = TerminationFunction.PREFERRED;
            } case SAD -> {
                selectionFunction = SelectionFunction.GROUNDED;
                terminationFunction = TerminationFunction.ADMISSIBLE;
            } case GR -> {
                selectionFunction = SelectionFunction.GROUNDED;
                terminationFunction = TerminationFunction.COMPLETE;
            } case UC -> {
                selectionFunction = SelectionFunction.UNCHALLENGED;
                terminationFunction = TerminationFunction.UNCHALLENGED;
            } case ST -> {
                selectionFunction = SelectionFunction.ADMISSIBLE;
                terminationFunction = TerminationFunction.STABLE;
            } default -> throw new IllegalArgumentException("Semantics is not serialisable: " + semantics);
        }
    }

    /**
     * Returns a serialisable reasoner for the given semantics.
     * @param semantics a semantics
     * @return a serialisable reasoner for the given semantics
     */
    public static SerialisableReasoner getSerialisableReasonerForSemantics(Semantics semantics){
        return new SerialisableReasoner(semantics);
    }

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        for (SerialisationSequence sequence: this.getSequences(bbase)) {
            result.add(sequence.getExtension());
        }
        return result;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        return this.getSequences(bbase).iterator().next().getExtension();
    }

    /**
     * Computes the set of serialisation sequences wrt. the semantics
     * @param bbase some argumentation framework
     * @return the set of serialisation sequences
     */
    public Collection<SerialisationSequence> getSequences(DungTheory bbase){
        return this.getSequences(bbase, new SerialisationSequence());
    }

    /**
     * Recursively computes all serialisation sequences wrt. the semantics
     * @param theory an argumentation framework
     * @param parentSequence the current serialisation sequence
     * @return the set of serialisation sequences
     */
    private Collection<SerialisationSequence> getSequences(DungTheory theory, SerialisationSequence parentSequence) {
        Collection<SerialisationSequence> result = new HashSet<>();

        // check whether the current state is acceptable, if yes add to results
        if (this.terminationFunction.execute(theory, parentSequence.getExtension())) {
            result.add(parentSequence);
        }
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(theory);
        Collection<Extension<DungTheory>> candidateSets = this.selectionFunction.execute((Set<Extension<DungTheory>>) initialSets.get(UA), (Set<Extension<DungTheory>>) initialSets.get(UC), (Set<Extension<DungTheory>>) initialSets.get(C));
        // iterate depth-first through all initial sets (and hence their induced states) and add all found serialisation sequences
        for (Extension<DungTheory> set: candidateSets) {
            DungTheory reduct = theory.getReduct(set);

            SerialisationSequence newSequence = new SerialisationSequence(parentSequence);
            newSequence.add(set);
            // continue computation of the serialisation in the reduct
            result.addAll(this.getSequences(reduct, newSequence));
        }
        return result;
    }

    /**
     * Creates a graph, visualizing the transition states of the serialisation process, which creates all serialisable extensions
     * according to the specified semantics of the specified framework.
     *
     * @param bbase argumentation framework, for which the extensions shall be computed
     * @return Graph representing the serialisation sequences
     */
    public SerialisationGraph getSerialisationGraph(DungTheory bbase) {
        Collection<SerialisationSequence> sequences = this.getSequences(bbase);
        return new SerialisationGraph(bbase, sequences, getSemantics());
    }

    /**
     * Return the semantics of this reasoner
     * @return the semantics
     */
    public Semantics getSemantics() {
        return semantics;
    }
}
