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
	 * @return null or the analysis of the specified state this transition state.
	 */
	public static TransitionStateAnalysis getAnalysisByState(HashSet<TransitionStateAnalysis> setOfAnalyses, TransitionState state) {
		for (TransitionStateAnalysis analysis : setOfAnalyses) {
			if(analysis.equals(state)) {
				return analysis;
			}
		}
		return null;
	}
	private Semantics semanticsUsed;
	private SimpleGraph<TransitionStateNode> graphResulting;
	private TransitionStateNode root;
	private Collection<Extension<DungTheory>> extensionsFound;
	private HashSet<TransitionStateAnalysis> analysesSuccessive;
	private Extension<DungTheory> setInitial;

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
	 * @param initialSet Set of arguments, which was used to generate the state of this analysis.
	 */
	public TransitionStateAnalysis(
			DungTheory examinedFramework,
			Extension<DungTheory> stateOfConstructedExtension,
			Semantics usedSemantics,
			SimpleGraph<TransitionStateNode> resultingGraph,
			TransitionStateNode root,
			Collection<Extension<DungTheory>> foundExtensions,
			HashSet<TransitionStateAnalysis> subAnalyses,
			Extension<DungTheory> initialSet) {
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

		this.semanticsUsed = usedSemantics;
		this.graphResulting = resultingGraph;
		this.root = root;
		this.extensionsFound = foundExtensions;
		this.analysesSuccessive = subAnalyses;
	}

	/**
	 * Adds an analysis to the set of subAnalyses of this analysis.
	 *
	 * @param newSubAnalysis Analysis, done in reducted sub-frameworks of the current framework.
	 * @return
	 */
	public boolean addSubAnalysis(TransitionStateAnalysis newSubAnalysis) {
		return this.analysesSuccessive.add(newSubAnalysis);
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

		return  this.semanticsUsed.equals(other.semanticsUsed) &&
				this.graphResulting.equals(other.graphResulting) &&
				this.root.equals(other.root) &&
				this.extensionsFound.equals(other.extensionsFound) &&
				this.analysesSuccessive.equals(other.analysesSuccessive) &&
				super.equals(other);
	}

	/**
	 * @return Extensions, which can be generated using the concept of serialisibility.
	 */
	public Collection<Extension<DungTheory>> getExtensions() {
		HashSet<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
		result.addAll(this.extensionsFound);
		return result;
	}

	/**
	 * @return Graph visualizing the build paths, which lead to the finally found extensions.
	 */
	public SimpleGraph<TransitionStateNode> getGraph() {
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
	public Semantics getSemantics() {
		return this.semanticsUsed;
	}

	/**
	 * @return Set of arguments, which was used to generate the state of this analysis.
	 */
	public Extension<DungTheory> getInitialSet() {
		return this.setInitial;
	}

	/**
	 * @return Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public TransitionStateAnalysis[] getSubAnalysis() {
		return this.analysesSuccessive.toArray(new TransitionStateAnalysis[0]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String printedResult = "Argumentation Framework: " + this.getTheory().toString() + "\n"
				+ "Semantic: " + this.semanticsUsed.toString() + "\n"
				+ "Extensions: " + this.extensionsFound.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + this.graphResulting.toString();
		return printedResult;
	}
	
	/**
	 * Adds an analysis of a successive transition state and integrates its values in this objects fields
	 * 
	 * @param subAnalysis Analysis of a successive transition state
	 */
	public void integrateSubAnalysis(TransitionStateAnalysis subAnalysis) {
		// integrate findings of sub-level in this level's analysis
		this.analysesSuccessive.add(subAnalysis);
		this.extensionsFound.addAll(subAnalysis.getExtensions());
		try {
			this.graphResulting.addSubGraph(root, subAnalysis.getGraph(), subAnalysis.getRoot(), this.setInitial.toString());
		} catch (NoSuchObjectException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
