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
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionAnalysisNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;
import org.tweetyproject.graphs.*;

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

	protected Semantics usedSemantics;
	
	/**
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasonerWithAnalysis(Semantics usedSemantics) {
		super();
		this.usedSemantics = usedSemantics;
	}

	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemantic() {
		return usedSemantics;
	}

	/**
	 * @param semantics Semantics used to generate the extensions found during the examination.
	 */
	protected void setSemantic(Semantics semantics) {
		this.usedSemantics = semantics;
	}

	/**
	 * Examines the specified framework and computes all extensions, according to this object's semantic.
	 * 
	 * @param framework Argumentation framework, for which the extensions shall be computed.
	 * @return Analysis of the framework, containing all found extensions and further information e.g. derivation graph.
	 */
	public SerialisableExtensionAnalysis getModelsWithAnalysis(DungTheory framework) {

		Extension<DungTheory> initExtension = new Extension<DungTheory>();
		TransitionState initState = new TransitionState(framework, initExtension);

		return getModelsRecursiveWithAnalysis(new HashSet<SerialisableExtensionAnalysisNode>(), initState);
	}
	
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

	/**
	 * Examines recursively the specified state and all states that can be reducted from this one, 
	 * until the termination function is satisfied.
	 * 
	 * @param consistencyCheckSet Set of all nodes, visited during the process
	 * @param state Current transition state of the serialising process.
	 * @return Analysis of the current state.
	 */
	@SuppressWarnings("unchecked")
	private SerialisableExtensionAnalysis getModelsRecursiveWithAnalysis(
			HashSet<SerialisableExtensionAnalysisNode> consistencyCheckSet,
			TransitionState state) {

		SimpleGraph<SerialisableExtensionAnalysisNode> currentGraph = new SimpleGraph<SerialisableExtensionAnalysisNode>();
		
		SerialisableExtensionAnalysisNode root = getNodeByState(consistencyCheckSet, state);
		// if this state (and all of subordinates) has already been examined, and is hence a node in the set, abort function
		if(root != null) {
			if(root.getAnalysis() != null) return root.getAnalysis(); // theoretical case: root is called from one of it's children, hence root has no analysis yet.
			else return null; // return null, to break circle (see theoretical case above)
		}
		else {
			root = new SerialisableExtensionAnalysisNode(state);
			consistencyCheckSet.add(root);
		}
		currentGraph.add(root);
		
		HashSet<Extension<DungTheory>> foundExtensions = new HashSet<Extension<DungTheory>>();
		HashSet<SerialisableExtensionAnalysis> subAnalyses = new HashSet<SerialisableExtensionAnalysis>();

		// check whether a construction of an extension is ﬁnished
		if (this.checkTerminationFunction(state)) {
			// found final extension
			foundExtensions.add(state.getExtension());
		}

		Collection<Extension<DungTheory>> newExtensions = selectInitialSetsForReduction(state);

		// [TERMINATION CONDITION] - terminates if newExtensions empty -  iterate depth-first through all reductions
		for (Extension<DungTheory> newExt : newExtensions) {
			// reduct framework of the current state by transforming to new state for given extension
			TransitionState newState = state.getNext(newExt);

			// [RECURSIVE CALL] examine reduced framework
			SerialisableExtensionAnalysis subAnalysis = getModelsRecursiveWithAnalysis(consistencyCheckSet, newState);

			// integrate findings of sub-level in this level's analysis
			subAnalyses.add(subAnalysis);
			foundExtensions.addAll(subAnalysis.getExtensions());
			SimpleGraph<SerialisableExtensionAnalysisNode> subGraph = subAnalysis.getGraph();
			SerialisableExtensionAnalysisNode subRoot = subAnalysis.getRoot();
			currentGraph = addSubGraph(currentGraph, root, subGraph, subRoot, newExt);
		}
		
		SerialisableExtensionAnalysis currentAnalysis = new SerialisableExtensionAnalysis(
				state.getTheory(), this.usedSemantics, currentGraph, root, foundExtensions, subAnalyses);
		root.setAnalysis(currentAnalysis);
		return currentAnalysis;
	}

	/**
	 *  Adds a graph as a subgraph in a superior graph
	 * 
	 * @param superGraph Graph in which the sub-graph will be added.
	 * @param superRoot Root of the super-graph
	 * @param subGraph Graph, which will be added to the super-graph
	 * @param subRoot Root of the sub-graph
	 */
	private SimpleGraph<SerialisableExtensionAnalysisNode> addSubGraph(SimpleGraph<SerialisableExtensionAnalysisNode> superGraph, SerialisableExtensionAnalysisNode superRoot,
			SimpleGraph<SerialisableExtensionAnalysisNode> subGraph, SerialisableExtensionAnalysisNode subRoot, Extension<DungTheory> reductExtension ) {
		superGraph.addAll(subGraph.getNodes());
		superGraph.addAllEdges( (Collection<Edge<SerialisableExtensionAnalysisNode>>) subGraph.getEdges());
		superGraph.add(new DirectedEdge<SerialisableExtensionAnalysisNode>(superRoot,subRoot,reductExtension.toString()));
		
		return superGraph;
	}
	
	/**
	 * Searches in a specified set for a node, which holds the specified transition state.
	 * 
	 * @param setOfNodes Set, which is to check if it contains a node with the specified transition state.
	 * @param state Transition state, which is used as a condition to find the node to search for.
	 * @return NULL or the node in the specified graph, which contains this transition state.
	 */
	private SerialisableExtensionAnalysisNode getNodeByState(HashSet<SerialisableExtensionAnalysisNode> setOfNodes, TransitionState state) {
		for (SerialisableExtensionAnalysisNode node : setOfNodes) {
			if(node.getState().equals(state)) return node;
		}
		return null;
	}
}
