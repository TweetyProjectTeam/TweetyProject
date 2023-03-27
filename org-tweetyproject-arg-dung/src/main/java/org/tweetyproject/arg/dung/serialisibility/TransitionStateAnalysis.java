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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.plotter.TransitionStateNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;
import org.tweetyproject.graphs.SimpleGraph;

/**
 * This class represents a transition state {@link Transitionstate} which has been further analyzed,
 * regarding the possible serializing of extensions rooted from this state.
 *
 *
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation. Computational Models of Argument (2022) DOI: 10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class TransitionStateAnalysis extends TransitionState {

	/**
	 * Searches in a specified set for a analysis of the specified transition state.
	 *
	 * @param setOfAnalyses Set, which is to check if it contains a analysis of the specified transition state.
	 * @param state Transition state, which is used as a condition to find the analysis to search for.
	 * @return NULL or the analysis of the specified state this transition state.
	 */
	public static TransitionStateAnalysis getAnalysisByState(HashSet<TransitionStateAnalysis> setOfAnalyses, TransitionState state) {
		for (TransitionStateAnalysis analysis : setOfAnalyses) {
			if(analysis.equals(state)) {
				return analysis;
			}
		}
		return null;
	}
	private Semantics usedSemantics;
	private SimpleGraph<TransitionStateNode> resultingGraph;
	private TransitionStateNode root;
	private Collection<Extension<DungTheory>> foundExtensions;
	private HashSet<TransitionStateAnalysis> subAnalyses;

	/**
	 * Creates an object containing all relevant findings from examining the given framework for serialisable extensions.
	 *
	 * @param examinedFramework Argumentation framework, which has been examined.
	 * @param stateOfConstructedExtension Extension, which is about to be constructed.
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 * @param resultingGraph Graph visualizing the build paths, which lead to the finally found extensions.
	 * @param root Node with whom the processing of the examined framework started
	 * @param foundExtensions Extensions, which can be generated using the concept of serialisibility.
	 * @param subAnalyses Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public TransitionStateAnalysis(
			DungTheory examinedFramework,
			Extension<DungTheory> stateOfConstructedExtension,
			Semantics usedSemantics,
			SimpleGraph<TransitionStateNode> resultingGraph,
			TransitionStateNode root,
			Collection<Extension<DungTheory>> foundExtensions,
			HashSet<TransitionStateAnalysis> subAnalyses) {
		super(examinedFramework, stateOfConstructedExtension);

		{
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

		this.usedSemantics = usedSemantics;
		this.resultingGraph = resultingGraph;
		this.root = root;
		this.foundExtensions = foundExtensions;
		this.subAnalyses = subAnalyses;
	}

	/**
	 * Adds an analysis to the set of subAnalyses of this analysis.
	 *
	 * @param newSubAnalysis Analysis, done in reducted sub-frameworks of the current framework.
	 * @return
	 */
	public boolean addSubAnalysis(TransitionStateAnalysis newSubAnalysis) {
		return this.subAnalyses.add(newSubAnalysis);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if(obj.getClass() == super.getClass()) {
			return super.equals(obj);
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		TransitionStateAnalysis other = (TransitionStateAnalysis) obj;

		return  this.usedSemantics.equals(other.usedSemantics) &&
				this.resultingGraph.equals(other.resultingGraph) &&
				this.root.equals(other.root) &&
				this.foundExtensions.equals(other.foundExtensions) &&
				this.subAnalyses.equals(other.subAnalyses) &&
				super.equals(other);
	}

	/**
	 * @return Extensions, which can be generated using the concept of serialisibility.
	 */
	public Collection<Extension<DungTheory>> getExtensions() {
		return this.foundExtensions;
	}

	/**
	 * @return Graph visualizing the build paths, which lead to the finally found extensions.
	 */
	public SimpleGraph<TransitionStateNode> getGraph() {
		return this.resultingGraph;
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
	public Semantics getSemantics() {
		return this.usedSemantics;
	}

	/**
	 * @return Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public TransitionStateAnalysis[] getSubAnalysis() {
		return this.subAnalyses.toArray(new TransitionStateAnalysis[0]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String printedResult = "Argumentation Framework: " + this.getTheory().toString() + "\n"
				+ "Semantic: " + this.usedSemantics.toString() + "\n"
				+ "Extensions: " + this.foundExtensions.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + this.resultingGraph.toString();
		return printedResult;
	}

}
