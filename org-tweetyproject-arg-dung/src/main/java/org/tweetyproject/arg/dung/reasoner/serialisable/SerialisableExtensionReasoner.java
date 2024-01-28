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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DirectedEdge;

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
public abstract class SerialisableExtensionReasoner extends AbstractExtensionReasoner {
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

	protected Semantics semantics;

	/**
	 * Constructs a serialisable extension reasoner for the given semantics
	 * @param semantics Semantics used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasoner(Semantics semantics) {
		super();
		this.semantics = semantics;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// not supported
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		var initSeq = new SerialisationSequence();
		initSeq.add(new Extension<>());
		return this.computeExtension(this.getModelsRecursive( bbase, initSeq));
	}

	/**
	 * Creates a graph, visualizing the transition states of the serialisation process, which creates all serialisable extensions
	 * according to the specified semantics of the specified framework.
	 *
	 * @param bbase Argumentation framework, for which the extensions shall be computed.
	 * @return Graph showing the serialisation process.
	 */
	public SerialisationGraph getModelsGraph(DungTheory bbase) {
		var initSeq = new SerialisationSequence();
		initSeq.add(new Extension<>());
		return this.computeGraph(this.getModelsRecursive( bbase, initSeq));
	}

	/**
	 * Creates a set of sequences, each sequences is showing the serialisation path to generate the final extension regarding 
	 * the semantics of the reasoner.
	 * @param bbase Argumentation framework, for which the extensions shall be computed.
	 * @return Set of sequences tracing the serialisation process for each final extension
	 */
	public HashSet<SerialisationSequence> getModelsSequences(DungTheory bbase){
		var initSeq = new SerialisationSequence();
		initSeq.add(new Extension<>());
		return this.getModelsRecursive( bbase, initSeq);
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
	public abstract Collection<Extension<DungTheory>> selectionFunction(
			Collection<Extension<DungTheory>> unattacked,
			Collection<Extension<DungTheory>> unchallenged,
			Collection<Extension<DungTheory>> challenged);

	/**
	 * Determines whether the current state represents an extension wrt. the semantics of the reasoner or not.
	 * @param reducedFramework The current framework of the transition system
	 * @param constructedExtension The extension constructed so far
	 * @return true, if the state satisfies the condition of the semantics
	 */
	public abstract boolean terminationFunction(DungTheory reducedFramework, Extension<DungTheory> constructedExtension);

	/**
	 * Selects, by using the specified selection function of the reasoner, those initial sets which should be used to reduct the framework
	 * in order to transit to a new state
	 *
	 * @param framework current framework of the process
	 * @return candidate sets S' via the selection function
	 */
	protected Collection<Extension<DungTheory>> selectInitialSetsForReduction(DungTheory framework) {
		// compute the candidate sets S' via the selection function
		// compute all initial sets, sorted in the three categories unattacked, unchallenged, challenged
		Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(framework);
		// select initial sets according to the given semantics
		Collection<Extension<DungTheory>> newExts = this.selectionFunction(initialSets.get("unattacked"), initialSets.get("unchallenged"), initialSets.get("challenged"));
		return newExts;
	}

	/**
	 * Transforms a given set of serialisation sequences into the corresponding set of extensions
	 * @param sequences a set of serialisation sequences
	 * @return the set of extensions that correspond to some serialisation sequence in the given set
	 */
	private Collection<Extension<DungTheory>> computeExtension(HashSet<SerialisationSequence> sequences){
		var output = new HashSet<Extension<DungTheory>>();

		for (SerialisationSequence sequence : sequences) {
			output.add(sequence.getExtension());
		}
		return output;
	}

	/**
	 * Constructs a graph from the given set of serialisation sequences
	 * @param sequences a set of serialisation sequences
	 * @return the serialisation graph constructed from the given serialisation sequences
	 */
	private SerialisationGraph computeGraph(HashSet<SerialisationSequence> sequences) {
		if(sequences.isEmpty())return new SerialisationGraph(this.semantics, new HashSet<>());
		
		var iterationOfSequences = sequences.iterator();
		var sequence = iterationOfSequences.next(); // get first sequence
		var iterationOfSets = sequence.iterator();
		var tmpParent = iterationOfSets.next(); // get first set
		SerialisationGraph graph = new SerialisationGraph(tmpParent, this.semantics, this.computeExtension(sequences));

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
				graph.add(new DirectedEdge<>(tmpParent, newChild, argSet.toString()));
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
	 * recursively computes all possible states of the transition system defined by the selection and termination function
	 * the transition system is traversed with a depth-first search approach 
	 * @param currentFramework *description missing*
	 * @param parentSequence *description missing*
	 * @return *description missing*
	 */
	private HashSet<SerialisationSequence> getModelsRecursive(DungTheory currentFramework, SerialisationSequence parentSequence) {

		var output = new HashSet<SerialisationSequence>();

		// check whether the current state is acceptable, if yes add to results
		if (this.terminationFunction(currentFramework, parentSequence.getExtension())) {
			output.add(parentSequence);
		}

		Collection<Extension<DungTheory>> initSets = this.selectInitialSetsForReduction(currentFramework);

		// recursively compute successor states
		// iterate depth-first through all initial sets (and hence their induced states) and add all found final extensions
		for (Extension<DungTheory> initSet: initSets) {
			DungTheory reductedFramework = currentFramework.getReduct(initSet);

			SerialisationSequence newSequence = new SerialisationSequence(parentSequence);
			newSequence.add(initSet);
			// compute possible extension resulting from the reduced framework in the new state
			output.addAll(this.getModelsRecursive(reductedFramework, newSequence));
		}
		return output;
	}



}
