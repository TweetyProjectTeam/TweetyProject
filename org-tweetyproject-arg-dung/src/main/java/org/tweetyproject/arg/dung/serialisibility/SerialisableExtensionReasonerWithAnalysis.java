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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedPreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedStableReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedUnchallengedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisibility.graph.TransitionStateNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.SimpleGraph;

/**
 * {@inheritDoc}
 *
 * This specialization of the reasoner gives access to all interim results of
 * the computation.
 *
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public abstract class SerialisableExtensionReasonerWithAnalysis extends SerialisableExtensionReasoner {

	/**
	 * Creates a reasoner for the given semantic.
	 * @param semantics Semantics, according to which the reasoner shall derive the extensions
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static SerialisableExtensionReasonerWithAnalysis getSerialisableReasonerForSemantics(Semantics semantics){
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

	protected Semantics usedSemantics;

	/**
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasonerWithAnalysis(Semantics usedSemantics) {
		super();
		this.usedSemantics = usedSemantics;
	}

	/**
	 * Examines the specified framework and computes all extensions, according to this object's semantic.
	 *
	 * @param framework Argumentation framework, for which the extensions shall be computed.
	 * @return Analysis of the framework, containing all found extensions and further information e.g. derivation graph.
	 */
	public SerialisationGraph getModelsWithAnalysis(DungTheory framework) {

		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(framework, initExtension);

		return this.getModelsRecursiveWithAnalysis(
				new HashSet<SerialisationGraph>(),
				initState,
				null);
	}

	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemantic() {
		return this.usedSemantics;
	}

	/**
	 * @param semantics Semantics used to generate the extensions found during the examination.
	 */
	protected void setSemantic(Semantics semantics) {
		this.usedSemantics = semantics;
	}

	/**
	 * Examines recursively the specified state and all states that can be reducted from this one,
	 * until the termination function is satisfied.
	 *
	 * @param consistencyCheckSet Set of all analysis, computed during the process
	 * @param state Current transition state of the serialising process.
	 * @return Analysis of the current state.
	 */
	@SuppressWarnings("unchecked")
	private SerialisationGraph getModelsRecursiveWithAnalysis(
			HashSet<SerialisationGraph> consistencyCheckSet,
			TransitionState state,
			Extension<DungTheory> setInitial) {

		var analysis = SerialisationGraph.getAnalysisByState(consistencyCheckSet, state);
		if(analysis != null ) {
			return analysis;
		}

		var root = new TransitionStateNode(state);
		var graphGenerationProcess = new SimpleGraph<TransitionStateNode>();
		graphGenerationProcess.add(root);
		var currentAnalysis = new SerialisationGraph(
				state, 
				this.usedSemantics, 
				graphGenerationProcess, 
				root, 
				new HashSet<Extension<DungTheory>>(), 
				new HashSet<SerialisationGraph>(), 
				setInitial);
		consistencyCheckSet.add(currentAnalysis);

		// check whether a construction of an extension is finished
		if (this.checkTerminationFunction(state)) {
			// found final extension
			currentAnalysis.addExtensionFound(state.getExtension());
		}

		Collection<Extension<DungTheory>> newExtensions = this.selectInitialSetsForReduction(state);

		// [TERMINATION CONDITION] - terminates if newExtensions empty -  iterate depth-first through all reductions
		for (Extension<DungTheory> newExt : newExtensions) {
			TransitionState newState = state.transitToNewState(newExt);

			// [RECURSIVE CALL] examine reduced framework
			SerialisationGraph subAnalysis = this.getModelsRecursiveWithAnalysis(consistencyCheckSet, newState, newExt);

			currentAnalysis.integrateSubAnalysis(subAnalysis);
		}

		return currentAnalysis;
	}




}
