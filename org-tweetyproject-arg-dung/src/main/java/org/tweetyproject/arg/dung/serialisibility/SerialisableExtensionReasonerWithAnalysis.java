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

import java.rmi.NoSuchObjectException;
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
	 * Creates a graph, visualizing the transition states of the serialisation process, which creates all serialisable extensions 
	 * according to the specified semantics of the specified framework.
	 *
	 * @param framework Argumentation framework, for which the extensions shall be computed.
	 * @return Graph showing the serialisation process.
	 */
	public SerialisationGraph getModelsGraph(DungTheory framework) {

		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(framework, initExtension);

		return this.getModelsRecursiveGraph(
				new HashSet<SerialisationGraph>(),
				initState);
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
	 * @param consistencyCheckSet Set of all graphs, computed during the process
	 * @param state Current transition state of the serialisation process.
	 * @return Graph showing the serialisation process, starting with the current state.
	 */
	private SerialisationGraph getModelsRecursiveGraph(
			HashSet<SerialisationGraph> consistencyCheckSet,
			TransitionState state) {
		
		for (SerialisationGraph existingGraph : consistencyCheckSet) {
			if(existingGraph.getRoot().getState() == state) return existingGraph;
		}

		var graph = new SerialisationGraph(new TransitionStateNode(state), this.usedSemantics);
		consistencyCheckSet.add(graph);

		// check whether a construction of an extension is finished
		  if (this.checkTerminationFunction(state)) { 
			  // found final extension
			  graph.addExtension(state.getExtension());
		  }
		 

		Collection<Extension<DungTheory>> newExtensions = this.selectInitialSetsForReduction(state);

		// [TERMINATION CONDITION] - terminates if newExtensions empty -  iterate depth-first through all reductions
		for (Extension<DungTheory> newExt : newExtensions) {
			TransitionState newState = state.transitToNewState(newExt);

			// [RECURSIVE CALL] examine reduced framework
			SerialisationGraph subGraph = this.getModelsRecursiveGraph(consistencyCheckSet, newState);

			try {
				graph.addSubGraph(graph.getRoot(), subGraph, subGraph.getRoot(), newExt.toString());
			} catch (NoSuchObjectException e) {
				// not possible
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		return graph;
	}

	
	

	/**
	 * Adds an analysis of a successive transition state and integrates its values in this objects fields
	 *
	 * @param subAnalysis Analysis of a successive transition state
	 */
	/*
	 * public void integrateSubAnalysis(SerialisationGraph subAnalysis) { //
	 * integrate findings of sub-level in this level's analysis
	 * this.analysesSuccessive.add(subAnalysis);
	 * this.extensionsFound.addAll(subAnalysis.getExtensionsFound()); try {
	 * this.graphResulting } catch (NoSuchObjectException e) {
	 * System.out.println(e.getMessage()); e.printStackTrace(); } }
	 */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	//@Override
	/*public String toString(){
		String printedResult = "Argumentation Framework: " + this.stateExamined.getTheory().toString() + "\n"
				+ "Semantic: " + this.semanticsUsed.toString() + "\n"
				+ "Extensions: " + this.extensionsFound.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + this.graphResulting.toString();
		return printedResult;
	}*/

}
