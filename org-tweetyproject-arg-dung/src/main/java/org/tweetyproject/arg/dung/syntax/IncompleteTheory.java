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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Sebastian Franke
 * implementation of incomplete argumentation frameworks
 *
 */
public class IncompleteTheory extends DungTheory{
	
	public HashSet<Argument> definiteArguments;
	public HashSet<Attack> definiteAttacks;
	public HashSet<Argument> uncertainArgument;
	public HashSet<Attack> uncertainAttacks;
	/**
	 * costructor
	 */
	public IncompleteTheory() {
		definiteArguments = new HashSet<Argument>();
		uncertainArgument = new HashSet<Argument>();
		definiteAttacks = new HashSet<Attack>();
		uncertainAttacks = new HashSet<Attack>();
	}
	/**
	 * adds definite argument
	 * @param arg argument
	 */
	public void addDefiniteArgument(Argument arg) {
		this.definiteArguments.add(arg);
	}
	/**
	 * adds possible argument
	 * @param arg argument
	 */
	public void addPossibleArgument(Argument arg) {
		this.uncertainArgument.add(arg);
	}
	/**
	 * adds definite attack
	 * @param att attack
	 */
	public void addPossibleAttack(Argument arg1, Argument arg2) {
		this.uncertainAttacks.add(new Attack(arg1, arg2));
		
	}
	/**
	 * adds possible attack
	 * @param att attack
	 */
	public void addDefiniteAttack(Argument arg1, Argument arg2) {
		this.definiteAttacks.add(new Attack(arg1, arg2));
	}
	/**
	 * instantiates some possible arguments and attacks
	 * @param usedPossibleArguments arguments from this.possibleArguments	
	 * @param usedPossibleAttacks attacks from this.possibleAttacks
	 */
	public void instantiate(Set<Argument> usedPossibleArguments, Set<Attack> usedPossibleAttacks) {
		if((!this.uncertainAttacks.containsAll(usedPossibleAttacks) || 
				!this.uncertainArgument.containsAll(usedPossibleArguments)) && 
				(!this.definiteAttacks.containsAll(usedPossibleAttacks) || 
						!this.definiteArguments.containsAll(usedPossibleArguments)) ) {
			//TODO: error case
			System.out.println("error case");
			return;
		}

		for(Attack att : this.getAttacks())
			this.remove(att);

		this.clear();

		for(Argument i : this.definiteArguments) {
			this.add(i);
		}
		for(Attack i : definiteAttacks) {
			this.add(i);
		}
		for(Argument i : usedPossibleArguments) {
			this.add(i);
		}
		for(Attack i : usedPossibleAttacks) {
			this.add(i);
		}
		return;
	}
	/**
	 * megres DungTheories to one incomplete theory
	 * @param theories
	 */
	public void merge(HashSet<DungTheory> theories) {
		
		for(Argument arg : theories.iterator().next()) {
			boolean isInAllTheories = true;
			for(DungTheory theory  : theories) {
				if(!theory.contains(arg)) {
					isInAllTheories = false;
					break;
				}
			}
			if(isInAllTheories == true)
				this.definiteArguments.add(arg);
			else
				this.uncertainArgument.add(arg);
		}
		
		Set<Attack> attacks = theories.iterator().next().getAttacks();
		for(Attack att : attacks) {
			boolean isInAllTheories = true;
			for(DungTheory theory  : theories) {
				if(!theory.contains(att)) {
					isInAllTheories = false;
					break;
				}
			}
			if(isInAllTheories == true)
				this.definiteAttacks.add(att);
			else
				this.uncertainAttacks.add(att);
		}
	}
	/**
	 * constructs powerset of generic type
	 * @param <T> generic types (i.e. arguments)
	 * @param originalSet the set to build power set		
	 * @return power set
	 */
	public <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    ArrayList<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	} 
	

	/**
	 * constructs optimistic completion (all attacks from and to set s are instantiated and no other possible attacks)
	 * @param s a set of argumemts
	 */
	public void optimisticCompletion(Set<Argument> s) {
		HashSet<Attack> usedAttacks = new HashSet<Attack>();
		for(Attack att : this.uncertainAttacks) {
			if((this.definiteArguments.contains(att.getAttacker()) || s.contains(att.getAttacker())) &&
					(this.definiteArguments.contains(att.getAttacked()) || s.contains(att.getAttacked()))) {
				usedAttacks.add(att);
			}
			
		}

		this.instantiate(s, usedAttacks);
	}
	/**
	 * constructs pessimistic completion (all attacks but the ones from and to set s are instantiated and no other possible attacks)
	 * @param s a set of argumemts
	 */
	public void pessimisticCompletion(Set<Argument> s) {
		HashSet<Attack> usedAttacks = new HashSet<Attack>();
		for(Attack att : this.uncertainAttacks) {
			if(!(this.definiteArguments.contains(att.getAttacker()) || s.contains(att.getAttacker())) &&
					!(this.definiteArguments.contains(att.getAttacked()) || s.contains(att.getAttacked()))) {
				usedAttacks.add(att);
			}
			
		}
		HashSet<Argument> newArgs = (HashSet<Argument>) this.uncertainArgument.clone();
		newArgs.removeAll(s);
		this.instantiate(newArgs, usedAttacks);
	}

}
