/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.*;
import net.sf.tweety.math.matrix.Matrix;
import net.sf.tweety.math.term.IntegerConstant;


/**
 * This class implements an abstract argumentation theory in the sense of Dung.
 * <br>
 * <br>See
 * <br>
 * <br>Phan Minh Dung. On the Acceptability of Arguments and its Fundamental Role in Nonmonotonic Reasoning, Logic Programming and n-Person Games.
 * In Artificial Intelligence, Volume 77(2):321-358. 1995
 *
 *
 * @author Matthias Thimm, Tjitze Rienstra
 *
 */
public class DungTheory extends BeliefSet<Argument> implements Graph<Argument>, Comparable<DungTheory> {

	/**
	 * For archiving sub graphs 
	 */
	private static Map<DungTheory, Collection<Graph<Argument>>> archivedSubgraphs = new HashMap<DungTheory, Collection<Graph<Argument>>>();
		
	/**
	 * The set of attacks
	 */
	private Set<Attack> attacks = new HashSet<Attack>();;

	
	/**
	 * explicit listing of direct attackers and attackees (for efficiency reasons) 
	 */
	private Map<Argument,Set<Argument>> parents = new HashMap<Argument,Set<Argument>>();
	private Map<Argument,Set<Argument>> children= new HashMap<Argument,Set<Argument>>();
	
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public DungTheory(){
		super();
	}
	
	/**
	 * Creates a new theory from the given graph.
	 * @param graph some graph
	 */
	public DungTheory(Graph<Argument> graph){
		super(graph.getNodes());
		for(Edge<? extends Argument> e: graph.getEdges()) {
			this.attacks.add(new Attack(e.getNodeA(),e.getNodeB()));
			if(!parents.containsKey(e.getNodeB()))
				parents.put(e.getNodeB(), new HashSet<Argument>());
			parents.get(e.getNodeB()).add(e.getNodeA());
			if(!children.containsKey(e.getNodeA()))
				children.put(e.getNodeA(), new HashSet<Argument>());
			children.get(e.getNodeA()).add(e.getNodeB());
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	public Signature getSignature(){
		return new DungSignature(this);
	}

	/**
	 * returns true if <source>arguments</source> attack all other arguments in the theory
	 * @param ext An extension contains a set of arguments.
	 * @return true if <source>arguments</source> attack all other arguments in the theory
	 */
	public boolean isAttackingAllOtherArguments(Extension ext){
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
		Set<Argument> attackers = getAttackers(arguments.get(i));
		Iterator<Argument> it = attackers.iterator();
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
	 * Determines if the theory is coherent, i.e., if each preferred extension is stable
	 * @return true if the theory is coherent
	 */
	public boolean isCoherent(){
		Set<Extension> preferredExtensions = new PreferredReasoner(this).getExtensions();;
		Set<Extension> stableExtensions = new StableReasoner(this).getExtensions();
		stableExtensions.retainAll(preferredExtensions);
		return preferredExtensions.size() == stableExtensions.size();
	}

	/**
	 * Determines if the theory is relatively coherent, i.e., if the grounded extension coincides with the intersection of all preferred extensions
	 * @return true if the theory is relatively coherent
	 */
	public boolean isRelativelyCoherent(){
		Extension groundedExtension = new GroundReasoner(this).getExtensions().iterator().next();
		Set<Extension> preferredExtensions = new PreferredReasoner(this).getExtensions();
		Extension cut = new Extension(preferredExtensions.iterator().next());
		for(Extension e: preferredExtensions)
			cut.retainAll(e);
		return groundedExtension.equals(cut);
	}

	/**
	 * Computes the set {A | (A,argument) in attacks}.
	 * @param argument an argument
	 * @return the set of all arguments that attack <source>argument</source>.
	 */
	public Set<Argument> getAttackers(Argument argument){
		if(!this.parents.containsKey(argument))
			return new HashSet<Argument>();
		return new HashSet<Argument>(this.parents.get(argument));		
	}
	
	/**
	 * Computes the set {A | (argument,A) in attacks}.
	 * @param argument an argument
	 * @return the set of all arguments that are attacked by <source>argument</source>.
	 */
	public Set<Argument> getAttacked(Argument argument){
		if(!this.children.containsKey(argument))
			return new HashSet<Argument>();
		return new HashSet<Argument>(this.children.get(argument));	
	}

	/**
	 * returns true if some argument of <source>ext</source> attacks argument.
	 * @param argument an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <source>ext</source> attacks argument.
	 */
	public boolean isAttacked(Argument argument, Extension ext){
		if(!this.parents.containsKey(argument))
			return false;
		for(Argument attacker: this.parents.get(argument))
			if(ext.contains(attacker))
				return true;
		return false;
	}
	
	/**
	 * returns true if some argument of <source>ext</source> is attacked by argument.
	 * @param argument an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <source>ext</source> is attacked by argument.
	 */
	public boolean isAttackedBy(Argument argument, Collection<Argument> ext){
		if(!this.children.containsKey(argument))
			return false;
		for(Argument attacked: this.children.get(argument))
			if(ext.contains(attacked))
				return true;
		return false;
	}
	
	/**
	 * returns true if some argument of <source>ext2</source> attacks some argument
	 * in <source>ext1</source>
	 * @param ext1 an extension, ie. a set of arguments
	 * @param ext2 an extension, ie. a set of arguments
	 * @return true if some argument of <source>ext2</source> attacks some argument
	 * in <source>ext1</source>
	 */
	public boolean isAttacked(Extension ext1, Extension ext2){
		for(Argument a: ext1)
			if(this.isAttacked(a, ext2)) return true;
		return false;
	}

	/**
	 * Checks whether the given extension is stable wrt. this theory.
	 * @param e some extension
	 * @return "true" iff the extension is stable.
	 */
	public boolean isStable(Extension e) {
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
	public Extension faf(Extension extension){
		Extension newExtension = new Extension();
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
		if(!this.parents.containsKey(arg1))
			return false;
		return this.parents.get(arg1).contains(arg2);
	}
	
	/**
	 * Checks whether "arg1" indirectly attacks "arg2", i.e. whether there
	 * is an odd-length path from "arg1" to "arg2".
	 * @param arg1 an AbstractArgument.
	 * @param arg2 an AbstractArgument.
	 * @return "true" iff "arg1" indirectly attacks "arg2".
	 */
	public boolean isIndirectAttack(Argument arg1, Argument arg2){
		return this.isIndirectAttack(arg1, arg2, new HashSet<Argument>());
	}
	
	/**
	 * Checks whether "arg1" indirectly attacks "arg2", i.e. whether there
	 * is an odd-length path from "arg1" to "arg2".
	 * @param arg1 an AbstractArgument.
	 * @param arg2 an AbstractArgument.
	 * @param visited already visited arguments.
	 * @return "true" iff "arg1" indirectly attacks "arg2".
	 */
	private boolean isIndirectAttack(Argument arg1, Argument arg2, Set<Argument> visited){
		if(this.isAttackedBy(arg2,arg1)) return true;
		visited.add(arg1);
		Set<Argument> attackedArguments = this.getAttacked(arg1);
		attackedArguments.removeAll(visited);
		for(Argument attacked : attackedArguments){
			if(this.isSupport(attacked, arg2))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks whether "arg1" supports "arg2", i.e. whether there
	 * is an even-length path from "arg1" to "arg2".
	 * @param arg1 an AbstractArgument.
	 * @param arg2 an AbstractArgument.
	 * @return "true" iff "arg1" supports "arg2".
	 */
	public boolean isSupport(Argument arg1, Argument arg2){
		return this.isSupport(arg1, arg2, new HashSet<Argument>());
	}
	
	/**
	 * Checks whether "arg1" supports "arg2", i.e. whether there
	 * is an even-length path from "arg1" to "arg2".
	 * @param arg1 an AbstractArgument.
	 * @param arg2 an AbstractArgument.
	 * @param visited already visited arguments.
	 * @return "true" iff "arg1" supports "arg2".
	 */
	private boolean isSupport(Argument arg1, Argument arg2, Set<Argument> visited){
		if(arg1.equals(arg2)) return true;
		visited.add(arg1);
		Set<Argument> attackedArguments = this.getAttacked(arg1);
		attackedArguments.removeAll(visited);
		for(Argument attacked : attackedArguments){
			if(this.isIndirectAttack(attacked, arg2))
				return true;
		}
		return false;
	}
	
	// Misc methods

	
	/** Pretty print of the theory.
	 */
	public String prettyPrint(){
		String output = new String();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext())
			output += "argument("+it.next().toString()+").\n";
		output += "\n";
		Iterator<Attack> it2 = attacks.iterator();
		while(it2.hasNext())
			output += "attack"+it2.next().toString()+".\n";
		return output;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){		
		return "<" + super.toString() + "," + this.attacks + ">";
	}
	
	/**
	 * Adds the given attack to this dung theory.
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(Attack attack){
		boolean result = false;
		result |= this.attacks.add(attack);
		if(!parents.containsKey(attack.getAttacked()))
			parents.put(attack.getAttacked(), new HashSet<Argument>());
		result |= parents.get(attack.getAttacked()).add(attack.getAttacker());
		if(!children.containsKey(attack.getAttacker()))
			children.put(attack.getAttacker(), new HashSet<Argument>());
		result |= children.get(attack.getAttacker()).add(attack.getAttacked());		
		return result; 
	}
	
	/**
	 * Removes the given attack from this Dung theory.
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean remove(Attack attack){
		boolean result = false;
		result |= this.attacks.remove(attack);
		if(parents.containsKey(attack.getAttacked()))		
			result |= parents.get(attack.getAttacked()).remove(attack.getAttacker());
		if(children.containsKey(attack.getAttacker()))
			result |= children.get(attack.getAttacker()).remove(attack.getAttacked());
		return result; 
	}
	
	/**
	 * Removes the argument and all its attacks
	 * @param a some argument
	 * @return true if this structure has been changed
	 */
	public boolean remove(Argument a){
		Collection<Attack> toBeRemoved = new HashSet<Attack>();
		for(Attack att: this.attacks)
			if(att.contains(a))
				toBeRemoved.add(att);		
		this.attacks.removeAll(toBeRemoved);
		this.parents.remove(a);
		this.children.remove(a);
		return super.remove(a);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSet#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c){
		boolean result = true;
		for(Object a: c)
			if(a instanceof Argument)
				result |= this.remove((Argument)a);
			else if(a instanceof Attack)
				result |= this.remove((Attack)a);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o){
		if(o instanceof Argument)
			return super.contains(o);
		return this.attacks.contains(o);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefSet#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c){
		if(c instanceof DungTheory){
			DungTheory other = (DungTheory) c;
			return super.containsAll(other) && this.attacks.containsAll(other.getAttacks());
		}
		return super.containsAll(c);
	}
	
	/**
	 * Adds the set of attacks to this Dung theory.
	 * @param c a collection of attacks
	 * @return "true" if this Dung theory has been modified.
	 */
	public boolean addAllAttacks(Collection<? extends Attack> c){
		boolean result = false;
		for(Attack att: c)
			result |= this.add(att);
		return result;
	}

	/**
	 * Adds all arguments and attacks of the given theory to
	 * this theory
	 * @param theory some Dung theory
	 * @return "true" if this Dung Theory has been modified 
	 */
	public boolean add(DungTheory theory){
		boolean b1 = this.addAll(theory);
		boolean b2 = this.addAllAttacks(theory.getAttacks());
		return b1 || b2 ;		
	}
	
	/**
	 * Returns all attacks of this theory.
	 * @return all attacks of this theory.
	 */
	public Set<Attack> getAttacks(){
		return new HashSet<Attack>(this.attacks);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getRestriction(java.util.Collection)
	 */
	@Override
	public Graph<Argument> getRestriction(Collection<Argument> arguments) {
		DungTheory theory = new DungTheory();
		theory.addAll(arguments);
		for (Attack attack: this.attacks)
			if(arguments.contains(attack.getAttacked()) && arguments.contains(attack.getAttacker()))
				theory.add(attack);
		return theory;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((attacks == null) ? 0 : attacks.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DungTheory other = (DungTheory) obj;
		if (attacks == null) {
			if (other.attacks != null)
				return false;
		} else if (!attacks.equals(other.attacks))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#add(net.sf.tweety.graphs.Edge)
	 */
	@Override
	public boolean add(Edge<Argument> edge) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getNodes()
	 */
	@Override
	public Collection<Argument> getNodes() {		
		return this;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getNumberOfNodes()
	 */
	@Override
	public int getNumberOfNodes() {
		return this.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#areAdjacent(net.sf.tweety.graphs.Node, net.sf.tweety.graphs.Node)
	 */
	@Override
	public boolean areAdjacent(Argument a, Argument b) {
		return this.isAttackedBy(b, a);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getEdges()
	 */
	@Override
	public Collection<? extends Edge<? extends Argument>> getEdges() {
		return new HashSet<Attack>(this.attacks);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getChildren(net.sf.tweety.graphs.Node)
	 */
	@Override
	public Collection<Argument> getChildren(Node node) {
		if(!(node instanceof Argument))
			throw new IllegalArgumentException("Node of type argument expected");
		return this.getAttacked((Argument)node);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getParents(net.sf.tweety.graphs.Node)
	 */
	@Override
	public Collection<Argument> getParents(Node node) {
		if(!(node instanceof Argument))
			throw new IllegalArgumentException("Node of type argument expected");
		return this.getAttackers((Argument)node);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#existsDirectedPath(net.sf.tweety.graphs.Node, net.sf.tweety.graphs.Node)
	 */
	@Override
	public boolean existsDirectedPath(Argument node1, Argument node2) {
		return DefaultGraph.existsDirectedPath(this, node1, node2);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getNeighbors(net.sf.tweety.graphs.Node)
	 */
	@Override
	public Collection<Argument> getNeighbors(Argument node) {
		Set<Argument> neighbours = new HashSet<Argument>();
		neighbours.addAll(this.getAttacked(node));
		neighbours.addAll(this.getAttackers(node));
		return neighbours;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getAdjancyMatrix()
	 */
	@Override
	public Matrix getAdjancyMatrix() {
		Matrix m = new Matrix(this.getNumberOfNodes(), this.getNumberOfNodes());
		int i = 0, j;
		for(Argument a: this){
			j = 0;
			for(Argument b : this){
				m.setEntry(i, j, new IntegerConstant(this.areAdjacent(a, b) ? 1 : 0));				
				j++;
			}
			i++;
		}
		return m;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getComplementGraph(int)
	 */
	@Override
	public DungTheory getComplementGraph(int selfloops) {
		DungTheory comp = new DungTheory();
		for(Argument node: this)
			comp.add(node);
		for(Argument node1: this)
			for(Argument node2: this)
				if(node1 == node2){
					if(selfloops == Graph.INVERT_SELFLOOPS){
						if(!this.isAttackedBy(node2, node1))
							comp.add(new Attack(node1, node2));
					}else if(selfloops == Graph.IGNORE_SELFLOOPS){
						if(this.isAttackedBy(node2, node1))
							comp.add(new Attack(node1, node2));						
					}
				}else if(!this.isAttackedBy(node2, node1))
					comp.add(new Attack(node1, node2));
		return comp;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#hasSelfLoops()
	 */
	@Override
	public boolean hasSelfLoops() {
		for(Argument a: this)
			if(this.isAttackedBy(a, a))
				return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getEdge(net.sf.tweety.graphs.Node, net.sf.tweety.graphs.Node)
	 */
	@Override
	public Edge<Argument> getEdge(Argument a, Argument b) {
		if(this.isAttackedBy(b, a))
			return new Attack(a, b);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#isWeightedGraph()
	 */
	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getStronglyConnectedComponents()
	 */
	@Override
	public Collection<Collection<Argument>> getStronglyConnectedComponents() {
		return DefaultGraph.getStronglyConnectedComponents(this);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.graphs.Graph#getSubgraphs()
	 */
	@Override
	public Collection<Graph<Argument>> getSubgraphs() {	
		if(!DungTheory.archivedSubgraphs.containsKey(this))			
			DungTheory.archivedSubgraphs.put(this, DefaultGraph.<Argument>getSubgraphs(this));		
		return DungTheory.archivedSubgraphs.get(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DungTheory o) {
		// DungTheory implements Comparable in order to 
		// have a fixed (but arbitrary) order among all theories
		// for that purpose we just use the hash code.
		return this.hashCode() - o.hashCode();
	}
}
