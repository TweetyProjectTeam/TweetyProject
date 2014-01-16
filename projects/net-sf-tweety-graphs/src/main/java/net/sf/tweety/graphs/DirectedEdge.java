package net.sf.tweety.graphs;

/**
 * Instances of this class represent directed edges.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the nodes this edge connects
 */
public class DirectedEdge<T extends Node> extends Edge<T> {

	/** Creates a new directed edge for the given nodes.
	 * @param nodeA some node.
	 * @param nodeB some node.
	 */
	public DirectedEdge(T nodeA, T nodeB) {
		super(nodeA, nodeB);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "(" + this.getNodeA() + "," + this.getNodeB() + ")"; 
	}
}
