/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
