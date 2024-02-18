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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.math.algebra.*;

/**
 * This class implements a weighted abstract argumentation theory (WAF) using a C-Semiring.
 * <br>
 * <br>See
 * <br>
 * <br>Bistarelli et al. "A novel weighted defence and its relaxation in abstract argumentation." 
 * International Journal of Approximate Reasoning 92 (2018): 66-86.
 *
 *
 * @author Sandra Hoffmann
 *
 */

public class WeightedArgumentationFramework<T> extends DungTheory {

	private Map<String, T> weightMap;
	private Semiring<T> semiring;


	/**
	 * default constructor returns classic Dung style AF using a Boolean Semiring
	 */
	@SuppressWarnings("unchecked")
	public WeightedArgumentationFramework() {
		super();
		this.weightMap = new HashMap<>();
		this.semiring = (Semiring<T>) new BooleanSemiring();
	}

	/**
	 * Constructor for WAF with specific Semiring
	 * @param semiring The semiring used to model the weights
	 */
	public WeightedArgumentationFramework(Semiring<T> semiring) {
		super();
		this.weightMap = new HashMap<>();
		this.semiring = semiring;
	}

	/**
	 * Constructor for WAF from a graph with a specific Semiring. The weights will be initialized to the zeroElement of the Semiring.
	 * @param semiring The semiring used to model the weights
	 * @param graph The graph representing the argumentation framework.
	 */
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

	/**
	 * Constructs a Weighted Argumentation Framework using the specified semiring,
	 * graph, and weight map.
	 *
	 * @param semiring The semiring used to model the weights
	 * @param graph The graph representing the argumentation framework.
	 * @param weightMap A map associating argument identifiers (as strings) with their corresponding weights.
	 */
	public WeightedArgumentationFramework(Semiring<T> semiring, Graph<Argument> graph, Map<String, T> weightMap) {
		super(graph);
		this.weightMap = weightMap;
		this.semiring = semiring;
	}
	
	/**
	 * Adds the given attack to this weighted Dung theory. The weight will be set to the strongest Attack (zeroElement) of the Semiring.
	 * 
	 * @param attack an attack
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @param weight the weight for this attack
	 * @return {@code true} if the set of attacks has been modified.
	 */
	public boolean add(Attack attack, T weight) {
		return this.addAttack(attack.getAttacker(), attack.getAttacked(), weight);
	}
	
	
	/**
	 * Adds the given attacks to this dung theory. The weights will be set to the strongest Attack (zeroElement) of the Semiring.
	 * @param attacks some attacks
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @return {@code true} if this Weighted Dung Theory has been modified 
	 */
	public boolean add(DungTheory theory){
		T weight = this.semiring.getZeroElement();
		boolean result = super.add(theory);

		 Set<Attack> attacks = theory.getAttacks(); for (Attack f : attacks) { boolean
		 sub = this.add(f, weight); result = result && sub; }
		 
		return result;
	}
	
	
	/**
	 * Adds the given attack to this weighted Dung theory. The weight will be set to the strongest Attack (zeroElement) of the Semiring.
	 * 
	 * @param attack an attack
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @return {@code true} if the set of attacks has been modified.
	 */
	public boolean addAttack(Argument attacker, Argument attacked, T weight) {
		boolean result = super.addAttack(attacker, attacked);
		this.setWeight(attacker, attacked, weight);
		return result;
	}
	
	/**
	 * Adds the set of attacks to this Weighted Dung theory. The weights will be set to the strongest Attack (zeroElement) of the Semiring.
	 * @param c a collection of attacks
	 * @return {@code true} if this Dung theory has been modified.
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
	 * @return {@code true} if this Dung theory has been modified.
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
	 * @return {@code true} if the set of attacks has been modified.
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
	 * @return {@code true} if this structure has been changed
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

	/**
	 * Determines the stronger attack between two given attacks based on their weights.
	 *
	 * @param attackA The first attack for comparison.
	 * @param attackB The second attack for comparison.
	 * @return The stronger attack, considering their weights according to the specified semiring.
	 */
	public Attack strongerAttack(Attack attackA, Attack attackB) {
		T weigthAttackA = this.getWeight(attackA);
		T weigthAttackB = this.getWeight(attackB);
		if (semiring.betterOrSame(weigthAttackA, weigthAttackB)) {
			return attackA;
		} else {
			return attackB;
		}
	}

	/**
	 * Compares the weights of two attacks and returns their combined result based on the semiring addition.
	 *
	 * @param attackA The first attack for comparison.
	 * @param attackB The second attack for comparison.
	 * @return The weight of the stronger attack.
	 */
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
	 * @return {@code true} if every attacker on <code>argument</code> is attacked by some 
	 * accepted argument wrt. the given theory.
	 */
	@Override
	public boolean isAcceptable(Argument argument, Extension<DungTheory> ext){	
		return wDefence(argument, ext);
	}
	
	
	/**
	 * Determines if the given extension can defend itself from all attacks.
	 *
	 * @param e The extension to be checked for weighted defensibility.
	 * @return {@code true} if the extension can defend itself from all attacks.
	 */
	public boolean wDefence(Extension<DungTheory> e) {
		
		//Argument attacker = att.getAttacker();
		Set<Argument> attackers = new HashSet<Argument>();
		for(Argument arg:e) {
			attackers.addAll(this.getAttackers(arg));
		}
		
		return wDefence(e, attackers, new HashSet<Argument>());				
	}
	
	/**
	 * Determines if the given extension can defend a specific argument from all attacks.
	 *
	 * @param a The argument to be defended.
	 * @param e The extension to w-defend a.
	 * @return {@code true} if the extension can defend the argument from all attacks.
	 */
	public boolean wDefence(Argument a, Extension<DungTheory> e) {
	
		Set<Argument> attackers = this.getAttackers(a);
		Set<Argument> attacked = new HashSet<>();
		attacked.add(a);
		return wDefence(e, attackers, attacked);				
	}
	
	/**
	 * Determines if the given extension can defend itself from a set of attackers.
	 *
	 * @param e The extension to w-defend itself.
	 * @param attackers The set of arguments representing attackers.
	 * @return {@code true} if the extension can defend itself from the specified attackers.
	 */
	public boolean wDefence(Extension<DungTheory> e, Set<Argument> attackers) {
		return wDefence(e, attackers, new HashSet<Argument>());				
	}
	
	
	/**
	 * Determines if the given extension can defend itself and specific arguments from a set of attackers.
	 *
	 * @param e The extension to w-defend itself and attacked.
	 * @param attackers The set of arguments representing attackers.
	 * @param attacked The set of arguments that need to be defended by e.
	 * @return {@code true} if the extension can defend itself and the specified arguments from the attackers.
	 */
	public boolean wDefence(Extension<DungTheory> e, Set<Argument> attackers, Set<Argument> attacked) {
		return gDefence(this.getSemiring().getOneElement(), e, attackers, attacked);
		
	}
	
	/**
	 * Determines if the given extension can defend itself from all attacks up to a specified threshold gamma.
	 *
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the attack and defense weights.
	 * @param e The extension to g-defend itself.
	 * @return {@code true} if the extension can defend itself from all attacks within the specified threshold.
	 */
	public boolean gDefence(T gamma, Extension<DungTheory> e) {
		Set<Argument> attackers = new HashSet<Argument>();
		for(Argument arg:e) {
			attackers.addAll(this.getAttackers(arg));
		}
		return gDefence(gamma, e, attackers, new HashSet<Argument>());				
	}
	
	/**
	 * Determines if the given extension can defend itself and argument a from all attacks up to a specified threshold gamma.
	 *
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the attack and defense weights.
	 * @param a The argument to be defended by e.
	 * @param e The extension to g-defend itself.
	 * @return {@code true} if the extension can defend itself from all attacks within the specified threshold; {@code false} otherwise.
	 */
	public boolean gDefence(T gamma, Argument a, Extension<DungTheory> e) {
		Set<Argument> attackers = this.getAttackers(a);
		Set<Argument> attacked = new HashSet<>();
		attacked.add(a);
		return gDefence(gamma, e, attackers, attacked);				
	}
	
	/**
	 * Determines if the given extension can defend itself from a set of attackers up to a specified threshold gamma.
	 *
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the attack and defense weights.
	 * @param e The extension to g-defend itself.
	 * @param attackers The set of arguments representing attackers.
	 * @return {@code true} if the extension can defend itself from the specified attackers within the specified threshold.
	 */
	public boolean gDefence(T gamma, Extension<DungTheory> e, Set<Argument> attackers) {
		return gDefence(gamma,e, attackers, new HashSet<Argument>());				
	}
	
	
	/**
	 * Determines if the given extension can defend itself and specific arguments from a set of attackers up to a specified threshold gamma.
	 *
	 * @param gamma he threshold for defense, representing the maximum allowable difference between the attack and defense weights.
	 * @param e The extension to g-defend itself.
	 * @param attackers The set of arguments representing attackers.
	 * @param attacked The set of arguments that need to be defended by the extension.
	 * @return {@code true} if the extension can defend itself and the specified arguments from the attackers within the specified threshold.
	 */
	public boolean gDefence(T gamma, Extension<DungTheory> e, Set<Argument> attackers, Set<Argument> attacked) {
		//add attacked arguments to extension, if they are not included yet
		Extension<DungTheory> extUnionattacked = new Extension<>();
		extUnionattacked.addAll(e);
		extUnionattacked.addAll(attacked);
		T strengthAttack = this.getSemiring().getOneElement();
		T strengthDefence = this.getSemiring().getOneElement();

		//get strength of attack
		for(Argument attacker:attackers) {
			strengthAttack = this.getSemiring().getOneElement();
			strengthDefence = this.getSemiring().getOneElement();
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
			
			//if argument attacks extension, extension has to attack back
			if (strengthDefence.equals(this.getSemiring().getOneElement()) && !strengthAttack.equals(this.getSemiring().getOneElement()) && !gamma.equals(this.getSemiring().getOneElement())) {
			    //throw new IllegalStateException("Extension does not attack.");
				return false;
			}
			
			//check if extension is able to defend
			if (!semiring.betterOrSame(semiring.divide(strengthAttack, strengthDefence), gamma)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * returns true if some argument of <code>ext</code> attacks argument and argument cannot w-defend itself.
	 * @param argument an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return {@code true} if some argument of <code>ext</code> successfully attacks argument.
	 */
	public boolean isAttacked(Argument argument, @SuppressWarnings("rawtypes") Extension<? extends ArgumentationFramework> ext){
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
	 * @return {@code true} if some argument of <code>ext</code> is attacked by argument.
	 */
	public boolean isAttackedBy(Argument attacker, Collection<Argument> ext){
		if (getAttacked(attacker) == null)
			return false;
	    
	    return !wDefence(new Extension<DungTheory>(ext), Set.of(attacker));

	}
	
	
	/**
	 * returns true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code> and ext1 cannot w-defend itself
	 * @param ext1 an extension, ie. a set of arguments
	 * @param ext2 an extension, ie. a set of arguments
	 * @return {@code true} if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 */
	public boolean isAttacked(Extension<DungTheory> ext1, Extension<DungTheory> ext2){
		    Set<Argument> attackers = new HashSet<>();
		    attackers.addAll(ext2);
			return !wDefence(ext1, attackers);
	}
	
	
	/**
	 * Checks whether arg1 is attacked by arg2 and cannot w-defend itself.
	 * @param arg1 an argument.
	 * @param arg2 an argument.
	 * @return {@code true} if arg1 is attacked by arg2
	 */
	public boolean isAttackedBy(Argument arg1, Argument arg2){
		
		if(!this.getAttacked(arg2).contains(arg1))return false;
		
		Extension<DungTheory> attacked = new Extension<>();
		attacked.add(arg1);
		return !wDefence(attacked, Set.of(arg2));

	}
	
	/**
	 * Determines if the given extension is alpha-conflict-free.
	 *
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param ext The extension to be checked for alpha-conflict-freeness.
	 * @return {@code true} if the extension is alpha-conflict-free.
	 */
	public boolean isAlphaConflictFree(T alpha, Extension<DungTheory> ext) {
		T internalAttackWeight = this.getSemiring().getOneElement();
		//get weight of internal attacks
		for(Argument a:ext) {
			Set<Argument> attacksFromA = new HashSet<Argument>();
			attacksFromA.addAll(this.getAttacked(a));
			attacksFromA.retainAll(ext);
			if(!attacksFromA.isEmpty()) {
				for(Argument att:attacksFromA) {
					internalAttackWeight = semiring.multiply(internalAttackWeight, this.getWeight(new Attack(a,att)));
				}
			}
		}
		
		return semiring.betterOrSame(internalAttackWeight, alpha);
	}
	
	
	/**
	 * Determines if the given extension is both alpha-conflict-free and gamma-defensible.
	 *
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @param ext The extension to be checked for alpha-gamma-admissibility.
	 * @return {@code true} if the extension is alpha-conflict-free and gamma-defensible.
	 */
	public boolean isAlphaGammaAdmissible(T alpha, T gamma, Extension<DungTheory> ext) {
		if (!isAlphaConflictFree(alpha,ext)) return false;
		//get Arguments not in ext
		Set<Argument> outsideExt = new HashSet<>(this);
		outsideExt.removeAll(ext);
		return (this.gDefence(gamma, ext,outsideExt));	
	}
	
	/**
	 * Determines if the given extension is both alpha-gamma-complete.
	 *
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @param ext The extension to be checked for alpha-gamma-completeness.
	 * @return {@code true} if the extension is alpha-gamma-complete.
	 */
	public boolean isAlphaGammaComplete(T alpha, T gamma, Extension<DungTheory> ext) {
		if (!isAlphaGammaAdmissible(alpha,gamma,ext)) return false;
		for(Argument a: this)
			if(!ext.contains(a))
				if(this.gDefence(gamma, a, ext))
					return false;
		return true;	
	}
	
	
	/**
	 * Determines if the given extension is alpha-gamma-preferred.
	 *
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @param ext The extension to be checked for alpha-gamma-preference.
	 * @return {@code true} if the extension is alpha-gamma-preferred.
	 */
	public boolean isAlphaGammaPreferred(T alpha, T gamma, Extension<DungTheory> ext) {
	    if (!isAlphaGammaAdmissible(alpha, gamma, ext)) {
	        return false;
	    }

	    Extension<DungTheory> extUa = new Extension<>(ext);

	    for (Argument a : this) {
	        if (!ext.contains(a)) {
	            extUa.add(a);
	            if (this.isAlphaGammaAdmissible(alpha, gamma, extUa)) {
	                return false;
	            }
	            extUa.remove(a); // Remove the added Argument for the next iteration
	        }
	    }

	    return true;
	}

	/**
	 * Determines if the given extension is alpha-gamma-stable.
	 *
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @param ext The extension to be checked for alpha-gamma-stability.
	 * @return {@code true} if the extension is alpha-gamma-stable.
	 */
	public boolean isAlphaGammaStable(T alpha, T gamma, Extension<DungTheory> ext) {
		if (!isAlphaGammaAdmissible(alpha,gamma,ext)) return false;
		Extension<DungTheory> extUa = new Extension<>(ext);
		
		for (Argument a : this) {
			boolean attackedAtLeastOnce = false;
			if (!ext.contains(a)) {
				//get attacks from ext
				Collection<Argument> attacked = this.getAttacked(ext);
				//get attackers of a
				Collection<Argument> attackers = this.getAttackers(a);
				if(!attacked.contains(a)) return false;
				
				for (Argument attacker : ext) {
					if(attackers.contains(attacker)) {
						if (!this.getWeight(new Attack(attacker,a)).equals(this.getSemiring().getOneElement())) attackedAtLeastOnce = true;
					}
				}
				
				//check if there is at least one attack
				if (!attackedAtLeastOnce) return false;
				//check that ext union a is not alphaGammaAdmissible
				extUa.add(a);
				if (this.isAlphaGammaAdmissible(alpha,gamma,extUa)) return false;
				extUa.remove(a);
			}
		}
		return true;
	}
	

    /**
     * Sets the weight of a given attack in the framework.
     *
     * @param attack The attack for which the weight is to be set.
     * @param weight The weight to be set.
     */
	public void setWeight(Attack attack, T weight) {
		weightMap.put(attack.toString(), semiring.validateAndReturn(weight));
	}

	
    /**
     * Sets the weight of an attack between the specified attacker and attacked arguments in the framework.
     *
     * @param attacker The attacking argument.
     * @param attacked The attacked argument.
     * @param weight   The weight to be set.
     */
	public void setWeight(Argument attacker, Argument attacked, T weight) {
		String attack = new Attack(attacker, attacked).toString();
		weightMap.put(attack, semiring.validateAndReturn(weight));
	}

	
    /**
     * Retrieves the weight of a given attack in the framework.
     *
     * @param attack The attack for which the weight is retrieved.
     * @return The weight of the specified attack.
     */
	public T getWeight(Attack attack) {
		return weightMap.get(attack.toString());
	}
	
    /**
     * Retrieves all weights in the framework.
     *
     * @return A map containing the weights of all attacks.
     */
	public Map<String, T> getWeights() {
		return weightMap;
	}

    /**
     * Retrieves the numeric value of the weight associated with a given attack in the framework.
     *
     * @param attack The attack for which the numeric weight is retrieved.
     * @return The numeric value of the weight of the specified attack.
     */
	public double getNumericWeight(Attack attack) {
		T weight = weightMap.get(attack.toString());
		return semiring.toNumericalValue(weight);
	}

    /**
     * Retrieves the semiring used in the framework.
     *
     * @return The semiring associated with the framework.
     */
	public Semiring<T> getSemiring() {
		return this.semiring;
	}
	
    /**
     * Indicates whether the framework is weighted.
     *
     * @return {@code true} if the framework is weighted; {@code false} otherwise.
     */
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
	

    /**
     * Generates a string representation of the framework.
     *
     * @return The string representation of the framework.
     */
    @Override
    public String toString() {
        return "(" + super.toString() + "," + this.weightMap + ")";
    }

}
