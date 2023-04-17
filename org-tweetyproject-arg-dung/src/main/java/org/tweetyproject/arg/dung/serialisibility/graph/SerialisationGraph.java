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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.TransitionState;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.SimpleGraph;


/**
 * This class represents a graph visualising the different {@link TransitionState} during the generation process of serializable extensions.
 *
 * @see SimpleGraph
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation. Computational Models of Argument (2022) DOI: 10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationGraph extends SimpleGraph<TransitionStateNode> {

	private TransitionStateNode root;
	private HashSet<Extension<DungTheory>> extensionsFound = new HashSet<Extension<DungTheory>>();
	private Semantics semanticUsed;

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param graph Graph, showing a serialisation process.
	 * @param root Node with whom the processing of the examined framework started
	 * @param semanticsUsed Semantics used for the serialisation process.
	 */
	public SerialisationGraph(Graph<TransitionStateNode> graph, TransitionStateNode root, Semantics semanticsUsed) {
		super(graph);
		this.init(root, semanticsUsed);
	}

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param root Node with whom the processing of the examined framework started
	 * @param semanticsUsed Semantics used for the serialisation process.
	 */
	public SerialisationGraph(TransitionStateNode root, Semantics semanticsUsed) {
		super();
		this.init(root, semanticsUsed);
	}
	
	private void init(TransitionStateNode root, Semantics semanticsUsed) {
		this.initRoot(root);
		this.semanticUsed = semanticsUsed;
	}

	public boolean addExtension(Extension<DungTheory> extension) {
		return this.extensionsFound.add(extension);
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

		return  this.root.equals(other.root) &&
				super.equals(other);
	}
	
	/**
	 * @return Array of extensions, that have been found during the process shown by this graph.
	 */
	public Collection<Extension<DungTheory>> getExtensions(){
		HashSet<Extension<DungTheory>> output = new HashSet<Extension<DungTheory>>();
		output.addAll(extensionsFound);
		return output;
	}

	/**
	 * @return Node with whom the processing of the examined framework started
	 */
	public TransitionStateNode getRoot() {
		return this.root;
	}
	
	/**
	 * @return Semantics used for the serialisation process.
	 */
	public Semantics getSemantics() {
		return this.semanticUsed;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String printedResult = "Extensions: " + this.extensionsFound.toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + super.toString();
		return printedResult;
	}

	/**
	 * Sets the root of this graph.
	 * @param root Node with whom the processing of the examined framework started
	 */
	protected void initRoot(TransitionStateNode root) {
		if(root == null) {
			throw new NullPointerException("root");
		}
		if(this.root != null) {
			throw new IllegalArgumentException("Root of Graph was already set and can only be set once.");
		}
		this.root = root;
		this.add(root);
	}
	
	/**
	 *  Adds a graph as a subgraph
	 * 
	 * @param superExit Node of the this graph, under which the new graph will be anchored
	 * @param subGraph Graph, which will be added to the super-graph
	 * @param subEntry Node of the subgraph, which will be connected to the super-graph
	 * @param label Label of the newly created edge, from the superExit node to the subRoot node
     * @throws NoSuchObjectException Thrown if superExit is not a node of this graph
	 */
	public boolean addSubGraph(TransitionStateNode superExit,
			SerialisationGraph subGraph, TransitionStateNode subEntry, String label ) throws NoSuchObjectException {
    	super.addSubGraph(superExit, subGraph, subEntry, label);
		return this.extensionsFound.addAll(subGraph.getExtensions());
	}

}
