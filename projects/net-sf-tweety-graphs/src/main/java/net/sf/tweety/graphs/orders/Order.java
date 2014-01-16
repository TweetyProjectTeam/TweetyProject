package net.sf.tweety.graphs.orders;

import java.util.*;

import net.sf.tweety.graphs.*;

/**
 * This class represents an order among some objects.
 * 
 * @author Matthias Thimm
 * 
 * @param <T> The class that is being ordered.
 */
public class Order<T> {

	/** The directed defaultGraph that represents the order */
	private DefaultGraph<OrderNode> defaultGraph;
	/** A bijection between objects and nodes in the defaultGraph. */
	private Map<T,OrderNode> nodes;
	
	/**
	 * Represents an object that is ordered.
	 * @author Matthias Thimm
	 */
	private class OrderNode implements Node{ }
	
	/**
	 * Creates a new order for the given set of objects.
	 * @param objects some set of objects.
	 */
	public Order(Collection<T> objects){
		this.defaultGraph = new DefaultGraph<OrderNode>();
		this.nodes = new HashMap<T,OrderNode>();
		for(T object: objects){
			OrderNode node = new OrderNode();
			this.nodes.put(object, node);
			this.defaultGraph.add(node);
		}			
	}
	
	/**
	 * Adds that object1 is ordered before object2
	 * @param object1 some object
	 * @param object2 some object
	 */
	public void setOrderedBefore(T object1, T object2){
		if(!this.nodes.containsKey(object1) || !this.nodes.containsKey(object2))
			throw new IllegalArgumentException("Objects cannot be ordered by this order as they are not contained in the domain.");
		this.defaultGraph.add(new DirectedEdge<OrderNode>(this.nodes.get(object1),this.nodes.get(object2)));		
	}
	
	/**
	 * Checks whether object1 is ordered before object2.
	 * @param object1 some object.
	 * @param object2 some object.
	 * @return "true" if object1 is ordered before object2.
	 */
	public boolean isOrderedBefore(T object1, T object2){
		OrderNode node1 = this.nodes.get(object1);
		OrderNode node2 = this.nodes.get(object2);
		return this.defaultGraph.existsDirectedPath(node1, node2);
	}
	
}
