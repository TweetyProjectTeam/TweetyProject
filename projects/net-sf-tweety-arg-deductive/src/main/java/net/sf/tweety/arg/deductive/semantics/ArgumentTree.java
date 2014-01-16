package net.sf.tweety.arg.deductive.semantics;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.graphs.*;

/**
 * Instances of this class represent argument trees in the sense
 * of Definition 6.1 in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 * 
 * @author Matthias Thimm
 */
public class ArgumentTree extends DefaultGraph<DeductiveArgumentNode> {
	
	/** The root node of this tree. */
	private DeductiveArgumentNode rootNode;	
	
	/**
	 * Creates an empty argument tree for the given root node.
	 * @param root the root node.
	 */
	public ArgumentTree(DeductiveArgumentNode root){
		super();
		this.rootNode = root;
	}
	
	/**
	 * Returns the root node of this tree.
	 * @return the root node of this tree.
	 */
	public DeductiveArgumentNode getRoot(){
		return this.rootNode;
	}
	/**
	 * Returns a string representation of this argument tree.
	 * @return a string representation of this argument tree.
	 */
	public String prettyPrint(){
		return this.prettyPrint(this.rootNode, new HashSet<DeductiveArgumentNode>(), 0);
	}
	
	/** 
	 * Returns a string representation of the subtree rooted at
	 * the given node.
	 * @param node some node.
	 * @param visitedNodes already visited nodes.
	 * @param depth depth for indentation.
	 * @return a string.
	 */
	private String prettyPrint(DeductiveArgumentNode node, Set<DeductiveArgumentNode> visitedNodes, int depth){
		String s = "";
		visitedNodes.add(node);
		for(int i = 0; i < depth; i++)
			s += "--";
		s += node.toString() + "\n";
		for(DeductiveArgumentNode node2: this.getNeighbors(node))
			if(!visitedNodes.contains(node2)){
				s += this.prettyPrint(node2, new HashSet<DeductiveArgumentNode>(visitedNodes), depth+1);
			}
		return s;
	}
}
