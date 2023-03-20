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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

/**
 * This class represents the analysis result of an argumentation framework (called {@link org.tweetyproject.arg.dung.syntax.DungTheory}),
 * which has been examined for serialisable extensions.
 * 
 * 
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation. Computational Models of Argument (2022) DOI: 10.3233/FAIA220143
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisableExtensionAnalysis {
	
	private DungTheory examinedFramework; 
	private Semantics usedSemantics;
	private SimpleGraph<ExtensionNode> resultingGraph;
	private ExtensionNode root;
	private Collection<Extension<DungTheory>> foundExtensions;
	private HashSet<SerialisableExtensionAnalysis> subAnalyses;
	
	/**
	 * Creates an object containing all relevant findings from examining the given framework for serialisable extensions.
	 * 
	 * @param examinedFramework Argumentation framework, which has been examined.
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 * @param resultingGraph Graph visualizing the build paths, which lead to the finally found extensions.
	 * @param root Node with whom the processing of the examined framework started
	 * @param foundExtensions Extensions, which can be generated using the concept of serialisibility.
	 * @param subAnalyses Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public SerialisableExtensionAnalysis(
			DungTheory examinedFramework, 
			Semantics usedSemantics,
			SimpleGraph<ExtensionNode> resultingGraph, 
			ExtensionNode root,
			Collection<Extension<DungTheory>> foundExtensions,
			HashSet<SerialisableExtensionAnalysis> subAnalyses) {
		super();
		
		{
			if(examinedFramework == null) throw new NullPointerException("examinedFramework");
			if(resultingGraph == null) throw new NullPointerException("resultingGraph");
			if(root == null) throw new NullPointerException("root");
			if(foundExtensions == null) throw new NullPointerException("foundExtensions");
			if(subAnalyses == null) throw new NullPointerException("subAnalyses");
		}
				
		this.examinedFramework = examinedFramework;
		this.usedSemantics = usedSemantics;
		this.resultingGraph = resultingGraph;
		this.root = root;
		this.foundExtensions = foundExtensions;
		this.subAnalyses = subAnalyses;
	}
	
	

	/**
	 * @return Argumentation framework, which has been examined.
	 */
	public DungTheory getFramework() {
		return examinedFramework;
	}
	
	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemantics() {
		return usedSemantics;
	}

	/**
	 * @return Graph visualizing the build paths, which lead to the finally found extensions.
	 */
	public SimpleGraph<ExtensionNode> getGraph() {
		return resultingGraph;
	}

	/**
	 * @return Extensions, which can be generated using the concept of serialisibility.
	 */
	public Collection<Extension<DungTheory>> getExtensions() {
		return foundExtensions;
	}
	
	/**
	 * @return Node with whom the processing of the examined framework started
	 */
	public ExtensionNode getRoot() {
		return root;
	}
	
	/**
	 * @return Analyses, done in reducted sub-frameworks of the current framework.
	 */
	public SerialisableExtensionAnalysis[] getSubAnalysis() {
		return subAnalyses.toArray(new SerialisableExtensionAnalysis[0]);
	}

	/**
	 * Adds an analysis to the set of subAnalyses of this analysis.
	 * 
	 * @param newSubAnalysis Analysis, done in reducted sub-frameworks of the current framework.
	 * @return
	 */
	public boolean addSubAnalysis(SerialisableExtensionAnalysis newSubAnalysis) {
		return this.subAnalyses.add(newSubAnalysis);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String printedResult = "Argumentation Framework: " + this.examinedFramework.toString() + "\n"
				+ "Semantic: " + this.usedSemantics.toString() + "\n"
				+ "Extensions: " + this.foundExtensions.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + this.resultingGraph.toString();
		return printedResult;
	}
}
