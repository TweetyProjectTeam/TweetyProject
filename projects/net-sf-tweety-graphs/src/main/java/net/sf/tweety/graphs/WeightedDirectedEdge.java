package net.sf.tweety.graphs;

public class WeightedDirectedEdge<S extends Node,T extends Number> extends DirectedEdge<S> implements WeightedEdge<S,T>{

	/** The weight of this edge. */
	private T weight;
	
	/**
	 * Creates a new weighted direct edge.
	 * @param nodeA some node
	 * @param nodeB some node
	 * @param weight some weight
	 */
	public WeightedDirectedEdge(S nodeA, S nodeB, T weight) {
		super(nodeA, nodeB);
		this.weight = weight;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.WeightedEdge#getWeight()
	 */
	@Override
	public T getWeight() {
		return this.weight;
	}
}
