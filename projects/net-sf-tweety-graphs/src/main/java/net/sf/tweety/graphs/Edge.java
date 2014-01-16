package net.sf.tweety.graphs;

/**
 * Instances of this class represent abstract edges.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the nodes this edge connects
 */
public abstract class Edge<T extends Node> {

	/** The first node of this edge. */
	private T nodeA;
	
	/** The second node of this edge. */
	private T nodeB;
	
	/** Creates a new edge for the given nodes.
	 * @param nodeA some node.
	 * @param nodeB some node.
	 */
	public Edge(T nodeA, T nodeB){
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}
	
	/**
	 * Returns the first node of this edge.
	 * @return the first node of this edge. 
	 */
	public T getNodeA(){
		return this.nodeA;
	}
	
	/**
	 * Returns the second node of this edge.
	 * @return the second node of this edge.
	 */
	public T getNodeB(){
		return this.nodeB;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeA == null) ? 0 : nodeA.hashCode());
		result = prime * result + ((nodeB == null) ? 0 : nodeB.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge<?> other = (Edge<?>) obj;
		if (nodeA == null) {
			if (other.nodeA != null)
				return false;
		} else if (!nodeA.equals(other.nodeA))
			return false;
		if (nodeB == null) {
			if (other.nodeB != null)
				return false;
		} else if (!nodeB.equals(other.nodeB))
			return false;
		return true;
	}
}
