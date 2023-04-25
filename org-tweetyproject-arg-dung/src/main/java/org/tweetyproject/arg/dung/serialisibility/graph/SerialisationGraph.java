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
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.SimpleGraph;


/**
 * This class represents a graph visualising the different {@link TransitionState} during the generation process of serialisable extensions.
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

	/**
	 * Node of the transition state with whom the processing of the examined framework started
	 */
	private TransitionStateNode root;

	/**
	 * Set of serialisable extension, which are shown in the graph
	 */
	private HashSet<Extension<DungTheory>> extensionsFound = new HashSet<>();

	/**
	 * Semantics used to generate the graph
	 */
	private Semantics semanticUsed;

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param graph Graph, showing a serialisation process.
	 * @param root Node of the transition state with whom the processing of the examined framework started
	 * @param semanticsUsed Semantics used for the serialisation process.
	 */
	public SerialisationGraph(Graph<TransitionStateNode> graph, TransitionStateNode root, Semantics semanticsUsed) {
		super(graph);
		this.init(root, semanticsUsed);
	}

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param root Node of the transition state with whom the processing of the examined framework started
	 * @param semanticsUsed Semantics used for the serialisation process.
	 */
	public SerialisationGraph(TransitionStateNode root, Semantics semanticsUsed) {
		super();
		this.init(root, semanticsUsed);
	}

	/**
	 * Adds an extension to the set of serialisable extensions, that are shown in this graph
	 * @param extension extension to add
	 * @return TRUE iff the extension could be added
	 */
	public boolean addExtension(Extension<DungTheory> extension) {
		return this.extensionsFound.add(extension);
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
		HashSet<Extension<DungTheory>> output = new HashSet<>();
		output.addAll(this.extensionsFound);
		return output;
	}

	/**
	 * @return Node of the transition state with whom the processing of the examined framework started
	 */
	public TransitionStateNode getRoot() {
		return this.root;
	}

	/**
	 * @return Semantics used for the serialisation process
	 */
	public Semantics getSemantics() {
		return this.semanticUsed;
	}
	
	/**
	 * Returns the path from the root to the specified node as a sequence of the extensions of the transition states on that path.
	 * @param node Destination of the path
	 * @return
	 */
	public SerialisationSequence getSerialisationSequence(TransitionStateNode node) {
		if(!this.getNodes().contains(node)) throw new IllegalArgumentException("node is not part of this graph");
		
		SerialisationSequence output = new SerialisationSequence();
		
		// we perform a DFS.
		getSequenceRecusive(node, this.root, output);
		return output;
	}
	
	/**
	 * Recursive Method to compute the sequence of the path to one node in a DFS approach
	 * @param in_SearchedNode final destination of the path
	 * @param in_PointedNode node pointed at in this iteration of the recursive calls
	 * @param inout_IsOnPath True iff. the searched node has been found on the path
	 * @param out_Sequence Sequence which will be returned
	 */
	private boolean getSequenceRecusive(
			TransitionStateNode in_SearchedNode, 
			TransitionStateNode in_PointedNode,  
			SerialisationSequence out_Sequence) {
		boolean out_IsOnPath = false;
		
		//[TERMINATION CONDITION]
		if(in_PointedNode.equals(in_SearchedNode)) 
		{ 
			out_IsOnPath = true;
		}
		else {
			for (TransitionStateNode childNode : this.getChildren(in_PointedNode)) {
				// [RECUSIVE CALL]
				boolean chldIsOnPath = this.getSequenceRecusive(in_SearchedNode, childNode,  out_Sequence);
				if(chldIsOnPath) {
					out_IsOnPath = true;
					continue;
				}
			}
		}
		
		if(out_IsOnPath) out_Sequence.add(in_PointedNode.getState().getExtension());
		return out_IsOnPath;
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

	private void init(TransitionStateNode root, Semantics semanticsUsed) {
		this.initRoot(root);
		this.semanticUsed = semanticsUsed;
	}

}
