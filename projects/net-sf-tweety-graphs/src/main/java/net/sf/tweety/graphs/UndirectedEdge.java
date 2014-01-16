package net.sf.tweety.graphs;

/**
 * Instances of this class represent undirected edges.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the nodes this edge connects
 */
public class UndirectedEdge<T extends Node> extends Edge<T> {

	/** Creates a new undirected edge for the given nodes.
	 * @param nodeA some node.
	 * @param nodeB some node.
	 */
	public UndirectedEdge(T nodeA, T nodeB) {
		super(nodeA, nodeB);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = ((this.getNodeA() == null) ? 0 : this.getNodeA().hashCode());
		result += ((this.getNodeB() == null) ? 0 : this.getNodeB().hashCode());
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
		if(this.getNodeA() == null && this.getNodeB() == null)
			return other.getNodeA() == null && other.getNodeB() == null;
		if(other.getNodeA() == null && other.getNodeB() == null)
			return false;
		if(this.getNodeA() == null && other.getNodeA() == null )
			return this.getNodeB().equals(other.getNodeB());
		if(this.getNodeA() == null && other.getNodeB() == null )
			return this.getNodeB().equals(other.getNodeA());
		if(this.getNodeA() == null)
			return false;
		if(this.getNodeB() == null && other.getNodeA() == null )
			return this.getNodeA().equals(other.getNodeB());
		if(this.getNodeB() == null && other.getNodeB() == null )
			return this.getNodeA().equals(other.getNodeA());
		if(this.getNodeB() == null)
			return false;
		if(other.getNodeA() == null || other.getNodeB() == null)
			return false;
		if(this.getNodeA().equals(other.getNodeA()) && this.getNodeB().equals(other.getNodeB()))
			return true;
		if(this.getNodeA().equals(other.getNodeB()) && this.getNodeB().equals(other.getNodeA()))
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "{" + this.getNodeA() + "," + this.getNodeB() + "}"; 
	}
}
