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
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionState;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionStateNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * Abstract class for computing extensions via a serialised transition system
 *
 * to implement this class, you need to define a selection and a termination function
 * Selection function a(UA, UC, C):     selects and returns a subset of the initial sets
 * Termination function b((AF, S)):      If the given state satisfies a specific condition, its extension may be accepted by the associated serialised semantics
 *
 * @author Lars Bengel
 * @author Julian Sander
 */
public abstract class SerialisableExtensionReasoner extends AbstractExtensionReasoner {
	/**
	 * Creates a reasoner for the given semantics.
	 * @param semantics a semantics
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static SerialisableExtensionReasoner getSerialisableReasonerForSemantics(Semantics semantics){
		return switch (semantics) {
		case CO -> new SerialisedCompleteReasoner();
		case GR -> new SerialisedGroundedReasoner();
		case PR -> new SerialisedPreferredReasoner();
		case ST -> new SerialisedStableReasoner();
		case ADM -> new SerialisedAdmissibleReasoner();
		case UC -> new SerialisedUnchallengedReasoner();
		default -> throw new IllegalArgumentException("Unknown semantics.");
		};
	}

	protected Semantics semantics;

	/**
	 * @param semantics Semantics used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasoner(Semantics semantics) {
		super();
		this.semantics = semantics;
	}

	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// not supported
		return null;
	}

	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(bbase, initExtension);
		TransitionStateNode root = new TransitionStateNode(initState);
		
		return this.getModelsRecursive(
				initState,
				initExtension,
				new SerialisationSequence() ,
				root,
				new HashSet<TransitionState>(),
				true,
				new HashSet<SerialisationSequence>(),
				new SerialisationGraph(root, semantics));
	}

	/**
	 * Creates a graph, visualizing the transition states of the serialisation process, which creates all serialisable extensions
	 * according to the specified semantics of the specified framework.
	 *
	 * @param bbase Argumentation framework, for which the extensions shall be computed.
	 * @return Graph showing the serialisation process.
	 */
	public SerialisationGraph getModelsGraph(DungTheory bbase) {

		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(bbase, initExtension);
		TransitionStateNode root = new TransitionStateNode(initState);
		SerialisationGraph output = new SerialisationGraph(root, semantics);
		
		this.getModelsRecursive(
				initState,
				initExtension,
				new SerialisationSequence() ,
				root,
				new HashSet<TransitionState>(),
				true,
				new HashSet<SerialisationSequence>(),
				output);
		
		return output;
	}

	/**
	 * Creates a set of sequences, each sequences is showing the serialisation path to generate the final extension regarding the semantics of the reasoner.
	 * @param bbase Argumentation framework, for which the extensions shall be computed.
	 * @return Set of sequences tracing the serialisation process for each final extension
	 */
	public HashSet<SerialisationSequence> getModelsSequences(DungTheory bbase){
		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(bbase, initExtension);
		TransitionStateNode root = new TransitionStateNode(initState);
		HashSet<SerialisationSequence> output = new HashSet<>();
		
		this.getModelsRecursive(
				initState,
				initExtension,
				new SerialisationSequence() ,
				root,
				new HashSet<TransitionState>(),
				true,
				output,
				new SerialisationGraph(root, semantics));
		return output;
	}

	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemantics() {
		return this.semantics;
	}

	/**
	 * select a subset of the initial sets of the theory, i.e. the possible successor states
	 * @param unattacked the set of unattacked initial sets
	 * @param unchallenged the set of unchallenged initial sets
	 * @param challenged the set of challenged initial sets
	 * @return a subset of the initial sets of the theory, selected via a specific criteria
	 */
	public abstract Collection<Extension<DungTheory>> selectionFunction(
			Collection<Extension<DungTheory>> unattacked,
			Collection<Extension<DungTheory>> unchallenged,
			Collection<Extension<DungTheory>> challenged);

	/**
	 * Checks whether the current state satisfies some condition, i.e. its extension can be accepted by the corresponding semantics
	 * @param state the current state of the transition system
	 * @return true, if the state satisfies the condition
	 */
	public abstract boolean terminationFunction(TransitionState state);

	/**
	 * Selects, by using the specified selection function of the reasoner, those initial sets which should be used to reduct the framework
	 * in order to transit to a new state
	 *
	 * @param state current transition state of the process
	 * @return candidate sets S' via the selection function
	 */
	protected Collection<Extension<DungTheory>> selectInitialSetsForReduction(TransitionState state) {
		// compute the candidate sets S' via the selection function
		// compute all initial sets, sorted in the three categories unattacked, unchallenged, challenged
		Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(state.getTheory());
		// select initial sets according to given specific semantic
		Collection<Extension<DungTheory>> newExts = this.selectionFunction(initialSets.get("unattacked"), initialSets.get("unchallenged"), initialSets.get("challenged"));
		return newExts;
	}

	/**
	 * @param semantics Semantics used to generate the extensions
	 */
	protected void setSemantic(Semantics semantics) {
		this.semantics = semantics;
	}
	
	private SerialisationSequence createNewSequence(TransitionState in_State,
			SerialisationSequence in_ParentSequence) {
		SerialisationSequence newSequence = new SerialisationSequence();
		newSequence.addAll(in_ParentSequence);
		newSequence.add(in_State.getExtension());
		return newSequence;
	}
	
	private TransitionStateNode createNewNode(TransitionState state, SerialisationGraph graph,
			TransitionStateNode parentNode, Extension<DungTheory> setLabel) {
		var newNode = new TransitionStateNode(state);
		graph.add(newNode);
		graph.add(new DirectedEdge<TransitionStateNode>(parentNode, newNode, setLabel.toString()));
		return newNode;
	}

	/**
	 * recursively computes all possible states of the transition system defined by the selection and termination function
	 * the transition system is traversed with a depth-first search approach
	 * @param in_State the current state of the transition system
	 * @param in_ParentSequence Sequence of extensions of the transition states leading to this state
	 * @param out_SequencesFound Out parameter, defining the sequences of all final extensions
	 * @return Collection of all final extension regarding the semantics of the reasoner
	 */
	private Collection<Extension<DungTheory>> getModelsRecursive(
			TransitionState in_State,
			Extension<DungTheory> in_InitialSet,
			SerialisationSequence in_ParentSequence,
			TransitionStateNode in_ParentNode,
			HashSet<TransitionState> inout_VisitedStates,
			boolean in_IsFirstRun,
			HashSet<SerialisationSequence> out_SequencesFound,
			SerialisationGraph out_Graph) {
		Collection<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
		SerialisationSequence newSequence = createNewSequence(in_State, in_ParentSequence);
		TransitionStateNode newNode;
		
		if(in_IsFirstRun) {
			// only runs once in first call of method
			newNode = in_ParentNode; // necessary since root is already created
		}else {
			newNode = createNewNode(in_State, out_Graph, in_ParentNode, in_InitialSet);
		}
		
		// check whether the current state is acceptable, if yes add to results
		if (this.terminationFunction(in_State)) {
			out_SequencesFound.add(newSequence);
			result.add(in_State.getExtension());
			out_Graph.addExtension(in_State.getExtension());
		}
		inout_VisitedStates.add(in_State);
		
		Collection<Extension<DungTheory>> newExts = this.selectInitialSetsForReduction(in_State);

		// recursively compute successor states
		// iterate depth-first through all initial sets (and hence their induced states) and add all found final extensions
		for (Extension<DungTheory> newExt: newExts) {
			TransitionState newState = in_State.transitToNewState(newExt, inout_VisitedStates);
			if(inout_VisitedStates.contains(newState)) continue; // do not visit states twice
			// compute possible extension resulting from the reduced framework in the new state
			result.addAll(this.getModelsRecursive(
					newState, newExt, newSequence, newNode,
					inout_VisitedStates, false,
					out_SequencesFound, out_Graph));
		}
		return result;
	}
}
