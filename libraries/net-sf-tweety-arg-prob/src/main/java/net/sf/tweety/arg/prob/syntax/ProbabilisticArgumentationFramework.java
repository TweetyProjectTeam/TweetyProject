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
package net.sf.tweety.arg.prob.syntax;

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.math.probability.Probability;

/**
 * This class implements the probabilistic argumentation framework approach
 * of [Li, Oren, Norman. Probabilistic Argumentation Frameworks. TAFA'2011].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticArgumentationFramework extends DungTheory{

	/** Probability assignments to arguments (with independence assumption). */
	private Map<Argument,Probability> argumentProbabilityAssignment;
	/** Probability assignments to attacks (with independence assumption). */
	private Map<Attack,Probability> attackProbabilityAssignment;
	
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public ProbabilisticArgumentationFramework(){
		super();		
		this.argumentProbabilityAssignment = new HashMap<Argument,Probability>();
		this.attackProbabilityAssignment = new HashMap<Attack,Probability>();
	}	
	
	/**
	 * Creates a new PAF from the given graph, all arguments
	 * and attacks have probability 1.
	 * @param graph some graph
	 */
	public ProbabilisticArgumentationFramework(Graph<Argument> graph){
		super(graph);
		this.argumentProbabilityAssignment = new HashMap<Argument,Probability>();
		this.attackProbabilityAssignment = new HashMap<Attack,Probability>();
		for(Argument a: this)
			this.argumentProbabilityAssignment.put(a, Probability.ONE);
		for(Attack att: this.getAttacks())
			this.attackProbabilityAssignment.put(att, Probability.ONE);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.DungTheory#add(net.sf.tweety.arg.dung.syntax.Attack)
	 */
	@Override
	public boolean add(Attack att){
		boolean b = super.add(att);
		this.attackProbabilityAssignment.put(att, Probability.ONE);
		return b;
	}
	
	/**
	 * Adds the given attack with the given probability
	 * @param att some attack
	 * @param p some probability
	 * @return "true" iff this object was actually changed
	 */
	public boolean add(Attack att, Probability p){
		boolean b = this.add(att);
		this.attackProbabilityAssignment.put(att, p);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSet#add(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean add(Argument a){
		boolean b = super.add(a);
		this.argumentProbabilityAssignment.put(a, Probability.ONE);
		return b;
	}
	
	/**
	 * Adds the given argument with the given probability
	 * @param a some argument
	 * @param p some probability
	 * @return "true" iff this object was actually changed
	 */
	public boolean add(Argument a, Probability p){
		boolean b = this.add(a);
		this.argumentProbabilityAssignment.put(a, p);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.DungTheory#remove(net.sf.tweety.arg.dung.syntax.Attack)
	 */
	@Override
	public boolean remove(Attack attack){
		boolean b = super.remove(attack);
		this.attackProbabilityAssignment.remove(attack);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.DungTheory#remove(net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean remove(Argument a){
		this.argumentProbabilityAssignment.remove(a);
		for(Attack att: this.getAttacks())
			if(att.contains(a))
				this.attackProbabilityAssignment.remove(att);
		return super.remove(a);
	}
	
	/**
	 * Returns the probability of the given argument.
	 * If this framework does not contain the given argument,
	 * an IllegalArgumentException is thrown.
	 * @param a some argument
	 * @return the probability of the argument.
	 */
	public Probability getProbability(Argument a){
		if(this.argumentProbabilityAssignment.containsKey(a))
			return this.argumentProbabilityAssignment.get(a);
		throw new IllegalArgumentException("Unknown argument " + a);
	}
	
	/**
	 * Returns the probability of the given attack.
	 * If this framework does not contain the given attack,
	 * an IllegalArgumentException is thrown.
	 * @param a some attack
	 * @return the probability of the attack.
	 */
	public Probability getProbability(Attack a){
		if(this.attackProbabilityAssignment.containsKey(a))
			return this.attackProbabilityAssignment.get(a);
		throw new IllegalArgumentException("Unknown attack " + a);
	}
	
	/**
	 * Computes the probability of the given AAF wrt. 
	 * this framework (if the given AAF is not a subgraph
	 * of this framework, it has probability zero). 
	 * @param aaf some AAF (supposedly a subgraph of this framework)
	 * @return the probability of the given AAF.
	 */
	@SuppressWarnings("unlikely-arg-type")
	public Probability getProbability(DungTheory aaf){
		if(!this.containsAll(aaf))
			return Probability.ZERO;
		double d = 1d;
		for(Argument a: this)
			if(aaf.contains(a))
				d *= this.argumentProbabilityAssignment.get(a).doubleValue();
			else d *= 1-this.argumentProbabilityAssignment.get(a).doubleValue();
		for(Attack att: this.getAttacks())
			if(aaf.contains(att))
				d *= this.attackProbabilityAssignment.get(att).doubleValue();
			else
				if(aaf.contains(att.getAttacker()) && aaf.contains(att.getAttacked()))
					d *= 1-this.attackProbabilityAssignment.get(att).doubleValue();		
		return new Probability(d);
	}
	
	/**
	 * Returns the complete subgraph probability function on the subgraphs
	 * of this PAF, assuming independence between probabilities of all
	 * arguments and attacks.
	 * @return the subgraph probability function of this framework.
	 */
	public SubgraphProbabilityFunction getSubgraphProbabilityFunction(){
		SubgraphProbabilityFunction p = new SubgraphProbabilityFunction(this);
		for(DungTheory aaf: p.keySet())
			p.put(aaf, this.getProbability(aaf));
		return p;
	}
	
	/**
	 * Samples a random DungTheory from this framework wrt.
	 * the probabilities of its elements and assuming independence.
	 * @return a sampled DungTheory.
	 */
	public DungTheory sample(){
		DungTheory aaf = new DungTheory();
		for(Argument a: this)
			if(this.argumentProbabilityAssignment.get(a).sample())
				aaf.add(a);
		for(Attack att: this.getAttacks())
			if(aaf.contains(att.getAttacker()) && aaf.contains(att.getAttacked()))
				if(this.attackProbabilityAssignment.get(att).sample())
					aaf.add(att);			
		return aaf;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.DungTheory#isWeightedGraph()
	 */
	@Override
	public boolean isWeightedGraph() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.DungTheory#toString()
	 */
	@Override
	public String toString(){
		String s = "<{";
		boolean isFirst = true;
		for(Argument a: this)
			if(isFirst){
				s += a + ":" + this.argumentProbabilityAssignment.get(a);
				isFirst = false;
			}else s += "," + a + ":" + this.argumentProbabilityAssignment.get(a);
		s += "},{";
		isFirst = true;
		for(Attack att: this.getAttacks())
			if(isFirst){
				s += att + ":" + this.attackProbabilityAssignment.get(att);
				isFirst = false;
			}else s += "," + att + ":" + this.attackProbabilityAssignment.get(att);
		return s  + "}>";
	}
}
