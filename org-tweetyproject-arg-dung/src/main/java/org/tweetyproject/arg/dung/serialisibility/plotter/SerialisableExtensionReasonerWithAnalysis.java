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
package org.tweetyproject.arg.dung.serialisibility.plotter;

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

	protected Semantics usedSemantic;
	
	/**
	 * @param usedSemantic Semantic used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasonerWithAnalysis(Semantics usedSemantic) {
		super();
		this.usedSemantic = usedSemantic;
	}

	/**
	 * @return Semantic used to generate the extensions found during the examination.
	 */
	public Semantics getSemantic() {
		return usedSemantic;
	}

	/**
	 * @param semantic Semantic used to generate the extensions found during the examination.
	 */
	protected void setSemantic(Semantics semantic) {
		this.usedSemantic = semantic;
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

		return getModelsRecursiveWithAnalysis(initState);
	}
	
	/**
     * Creates a reasoner for the given semantic.
     * @param semantics Semantic
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
	 * @param state Current transition state of the serialising process.
	 * @return Analysis of the current state.
	 */
	@SuppressWarnings("unchecked")
	private SerialisableExtensionAnalysis getModelsRecursiveWithAnalysis(TransitionState state) {

		SimpleGraph<ExtensionNode> currentGraph = new SimpleGraph<ExtensionNode>();
		ExtensionNode root = new ExtensionNode(state.getExtension());
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
			SerialisableExtensionAnalysis subAnalysis = getModelsRecursiveWithAnalysis(newState);

			// integrate findings of sub-level in this level's analysis
			subAnalyses.add(subAnalysis);
			foundExtensions.addAll(subAnalysis.getExtensions());
			SimpleGraph<ExtensionNode> subGraph = subAnalysis.getGraph();
			ExtensionNode subRoot = subAnalysis.getRoot();
			currentGraph = addSubGraph(currentGraph, root, subGraph, subRoot, newExt);
		}

		return new SerialisableExtensionAnalysis(state.getTheory(), this.usedSemantic, currentGraph, root, foundExtensions, subAnalyses);
	}

	/**
	 *  Adds a graph as a subgraph in a superior graph
	 * 
	 * @param superGraph Graph in which the sub-graph will be added.
	 * @param superRoot Root of the super-graph
	 * @param subGraph Graph, which will be added to the super-graph
	 * @param subRoot Root of the sub-graph
	 */
	private SimpleGraph<ExtensionNode> addSubGraph(SimpleGraph<ExtensionNode> superGraph, ExtensionNode superRoot,
			SimpleGraph<ExtensionNode> subGraph, ExtensionNode subRoot, Extension<DungTheory> reductExtension ) {
		superGraph.addAll(subGraph.getNodes());
		superGraph.addAllEdges( (Collection<Edge<ExtensionNode>>) subGraph.getEdges());
		superGraph.add(new DirectedEdge<ExtensionNode>(superRoot,subRoot,reductExtension.toString()));
		
		return superGraph;
	}
}
