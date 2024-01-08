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

/**
 * @author Sandra Hoffmann
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.math.algebra.*;

public class WeightedArgumentationFramework<T> extends DungTheory{
	
    private Map<String, T> weightMap;
    private Semiring<T> semiring;
    
  //default constructor returns classic Dung style AF
    public WeightedArgumentationFramework() {
    	super();
    	this.weightMap  = new HashMap<>();
        this.semiring = (Semiring<T>) new BooleanSemiring();
    }
    
    //constructor for WAF with specific Semiring
    
    public WeightedArgumentationFramework(
            Semiring<T> semiring
    ) {
    	super();
        this.weightMap = new HashMap<>();
        this.semiring = semiring;
    }
    
    
    //constructor for WAF from graph with specific Semiring

    public WeightedArgumentationFramework(
            Map<String, T> weightMap,
            Semiring<T> semiring
    ) {
    	super();
        this.weightMap = weightMap;
        this.semiring = semiring;
    }
    
    
	/**
	 * Adds the given attack to this weighted Dung theory.
	 * @param attack an attack
	 * @param weight
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(Attack attack, T weight){
		return this.addAttack(attack.getAttacker(), attack.getAttacked(), weight); 
	}
	
	/**
	 * Adds an attack from the first argument to the second to this weighted Dung theory.
	 * @param attacker some argument
	 * @param attacked some argument
	 * @param weight
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(Argument attacker, Argument attacked, T weight){
		boolean result = super.addAttack(attacker, attacked);
		this.setWeight(attacker, attacked, weight);
		return result; 
	}
	
	//returns the strongerAttack
	public Attack strongerAttack (Attack attackA, Attack attackB) {
		T weigthAttackA = this.getWeight(attackA);
		T weigthAttackB = this.getWeight(attackB);
		if (semiring.largerOrSame(weigthAttackA, weigthAttackB) ) {
			return attackA;
		} else {
			return attackB;
		}
	}
	
	//returns the weight of the stronger Attack
	public T compareWeights (Attack attackA, Attack attackB) {
		T weigthAttackA = this.getWeight(attackA);
		T weigthAttackB = this.getWeight(attackB);
		return (semiring.add(weigthAttackA,weigthAttackB));
	}
	
	
    
    //sets the weight of a given attack
    public void setWeight(Attack attack, T weight) {
    	weightMap.put(attack.toString(), semiring.validateAndReturn(weight));
    }
    
    public void setWeight(Argument attacker, Argument attacked, T weight) {
    	String attack = "(" + attacker.getName() +"," + attacked.getName()+ ")";
    	weightMap.put(attack, semiring.validateAndReturn(weight));
    }
    
    public T getWeight(Attack attack) {
    	return weightMap.get(attack.toString());
    }
    
    public double getNumericWeight(Attack attack) {
    	T weight = weightMap.get(attack.toString());
    	return semiring.toNumericalValue(weight);
    }
    
    public Semiring<T> getSemiring() {
    	return this.semiring;
    }
    
	public String toString(){		
		return "(" + super.toString() + "," + this.weightMap + ")";
	}

 

}
