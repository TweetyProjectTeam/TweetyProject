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
package org.tweetyproject.arg.dung.serialisibility.graph;

import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.TransitionState;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;
import org.tweetyproject.math.matrix.Matrix;

/**
 * This class represents an object summarizing all information gathered during an analysis of a {@link TransitionState}, regarding serializable extensions.
 *
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation. Computational Models of Argument (2022) DOI: 10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationGraph implements Graph<TransitionStateNode> {

	/**
	 * Searches in a specified set for a analysis of the specified transition state.
	 *
	 * @param setOfAnalyses Set, which is to check if it contains a analysis of the specified transition state.
	 * @param state Transition state, which is used as a condition to find the analysis to search for.
	 * @return null or the analysis of the specified state this transition state.
	 */
	public static SerialisationGraph getAnalysisByState(HashSet<SerialisationGraph> setOfAnalyses, TransitionState state) {
		for (SerialisationGraph analysis : setOfAnalyses) {
			if(analysis.getStateExamined().equals(state)) {
				return analysis;
			}
		}
		return null;
	}
	private TransitionState stateExamined;
	private Semantics semanticsUsed;
	private SimpleGraph<TransitionStateNode> graphResulting;
	private TransitionStateNode root;
	private Collection<Extension<DungTheory>> extensionsFound;
	private HashSet<SerialisationGraph> analysesSuccessive;
	private Extension<DungTheory> setInitial;
	private String title;

	/**
	 * Creates an object containing all relevant findings from examining the given framework for serialisable extensions.
	 *
	 * @param examinedState Transition state, which has been examined.
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 * @param resultingGraph Graph visualizing the build paths, which lead to the finally found extensions.
	 * @param root Node with whom the processing of the examined framework started
	 * @param foundExtensions Extensions, which can be generated using the concept of serialisibility.
	 * @param subAnalyses Analyses, done in reducted sub-frameworks of the current framework.
	 * @param initialSet Set of arguments, which was used to generate the state of this analysis.
	 */
	public SerialisationGraph(
			TransitionState examinedState,
			Semantics usedSemantics,
			SimpleGraph<TransitionStateNode> resultingGraph,
			TransitionStateNode root,
			Collection<Extension<DungTheory>> foundExtensions,
			HashSet<SerialisationGraph> subAnalyses,
			Extension<DungTheory> initialSet) {

		{
			if(examinedState == null) {
				throw new NullPointerException("examinedState");
			}
			if(resultingGraph == null) {
				throw new NullPointerException("resultingGraph");
			}
			if(root == null) {
				throw new NullPointerException("root");
			}
			if(foundExtensions == null) {
				throw new NullPointerException("foundExtensions");
			}
			if(subAnalyses == null) {
				throw new NullPointerException("subAnalyses");
			}
		}

		this.stateExamined = examinedState;
		this.semanticsUsed = usedSemantics;
		this.graphResulting = resultingGraph;
		this.root = root;
		this.extensionsFound = foundExtensions;
		this.analysesSuccessive = subAnalyses;
		this.setInitial = initialSet;
	}

	/**
	 * Adds a completely serialized extension to the set of found extensions of the transition state associated with this object.
	 * @param newFinishedExtension Extension, which process of serializing is finished.
	 * @return True if the extension could be added to the set.
	 */
	public boolean addExtensionFound(Extension<DungTheory> newFinishedExtension) {
		return this.extensionsFound.add(newFinishedExtension);
	}

	/**
	 * Adds an analysis to the set of subAnalyses of this analysis.
	 *
	 * @param newSubAnalysis Analysis, done in reducted sub-frameworks of the current framework.
	 * @return True if successfully executed.
	 */
	public boolean addSubAnalysis(SerialisationGraph newSubAnalysis) {
		return this.analysesSuccessive.add(newSubAnalysis);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SerialisationGraph)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		SerialisationGraph other = (SerialisationGraph) obj;

		return  this.semanticsUsed.equals(other.semanticsUsed) &&
				this.graphResulting.equals(other.graphResulting) &&
				this.root.equals(other.root) &&
				this.extensionsFound.equals(other.extensionsFound) &&
				this.analysesSuccessive.equals(other.analysesSuccessive) &&
				super.equals(other);
	}

	/**
	 * @return Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public SerialisationGraph[] getAnalysesSuccessive() {
		return this.analysesSuccessive.toArray(new SerialisationGraph[0]);
	}

	/**
	 * @return Extensions, which can be generated using the concept of serialisibility.
	 */
	public Collection<Extension<DungTheory>> getExtensionsFound() {
		HashSet<Extension<DungTheory>> result = new HashSet<>();
		result.addAll(this.extensionsFound);
		return result;
	}


	/**
	 * @return Graph visualizing the build paths, which lead to the finally found extensions.
	 */
	public SimpleGraph<TransitionStateNode> getGraphResulting() {
		return this.graphResulting;
	}

	/**
	 * @return Node with whom the processing of the examined framework started
	 */
	public TransitionStateNode getRoot() {
		return this.root;
	}

	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemanticsUsed() {
		return this.semanticsUsed;
	}

	/**
	 * @return Set of arguments, which was used to generate the state of this analysis.
	 */
	public Extension<DungTheory> getSetInitial() {
		return this.setInitial;
	}

	/**
	 * @return Transition state, which has been examined.
	 */
	public TransitionState getStateExamined() {
		return this.stateExamined;
	}

	/**
	 *
	 * @return Title, that describes the analysis.
	 */
	public String getTitle() {
		return this.title != null ? this.title : "Serializability Analysis";
	}

	/**
	 * Adds an analysis of a successive transition state and integrates its values in this objects fields
	 *
	 * @param subAnalysis Analysis of a successive transition state
	 */
	public void integrateSubAnalysis(SerialisationGraph subAnalysis) {
		// integrate findings of sub-level in this level's analysis
		this.analysesSuccessive.add(subAnalysis);
		this.extensionsFound.addAll(subAnalysis.getExtensionsFound());
		try {
			this.graphResulting.addSubGraph(this.root, subAnalysis.getGraphResulting(), subAnalysis.getRoot(), subAnalysis.setInitial.toString());
		} catch (NoSuchObjectException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param title Title, that describes the analysis.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String printedResult = "Argumentation Framework: " + this.stateExamined.getTheory().toString() + "\n"
				+ "Semantic: " + this.semanticsUsed.toString() + "\n"
				+ "Extensions: " + this.extensionsFound.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + this.graphResulting.toString();
		return printedResult;
	}

	@Override
	public GeneralGraph<TransitionStateNode> getRestriction(Collection<TransitionStateNode> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(TransitionStateNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(GeneralEdge<TransitionStateNode> edge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<TransitionStateNode> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean areAdjacent(TransitionStateNode a, TransitionStateNode b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GeneralEdge<TransitionStateNode> getEdge(TransitionStateNode a, TransitionStateNode b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends GeneralEdge<? extends TransitionStateNode>> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<TransitionStateNode> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<TransitionStateNode> getChildren(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TransitionStateNode> getParents(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsDirectedPath(TransitionStateNode node1, TransitionStateNode node2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<TransitionStateNode> getNeighbors(TransitionStateNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<TransitionStateNode> getComplementGraph(int selfloops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<TransitionStateNode>> getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Graph<TransitionStateNode>> getSubgraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSelfLoops() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		// TODO Auto-generated method stub
		return false;
	}

}
