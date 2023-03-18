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
import org.tweetyproject.arg.dung.semantics.Extension;
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

	public SerialisableExtensionAnalysis getModelsWithAnalysis(DungTheory framework) {

		Extension<DungTheory> initExtension = new Extension<DungTheory>();
		TransitionState initState = new TransitionState(framework, initExtension);

		return getModelsRecursiveWithAnalysis(initState);
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

		// [TERMINATION CONDITION] check whether the current state is acceptable
		if (this.checkTerminationFunction(state)) {
			// found final extension
			foundExtensions.add(state.getExtension());
			return new SerialisableExtensionAnalysis(state.getTheory(), currentGraph, root, foundExtensions, subAnalyses);
		}

		Collection<Extension<DungTheory>> newExtensions = selectInitialSetsForReduction(state);

		// iterate depth-first through all reductions
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

		return new SerialisableExtensionAnalysis(state.getTheory(), currentGraph, root, foundExtensions, subAnalyses);
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
