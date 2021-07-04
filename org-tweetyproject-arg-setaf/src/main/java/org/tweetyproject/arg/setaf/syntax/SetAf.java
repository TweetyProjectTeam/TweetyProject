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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.semantics.SetAfExtension;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.graphs.DirHyperGraph;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.HyperDirEdge;
import org.tweetyproject.graphs.HyperGraph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.math.matrix.Matrix;


/**
 * This class implements an abstract argumentation theory in the sense of Dung on SetAfs.
 * <br>
 * <br>See
 * <br>
 * <br>Phan Minh Dung. On the Acceptability of Arguments and its Fundamental Role in Nonmonotonic Reasoning, Logic Programming and n-Person Games.
 * In Artificial Intelligence, Volume 77(2):321-358. 1995
 *
 *
 * @author  Sebastian Franke
 *
 */
public class SetAf extends BeliefSet<Argument,SetAfSignature> implements DirHyperGraph<Argument>, Comparable<SetAf> {

	/**
	 * For archiving sub DirHyperGraphs 
	 */
	private static Map<SetAf, Collection<DirHyperGraph<Argument>>> archivedSubgraphs = new HashMap<SetAf, Collection<DirHyperGraph<Argument>>>();

	@Override
	public int size(){
		return this.nodes.size();
	}
	@Override
	public Iterator<Argument> iterator(){
		return this.nodes.iterator();
	}
	
	/**
	 * Creates a new theory from the given DirHyperGraph.
	 * @param DirHyperGraph some DirHyperGraph
	 */
	public SetAf(SetAf DirHyperGraph){
		super(DirHyperGraph);		
	}
	

	
	public SetAf clone() {
		SetAf result = new SetAf(this);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefBase#getSignature()
	 */
	public Signature getMinimalSignature(){
		return new SetAfSignature(this);
	}

	/**
	 * returns true if <code>arguments</code> attack all other arguments in the theory
	 * @param ext An extension contains a set of arguments.
	 * @return true if <code>arguments</code> attack all other arguments in the theory
	 */
	public boolean isAttackingAllOtherArguments(SetAfExtension ext){
		for(Argument a: this) {
			if(ext.contains(a))
				continue;
			if(!this.isAttacked(a, ext))
				return false;
		}		
		return true;
	}

	/**
	 * returns true iff the theory is well-founded, i.e., there is no infinite sequence A1,A2,... of arguments with Ai attacking Ai+1
	 * @return true iff the theory is well-founded
	 */
	public boolean isWellFounded(){
		List<Argument> arguments = new ArrayList<Argument>();		
		for(Formula f: this)
			arguments.add((Argument) f);
		boolean[] dfn = new boolean[arguments.size()];
		boolean[] inProgress = new boolean[arguments.size()];
		for(int i = 0; i < arguments.size(); i++){
			dfn[i] = false;
			inProgress[i] = false;			
		}
		for(int i = 0; i < arguments.size(); i++)
			if(!dfn[i])
				if(dfs(i,arguments,dfn,inProgress))
					return false;		
		return true;
	}

	/**
	 * Depth-First-Search to find a cycle in the theory. Auxiliary method to determine if the theory is well-founded
	 * @param i current node
	 * @param arguments list of all nodes (arguments)
	 * @param dfn array which keeps track whether a node has been visited
	 * @param inProgress array which keeps track which nodes are currently being processed
	 * @return true iff the theory contains a cycle
	 */
	private boolean dfs(int i, List<Argument> arguments, boolean[] dfn, boolean[] inProgress){
		dfn[i] = true;
		inProgress[i] = true;
		Set<Set<Argument>> attackers = getAttackers(arguments.get(i));
		Iterator<Set<Argument>> it = attackers.iterator();
		while(it.hasNext()){
			Argument argument = (Argument) it.next();
			if(inProgress[arguments.indexOf(argument)])
				return true;
			else if(!dfn[arguments.indexOf(argument)])
				if(dfs(arguments.indexOf(argument),arguments,dfn,inProgress))
					return true;
		}
		inProgress[i] = false;
		return false;
	}



	/**
	 * Computes the set {A | (A,argument) in attacks}.
	 * @param argument an argument
	 * @return the set of all arguments that attack <code>argument</code>.
	 */
	public Set<Set<Argument>> getAttackers(Argument node){
		HashSet<Set<Argument>> s = new HashSet<Set<Argument>>();
		for(SetAttack e : this.edges) {
			if(e.getNodeB().equals(node))
				s.add(e.getNodeA());
		}
		return s;
	}
	
	/**
	 * Computes the set {A | (argument,A) in attacks}.
	 * @param node an argument
	 * @return the set of all arguments that are attacked by <code>argument</code>.
	 */
	public Set<Argument> getAttacked(Argument node){
		HashSet<Argument> s = new HashSet<Argument>();
		for(SetAttack e : this.edges) {
			if(e.getNodeA().contains(node))
				s.add(e.getNodeB());
		}
		return s;
	}

	/**
	 * returns true if some argument of <code>ext</code> attacks argument.
	 * @param a an argument
	 * @param SetAfExtension an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> attacks argument.
	 */
	public boolean isAttacked(Argument a, SetAfExtension setAfExtension){
		return this.isAttackedBy(a, setAfExtension);
	}
	
	/**
	 * returns true if some argument of <code>ext</code> is attacked by argument.
	 * @param arg2 an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> is attacked by argument.
	 */
	public boolean isAttackedBy(Argument arg2, Collection<Argument> ext){
		for(SetAttack e: this.edges) {
			if(arg2.equals(e.getNodeB())) {
				for(Argument a : ext) {
					if(e.getNodeA().contains(a))
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * returns true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 * @param ext1 an extension, ie. a set of arguments
	 * @param ext2 an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 */
	public boolean isAttacked(SetAfExtension ext1, SetAfExtension ext2){
		for(Argument a: ext1)
			if(this.isAttacked(a, ext2)) return true;
		return false;
	}

	/**
	 * Checks whether the given extension is stable wrt. this theory.
	 * @param e some extension
	 * @return "true" iff the extension is stable.
	 */
	public boolean isStable(SetAfExtension e) {
		for(Argument a: this) {
			if(e.contains(a)) { 
				if(this.isAttacked(a, e))
					return false;
			}else {
				if(!this.isAttacked(a, e))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * The characteristic function of an abstract argumentation framework: F_AF(S) = {A|A is acceptable wrt. S}.
	 * @param extension an extension (a set of arguments).
	 * @return an extension (a set of arguments).
	 */
	public SetAfExtension faf(SetAfExtension extension){
		SetAfExtension newExtension = new SetAfExtension();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext()){
			Argument argument = it.next();
			if(extension.isAcceptable(argument, this))
				newExtension.add(argument);
		}
		return newExtension;
	}
	
	/**
	 * Checks whether arg1 is attacked by arg2.
	 * @param arg1 an argument.
	 * @param arg2 an argument.
	 * @return "true" if arg1 is attacked by arg2
	 */
	public boolean isAttackedBy(Argument arg1, Argument arg2){
		for(SetAttack e: this.edges) {
			if(e.getNodeB().equals(arg1) && e.getNodeA().contains(arg2))
				return true;
		}
		return false;
	}
	

	// Misc methods

	public boolean add(Argument arg){
		this.nodes.add(arg);
		return true; 
	}



	

	
	/**
	 * Adds an attack from the first argument to the second to this SetAf theory.
	 * @param hashSet some arguments
	 * @param attacked some argument
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(HashSet<Argument> hashSet, Argument attacked){
		SetAttack s = new SetAttack(hashSet, attacked);
		this.edges.add(s);
		return true; 
	}
	
	/**
	 * Adds an attack from the first argument to the second to this SetAf theory.
	 * @param attacker
	 * @param attacked some argument
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(Argument hashSet, Argument attacked){
		SetAttack s = new SetAttack(hashSet, attacked);
		this.edges.add(s);
		return true; 
	}
	
	/**
	 * Removes the given attack from this SetAf theory.
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean remove(SetAttack attack){
		this.edges.remove(attack);
		return true;
	}
	
	/**
	 * Removes the argument and all its attacks
	 * @param a some argument
	 * @return true if this structure has been changed
	 */
	public boolean remove(Argument a){

		
		for (Iterator<SetAttack> i = this.edges.iterator(); i.hasNext();) {			
			SetAttack e = i.next();
			e.remove(a);
			if(e.getNodeA().isEmpty() || e.getNodeB() == null) {
		        i.remove();
		    }
		}

		this.nodes.remove(a);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSet#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c){
		boolean result = true;
		for(Object a: c)
			if(a instanceof Argument)
				result |= this.remove((Argument)a);
			else if(a instanceof SetAttack)
				result |= this.remove((SetAttack)a);
		return result;
	}
	


	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefSet#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c){
		for(Object o: c)
			if(!this.contains(o))
				return false;
		return true;
	}
	
	/**
	 * Checks whether this theory contains the given attack.
	 * @param att some attack
	 * @return "true" iff this theory contains the given attack.
	 */
	public boolean containsAttack(SetAttack att) {
		return this.edges.contains(att);
	}		
	
	/**
	 * Adds the set of attacks to this SetAf theory.
	 * @param edges2 a collection of attacks
	 * @return "true" if this SetAf theory has been modified.
	 */
	public boolean addAllAttacks(Set<SetAttack> edges2){
		boolean result = false;
		for(SetAttack att: edges2)
			result |= this.add(att);
		return result;
	}

	/**
	 * Adds all arguments and attacks of the given theory to
	 * this theory
	 * @param theory some SetAf theory
	 * @return "true" if this SetAf Theory has been modified 
	 */
	public boolean add(SetAf theory){
		boolean b1 = this.addAll(theory);
		boolean b2 = this.addAllAttacks(theory.edges);
		return b1 || b2 ;		
	}
	



	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.edges == null) ? 0 : this.edges.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		if(!this.equals(obj))
			return false;
		return true;
	}






	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SetAf o) {
		// SetAfTheory implements Comparable in order to 
		// have a fixed (but arbitrary) order among all theories
		// for that purpose we just use the hash code.
		return this.hashCode() - o.hashCode();
	}

	@Override
	protected SetAfSignature instantiateSignature() {
		return new SetAfSignature();
	}
	


	
	
	/** The set of nodes */
	protected Set<Argument> nodes;

	/** The set of edges */
	protected Set<SetAttack> edges;
	
	public SetAf(){
		this.nodes = new HashSet<Argument>();
		this.edges = new HashSet<SetAttack>();
	}




	public boolean add(SetAttack edge) {
		for(Argument e: edge.getNodeA())
			if(!this.nodes.contains(e))
				throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		
		if (!this.nodes.contains(edge.getNodeB()))
			throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		this.edges.add(edge);
		return true;
	}

	@Override
	public Collection<Argument> getNodes() {
		return this.nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	@Override
	public boolean areAdjacent(Argument a, Argument b) {
		for(HyperDirEdge<Argument> e : this.edges) {
			if((e.getNodeA().contains(a) && e.getNodeB().equals(b)) ||
					(e.getNodeA().contains(b) && e.getNodeB().equals(a))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public SetAttack getEdge(Argument a, Argument b) {
		System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B");
		return null;
	}
	

	public Set<SetAttack> getAttacks() {
		return this.edges;
	}
	
	public SetAttack getDirEdge(Set<Argument> node1, Node b) {
		for(SetAttack e : this.edges) {
			if(e.getNodeA().equals(node1) && e.getNodeB().equals(b))
				return e;
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection getEdges() {
		return this.edges;
	}



	@Override
	public boolean contains(Object obj) {
		if(obj instanceof Argument)
			if(this.nodes.contains((Argument) obj)){
				return true;
			}
			else {
				return false;
			}
		if(obj instanceof HyperDirEdge)
			if(this.edges.contains((SetAttack) obj)){
				return true;
			}
			else {
				return false;
			}
		return false;
	}

	
	public Collection<Argument> getChildren(Set<Argument> node) {
		HashSet<Argument> result = new HashSet<Argument>();
		for(SetAttack e : this.edges) {
			if(e.getNodeA().equals(node)) {
				result.add((Argument) e.getNodeB());
			}
		}
		return result;
		
	}

	@Override
	public Collection<Argument> getParents(Node node) {
		System.err.println("The return type for getParents in SetAfs is Collection<Set<Argument>>");
		return null;
	}
	public Collection<Set<Argument>> getParents(Argument node) {
		HashSet<Set<Argument>> result = new HashSet<Set<Argument>>();
		for(SetAttack e : this.edges) {
			if(e.getNodeB().equals(node)) {
				result.add(e.getNodeA());
			}
		}
		return result;
	}

	
	public static <S extends Node> boolean existsDirectedPath(SetAf hyperGraph, Argument node1, Argument node2) {
		if (!hyperGraph.getNodes().contains(node1) || !hyperGraph.getNodes().contains(node2))
			throw new IllegalArgumentException("The nodes are not in this graph.");
		if (node1.equals(node2))
			return true;
		// we perform a DFS.
		Stack<Argument> stack = new Stack<Argument>();
		Collection<Argument> visited = new HashSet<Argument>();
		stack.add((Argument) node1);
		while (!stack.isEmpty()) {
			Argument node = stack.pop();
			visited.add(node);
			if (node.equals(node2))
				return true;
			stack.addAll(hyperGraph.getChildren(node));
			stack.removeAll(visited);
		}
		return false;
	}
	
	@Override
	public boolean existsDirectedPath(Argument node1, Argument node2) {
		return SetAf.existsDirectedPath(this, node1, node2);
	}
	


	@Override
	public Collection<Argument> getNeighbors(Argument node) {
		HashSet<Argument> result = new HashSet<Argument>();
		for(SetAttack a : this.edges) {
			if(a.getNodeB().equals(node)) {
				result.addAll(a.getNodeA());
			}
			if(a.getNodeA().contains(node)) {
				result.add((Argument) a.getNodeB());
			}
		}
		return result;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		// A matrix representation o a hypergraph is not known to me
		return null;
	}
	
	/**
	 * 
	 * 
	 * @return the powerset of @param originalSet
	 */
	public Set<Set<Argument>> powerSet(Set<Argument> originalSet) {
	    HashSet<Set<Argument>> sets = new HashSet<Set<Argument>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<Argument>());
	        return sets;
	    }
	    ArrayList<Argument> list = new ArrayList<Argument>(originalSet);
	    Argument head = list.get(0);
	    HashSet<Argument> rest = new HashSet<Argument>(list.subList(1, list.size())); 
	    for (Set<Argument> set : powerSet(rest)) {
	        Set<Argument> newSet = new HashSet<Argument>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }  

	    return sets;
	}  

	

	public SetAf getComplementGraph(int selfloops) {
		//very inefficient
		Set<Set<Argument>> myPowerSet = new HashSet<Set<Argument>>();
		myPowerSet = powerSet(this.nodes);
		
		SetAf comp = new SetAf();
		for (Argument node : this.nodes)
			comp.add(node);
		//iterate over powerset and add every edge that is not in the original graph
		//this can make the String represtnattion extremly log
		//and it may not be able to be shown in 1 line with the toString() method
		for (Set<Argument> node1 : myPowerSet)
			for (Argument node2 : this.nodes)
				if (node1.contains(node2)) {
					if (selfloops == HyperGraph.INVERT_SELFLOOPS) {
						if (this.getDirEdge(node1, node2) != null) 						
							comp.add(new SetAttack(node1, node2)); 
					} else if (selfloops == HyperGraph.IGNORE_SELFLOOPS) {
						if (this.getDirEdge(node1, node2) != null)
							comp.add(new SetAttack(node1, node2));
					}
				} else if (this.getDirEdge(node1, node2) == null) {
					comp.add(new SetAttack(node1, node2));
				}


		return comp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		//algorithm yet to be implemented, not important for the next time
		return null;
	}

	

	
	/**
	 * Returns the set of sub graphs of the given graph.
	 * @param g a graph
	 * @param <S> the type of nodes
	 * 
	 * @return the set of sub graphs of the given graph.
	 */
	public Collection<SetAf> getSubgraphs(SetAf g) {
		
		// not very efficient but will do for now
		Collection<SetAf> result = new HashSet<SetAf>();
		Set<Set<Argument>> subNodes = new SetTools<Argument>().subsets(g.getNodes());
		for (Set<Argument> nodes : subNodes) {
			@SuppressWarnings("unchecked")
			Set<Set<SetAttack>> edges = new SetTools<SetAttack>()
					.subsets((Set<SetAttack>) g.getRestriction(nodes).getEdges());
			for (Set<SetAttack> es : edges) {
				SetAf newg = new SetAf();
				newg.nodes.addAll(nodes);
				newg.edges.addAll(es);
				result.add(newg);
			}
		}
		
		return result;
	}

	@Override
	public SetAf getRestriction(Collection<Argument> nodes) {
		SetAf graph = new SetAf();
		graph.nodes.addAll(nodes);
		for (HyperDirEdge<Argument> e : this.edges)
			if (nodes.containsAll(e.getNodeA()) && nodes.contains(e.getNodeB()))
				graph.add(e);
		return graph;
	}

	@Override
	public boolean hasSelfLoops() {
		for (Argument node1 : this.nodes)
			if (this.areAdjacent(node1, node1))
				return true;
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean add(GeneralEdge edge) {
		return this.edges.add((SetAttack)edge);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<" + this.nodes.toString() + "," + this.edges.toString() + ">";
	}

	@Override
	public Collection<Argument> getChildren(Node node) {
		HashSet<Argument> children = new HashSet<Argument>();
		for(SetAttack att : this.edges) {
			if(att.getNodeA().contains(node))
				children.add(att.getNodeB());
		}
		return children;
	}

	@Override
	public Collection<DirHyperGraph<Argument>> getSubGraphs() {
		if(!SetAf.archivedSubgraphs.containsKey(this))			
			SetAf.archivedSubgraphs.put(this, getSubgraphsHelper(this));		
		return SetAf.archivedSubgraphs.get(this);
	}

	public static Collection<DirHyperGraph<Argument>> getSubgraphsHelper(SetAf g) {
		// not very efficient but will do for now
		Collection<DirHyperGraph<Argument>> result = new HashSet<DirHyperGraph<Argument>>();
		Set<Set<Argument>> subNodes = new SetTools<Argument>().subsets(g.getNodes());
		for (Set<Argument> nodes : subNodes) {
			@SuppressWarnings("unchecked")
			Set<Set<SetAttack>> edges = new SetTools<SetAttack>()
					.subsets((Set<SetAttack>) g.getRestriction(nodes).getEdges());
			for (Set<SetAttack> es : edges) {
				SetAf newg = new SetAf();
				newg.nodes.addAll(nodes);
				newg.edges.addAll(es);
				result.add(newg);
			}
		}
		return result;
	}



	
}
