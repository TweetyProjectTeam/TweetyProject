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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner.serialisable;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DirectedEdge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner.Initial.*;

/**
 * Abstract class for computing extensions via a serialised transition system
 * <p>
 * to implement this class, you need to define a selection and a termination function.<br>
 * Selection function a(UA, UC, C):     selects and returns a subset of the initial sets.<br>
 * Termination function b((AF, S)):      If the given state satisfies a specific condition,
 * its extension may be accepted by the associated serialisable semantics.
 *
 * @author Lars Bengel
 * @author Julian Sander
 */
public abstract class NewSerialisableExtensionReasoner extends AbstractExtensionReasoner {

    /** the semantics this reasoner is based on */
    protected Semantics semantics;

    /**
     * Constructs a serialisable extension reasoner for the given semantics
     * @param semantics the semantics of this reasoner
     */
    public NewSerialisableExtensionReasoner(Semantics semantics) {
        super();
        this.semantics = semantics;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
     */
    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        // not supported
        throw new UnsupportedOperationException("Not supported");
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
     */
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        for (SerialisationSequence sequence: this.getModelsSequences(bbase)) {
            result.add(sequence.getExtension());
        }
        return result;
    }

    /**
     * Computes the set of serialisation sequences that each correspond to some extension wrt. the semantics of this reasoner.
     * @param bbase some argumentation framework
     * @return the set of serialisation sequences computed by the reasoner
     */
    public Collection<SerialisationSequence> getModelsSequences(DungTheory bbase){
        return this.getModelsRecursive(bbase, new SerialisationSequence());
    }

    /**
     * Recursively computes all serialisation sequences for the semantics
     * @param theory an argumentation framework
     * @param parentSequence the current serialisation sequence
     * @return the set of serialisation sequences for the given AF
     */
    private Collection<SerialisationSequence> getModelsRecursive(DungTheory theory, SerialisationSequence parentSequence) {
        Collection<SerialisationSequence> result = new HashSet<>();

        // check whether the current state is acceptable, if yes add to results
        if (this.terminationFunction(theory, parentSequence.getExtension())) {
            result.add(parentSequence);
        }
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(theory);
        Collection<Extension<DungTheory>> candidateSets = this.selectionFunction(initialSets.get(UA), initialSets.get(UC), initialSets.get(C));
        // iterate depth-first through all initial sets (and hence their induced states) and add all found serialisation sequences
        for (Extension<DungTheory> set: candidateSets) {
            DungTheory reduct = theory.getReduct(set);

            SerialisationSequence newSequence = new SerialisationSequence(parentSequence);
            newSequence.add(set);
            // continue computation of the serialisation in the reduct
            result.addAll(this.getModelsRecursive(reduct, newSequence));
        }
        return result;
    }

    /**
     * @return The semantics of this reasoner.
     */
    public Semantics getSemantics() {
        return this.semantics;
    }

    /**
     * Select a subset of the initial sets of the AF, i.e. the possible successor states
     * @param unattacked the set of unattacked initial sets
     * @param unchallenged the set of unchallenged initial sets
     * @param challenged the set of challenged initial sets
     * @return a subset of the initial sets of the theory, depends on the semantics of the reasoner
     */
    protected abstract Collection<Extension<DungTheory>> selectionFunction(
            Collection<Extension<DungTheory>> unattacked,
            Collection<Extension<DungTheory>> unchallenged,
            Collection<Extension<DungTheory>> challenged);

    /**
     * Determines whether the current state represents an extension wrt. the semantics of the reasoner or not.
     * @param reducedFramework The current framework of the transition system
     * @param constructedExtension The extension constructed so far
     * @return true, if the state satisfies the condition of the semantics
     */
    protected abstract boolean terminationFunction(DungTheory reducedFramework, Extension<DungTheory> constructedExtension);

    /**
     * Creates a graph, visualizing the transition states of the serialisation process, which creates all serialisable extensions
     * according to the specified semantics of the specified framework.
     *
     * @param bbase Argumentation framework, for which the extensions shall be computed.
     * @return Graph showing the serialisation sequences.
     */
    public SerialisationGraph getModelsGraph(DungTheory bbase) {
        Collection<SerialisationSequence> sequences = this.getModelsSequences(bbase);
        if(sequences.isEmpty()) return new SerialisationGraph(this.semantics, new HashSet<>());

        Collection<Extension<DungTheory>> extensions = new HashSet<>();
        for (SerialisationSequence sequence: sequences) {
            extensions.add(sequence.getExtension());
        }

        var iterationOfSequences = sequences.iterator();
        var sequence = iterationOfSequences.next(); // get first sequence
        var iterationOfSets = sequence.iterator();
        var tmpParent = iterationOfSets.next(); // get first set
        SerialisationGraph graph = new SerialisationGraph(new Extension<>(tmpParent), this.semantics, extensions);

        do {
            //iterate through sequences
            while(iterationOfSets.hasNext()) {
                // iterate through one sequence
                var argSet = iterationOfSets.next();
                var newChild = new Extension<DungTheory>(tmpParent);
                newChild.addAll(argSet);
                if(!graph.add(newChild)) {
                    newChild = graph.getNode(newChild);
                }
                graph.add(new DirectedEdge<>(new Extension<>(tmpParent), newChild, argSet.toString()));
                tmpParent = newChild;
            }
            if(iterationOfSequences.hasNext()) {
                sequence = iterationOfSequences.next();
                iterationOfSets = sequence.iterator();
                tmpParent = iterationOfSets.next(); // get first set (should be root)
                if(!tmpParent.equals(graph.getRoot())) {
                    throw new IllegalArgumentException();
                }
                tmpParent = graph.getRoot();
            } else {
                sequence = null;
            }
        }
        while(sequence != null);

        return graph;
    }

    /**
     * Returns a reasoner for the given semantics.
     * @param semantics a semantics
     * @return a serialisable extension reasoner for the given semantics
     */
    public static SerialisableExtensionReasoner getSerialisableReasonerForSemantics(Semantics semantics){
        return switch (semantics) {
            case CO -> new SerialisedCompleteReasoner();
            case GR -> new SerialisedGroundedReasoner();
            case PR -> new SerialisedPreferredReasoner();
            case ST -> new SerialisedStableReasoner();
            case ADM -> new SerialisedAdmissibleReasoner();
            case UC -> new SerialisedUnchallengedReasoner();
            case SAD -> new SerialisedStronglyAdmissibleReasoner();
            default -> throw new IllegalArgumentException("Semantics is not serialisable: " + semantics);
        };
    }
}