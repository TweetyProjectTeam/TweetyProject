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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.weighted.syntax;

import java.util.ArrayList;
import java.util.Collection;
/**
 * @author Sandra Hoffmann
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.math.algebra.*;

public class WeightedArgumentationFramework<T> extends DungTheory {

	private Map<String, T> weightMap;
	private Semiring<T> semiring;

	// default constructor returns classic Dung style AF
	public WeightedArgumentationFramework() {
		super();
		this.weightMap = new HashMap<>();
		this.semiring = (Semiring<T>) new BooleanSemiring();
	}

	// constructor for WAF with specific Semiring

	public WeightedArgumentationFramework(Semiring<T> semiring) {
		super();
		this.weightMap = new HashMap<>();
		this.semiring = semiring;
	}

	// constructor for WAF from graph with specific Semiring. The weights will be set to the zeroElement of the Semiring.
	public WeightedArgumentationFramework(Semiring<T> semiring, Graph<Argument> graph) {
		super(graph);
		this.weightMap = new HashMap<>();
		this.semiring = semiring;
		//set weights
		T weight = this.semiring.getZeroElement();
		for(Attack att:this.getAttacks()) {
			this.setWeight(att, weight);
		}
	}

	// constructor for WAF from graph with specific Semiring and given weightMap
	public WeightedArgumentationFramework(Semiring<T> semiring, Graph<Argument> graph, Map<String, T> weightMap) {
		super(graph);
		this.weightMap = weightMap;
		this.semiring = semiring;
	}
	
	/**
	 * Adds the given attack to this weighted Dung theory. The weight will be set to the strongest Attack (zeroElement) of the Semiring.
	 * 
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	@Override
	public boolean add(Attack attack) {
		T weight = this.semiring.getZeroElement();
		return this.addAttack(attack.getAttacker(), attack.getAttacked(), weight);
	}
	

	/**
	 * Adds the given attack to this weighted Dung theory.
	 * 
	 * @param attack an attack
	 * @param weight
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(Attack attack, T weight) {
		return this.addAttack(attack.getAttacker(), attack.getAttacked(), weight);
	}
	
	
	/**
	 * Adds the given attacks to this dung theory. The weights will be set to the strongest Attack (zeroElement) of the Semiring.
	 * @param attacks some attacks
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(Attack... attacks){
		Map<String,T> weightList = new HashMap<>();
        for (Attack att:attacks) {
        	weightList.put(att.toString(),semiring.getZeroElement());
        }
		return this.add(weightList, attacks);
	}


	/**
	 * Adds the given attacks to this dung theory.
	 * 
	 * @param weights a map containing the attacks as key and the weights as values.
	 * @param attacks some attacks
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(Map<String, T> weights, Attack... attacks) {
		  boolean result = true; 
		  for (Attack f : attacks) {
			  T weight = weights.get(f.toString());
			  boolean sub = this.add(f, weight);
			  result = result && sub; 
		  }
		return result;
	}
	
	/**
	 * Adds all arguments and attacks of the given theory to
	 * this theory. The weights will be set to the strongest Attack (zeroElement) of the Semiring.
	 * @param theory some Dung theory
	 * @return "true" if this Weighted Dung Theory has been modified 
	 */
	public boolean add(DungTheory theory){
		T weight = this.semiring.getZeroElement();
		boolean result = super.add(theory);
		/*
		 * Set<Attack> attacks = theory.getAttacks(); for (Attack f : attacks) { boolean
		 * sub = this.add(f, weight); result = result && sub; }
		 */
		return result;
	}
	
	
	/**
	 * Adds the given attack to this weighted Dung theory. The weight will be set to the strongest Attack (zeroElement) of the Semiring.
	 * 
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(Attack attack) {
		T weight = this.semiring.getZeroElement();
		return this.addAttack(attack.getAttacker(), attack.getAttacked(), weight);
	}

	/**
	 * Adds an attack from the first argument to the second to this weighted Dung
	 * theory. The weight will be set to the strongest Attack (zeroElement) of the Semiring.
	 * 
	 * @param attacker some argument
	 * @param attacked some argument
	 * @param weight
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(Argument attacker, Argument attacked) {
		T weight = this.semiring.getZeroElement();
		boolean result = super.addAttack(attacker, attacked);
		this.setWeight(attacker, attacked, weight);
		return result;
	}
	
	
	/**
	 * Adds an attack from the first argument to the second to this weighted Dung
	 * theory.
	 * 
	 * @param attacker some argument
	 * @param attacked some argument
	 * @param weight
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(Argument attacker, Argument attacked, T weight) {
		boolean result = super.addAttack(attacker, attacked);
		this.setWeight(attacker, attacked, weight);
		return result;
	}
	
	/**
	 * Adds the set of attacks to this Weighted Dung theory. The weights will be set to the strongest Attack (zeroElement) of the Semiring.
	 * @param c a collection of attacks
	 * @return "true" if this Dung theory has been modified.
	 */
	public boolean addAllAttacks(Collection<? extends Attack> c){
		Map<String,T> weightList = new HashMap<>();
        for (Attack att:c) {
        	weightList.put(att.toString(),semiring.getZeroElement());
        }
        return addAllAttacks(c,weightList);
	}
	
	/**
	 * Adds the set of attacks to this Weighted Dung theory. 
	 * @param c a collection of attacks
	 * @return "true" if this Dung theory has been modified.
	 */
	public boolean addAllAttacks(Collection<? extends Attack> c, Map<String,T> weights){
		boolean result = false;
		for(Attack att: c) {
			T weight = weights.get(att.toString());
			result |= this.add(att, weight);
		}
		return result;
	}


	/**
	 * Removes the given attack from this weighted Dung theory.
	 * 
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean remove(Attack attack) {
		  this.weightMap.remove(attack.toString());
		  boolean result = super.remove(attack);
		  return result; 
	}

	/**
	 * Removes the argument and all its attacks and the corresponding weights
	 * 
	 * @param a some argument
	 * @return true if this structure has been changed
	 */
	public boolean remove(Argument a) {
		Collection<Argument> attackers = super.getAttackers(a);
		Collection<Argument> attacked = super.getAttacked(a);
		
		//delete weights for a
		for(Argument att:attackers) {
			Attack attack = new Attack(att, a);
			this.weightMap.remove(attack.toString());
		}
		for(Argument atts:attacked) {
			Attack attack = new Attack(a, atts);
			this.weightMap.remove(attack.toString());
		}
		return super.remove(a);
	}

	// returns the strongerAttack
	public Attack strongerAttack(Attack attackA, Attack attackB) {
		T weigthAttackA = this.getWeight(attackA);
		T weigthAttackB = this.getWeight(attackB);
		if (semiring.betterOrSame(weigthAttackA, weigthAttackB)) {
			return attackA;
		} else {
			return attackB;
		}
	}

	// returns the weight of the stronger Attack
	public T compareWeights(Attack attackA, Attack attackB) {
		T weigthAttackA = this.getWeight(attackA);
		T weigthAttackB = this.getWeight(attackB);
		return (semiring.add(weigthAttackA, weigthAttackB));
	}
	
	/**
	 * returns true if every attacker on <code>argument</code> is successfully attacked by some 
	 * accepted argument wrt. the given theory. This corresponds to the wDefence of <code>argument</code>.
	 * @param argument an argument
	 * @param ext an extension (the knowledge base)
	 * @return true if every attacker on <code>argument</code> is attacked by some 
	 * accepted argument wrt. the given theory.
	 */
	
	@Override
	public boolean isAcceptable(Argument argument, Extension<DungTheory> ext){	
		return wDefence(argument, ext);
	}
	
	
	// define w-defense
	//returns true if the Extension e can defend itself from all attacks
	public boolean wDefence(Extension<DungTheory> e) {
		
		//Argument attacker = att.getAttacker();
		Set<Argument> attackers = new HashSet<Argument>();
		for(Argument arg:e) {
			attackers.addAll(this.getAttackers(arg));
		}
		
		return wDefence(e, attackers, new HashSet<Argument>());				
	}
	
	// define w-defence
	//returns true if the Extension e can defend argument a from all attacks
	public boolean wDefence(Argument a, Extension<DungTheory> e) {
	
		Set<Argument> attackers = this.getAttackers(a);
		Set<Argument> attacked = new HashSet<>();
		attacked.add(a);
		return wDefence(e, attackers, attacked);				
	}
	
	// define w-defence
	//returns true if the Extension e can defend itself from attackers
	public boolean wDefence(Extension<DungTheory> e, Set<Argument> attackers) {
		return wDefence(e, attackers, new HashSet<Argument>());				
	}
	
	
	// define w-defence
	//returns true if the Extension e can defend itself and attacked from attackers
	public boolean wDefence(Extension<DungTheory> e, Set<Argument> attackers, Set<Argument> attacked) {
		return gDefence(this.getSemiring().getOneElement(), e, attackers, attacked);
		
	}
	
	// define g-defense
	//returns true if the Extension e can defend itself from all attacks up to a threshold of gamma.
	public boolean gDefence(T gamma, Extension<DungTheory> e) {
		Set<Argument> attackers = new HashSet<Argument>();
		for(Argument arg:e) {
			attackers.addAll(this.getAttackers(arg));
		}
		return gDefence(gamma, e, attackers, new HashSet<Argument>());				
	}
	
	// define g-defence
	//returns true if the Extension e can defend argument a from all attacks up to a threshold of gamma.
	public boolean gDefence(T gamma, Argument a, Extension<DungTheory> e) {
		Set<Argument> attackers = this.getAttackers(a);
		Set<Argument> attacked = new HashSet<>();
		attacked.add(a);
		return gDefence(gamma, e, attackers, attacked);				
	}
	
	// define g-defence
	//returns true if the Extension e can defend itself from attackers up to a threshold of gamma.
	public boolean gDefence(T gamma, Extension<DungTheory> e, Set<Argument> attackers) {
		return gDefence(gamma,e, attackers, new HashSet<Argument>());				
	}
	
	
	// define gamma-defence
	//returns true if the Extension e can defend itself and attacked from attackers up to a threshold of gamma. I. e the difference between the aggregated
	//weights of attack and defence is better than gamma.
	public boolean gDefence(T gamma, Extension<DungTheory> e, Set<Argument> attackers, Set<Argument> attacked) {
		//add attacked arguments to extension, if they are not included yet
		Extension<DungTheory> extUnionattacked = new Extension<>();
		extUnionattacked.addAll(e);
		extUnionattacked.addAll(attacked);
		T strengthAttack = this.getSemiring().getOneElement();
		T strengthDefence = this.getSemiring().getOneElement();
		
		//get strength of attack
		for(Argument attacker:attackers) {
			Set<Argument> attacksToE = new HashSet<Argument>();
			attacksToE.addAll(this.getAttacked(attacker));
			attacksToE.retainAll(extUnionattacked);
			if(!attacksToE.isEmpty()) {
				for(Argument att:attacksToE) {
					strengthAttack = semiring.multiply(strengthAttack, this.getWeight(new Attack(attacker,att)));
				}
			}
			//get strength of defence
			Set<Argument> attacksFromE = this.getAttackers(attacker);
			attacksFromE.retainAll(e);
			if(!attacksFromE.isEmpty()) {
				for(Argument defender:attacksFromE) {
					strengthDefence = semiring.multiply(strengthDefence, this.getWeight(new Attack(defender,attacker)));
				}
			}
		}
		if (strengthDefence == this.getSemiring().getOneElement() && gamma != this.getSemiring().getOneElement()) {
		    throw new IllegalStateException("Extension does not attack.");
		}
		
		return semiring.betterOrSame(semiring.divide(strengthAttack, strengthDefence), gamma);
	}
	
	/**
	 * returns true if some argument of <code>ext</code> attacks argument and argument cannot w-defend itself.
	 * @param argument an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> successfully attacks argument.
	 */
	public boolean isAttacked(Argument argument, Extension<? extends ArgumentationFramework> ext){
		if (getAttackers(argument) == null)
			return false;
	    // Create extension with single argument
	    Set<Argument> singleArgumentSet = new HashSet<>();
	    singleArgumentSet.add(argument);
	    Extension<DungTheory> singleArgumentExtension = new Extension<>(singleArgumentSet);

	    // Create a set of attackers from the given extension
	    Set<Argument> attackers = new HashSet<>(ext);

	    // Check if the argument cannot defend itself
	    return !wDefence(singleArgumentExtension, attackers);
	}
	
	/**
	 * returns true if some argument of <code>ext</code> is attacked by argument and cannot be w-defended by ext.
	 * @param argument an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> is attacked by argument.
	 */
	public boolean isAttackedBy(Argument argument, Collection<Argument> ext){
		if (getAttacked(argument) == null)
			return false;
		
	    // Create a set for the attacker
	    Set<Argument> attacker = new HashSet<>();
	    attacker.add(argument);
	    
	    return !wDefence(new Extension<DungTheory>(ext), attacker);

	}
	
	
	/**
	 * returns true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code> and ext1 cannot w-defend itself
	 * @param ext1 an extension, ie. a set of arguments
	 * @param ext2 an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 */
	public boolean isAttacked(Extension<DungTheory> ext1, Extension<DungTheory> ext2){
		    Set<Argument> attackers = new HashSet<>();
		    attackers.addAll(ext2);
			return wDefence(ext1, attackers);
	}
	
	
	/**
	 * Checks whether arg1 is attacked by arg2 and cannot w-defend itself.
	 * @param arg1 an argument.
	 * @param arg2 an argument.
	 * @return "true" if arg1 is attacked by arg2
	 */
	public boolean isAttackedBy(Argument arg1, Argument arg2){
		if(!this.getAttacked(arg2).contains(arg1))
			return false;
		
		Attack attack = new Attack(arg2, arg1);
		Attack defence = new Attack(arg1, arg2);
		if(!semiring.betterOrSame(this.getWeight(attack), this.getWeight(defence))) 
			return false;
		return true;
	}
	
	

	// sets the weight of a given attack
	public void setWeight(Attack attack, T weight) {
		weightMap.put(attack.toString(), semiring.validateAndReturn(weight));
	}

	public void setWeight(Argument attacker, Argument attacked, T weight) {
		String attack = new Attack(attacker, attacked).toString();
		weightMap.put(attack, semiring.validateAndReturn(weight));
	}

	public T getWeight(Attack attack) {
		return weightMap.get(attack.toString());
	}
	
	public Map<String, T> getWeights() {
		return weightMap;
	}

	public double getNumericWeight(Attack attack) {
		T weight = weightMap.get(attack.toString());
		return semiring.toNumericalValue(weight);
	}

	public Semiring<T> getSemiring() {
		return this.semiring;
	}
	
	@Override
	public boolean isWeightedGraph() {
		return true;
	}
	
	
	/** Pretty print of the theory.
	 * @return the pretty print of the theory.
	 */
	public String prettyPrint(){
		String output = new String();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext())
			output += "argument("+it.next().toString()+").\n";
		output += "\n";
		Iterator<Attack> it2 = this.getAttacks().iterator();
		while(it2.hasNext()) {
			Attack temp = it2.next();
			output += "attack"+temp.toString()+ ":- " + this.weightMap.get(temp.toString())+".\n";
		}

		return output;
	}
	

	public String toString() {
		return "(" + super.toString() + "," + this.weightMap + ")";
	}

}
