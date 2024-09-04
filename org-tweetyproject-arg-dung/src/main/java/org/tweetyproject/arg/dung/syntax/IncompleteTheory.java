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

import org.tweetyproject.arg.dung.semantics.Extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

 /**
  * implementation of incomplete argumentation frameworks
  *
  * @author Sebastian Franke
  * @author Lars Bengel
  */
 public class IncompleteTheory extends DungTheory{
	 /**
	  * definite arguments
	  */
	 public Collection<Argument> definiteArguments;
	 /**
	  * definite attacks
	  */
	 public Collection<Attack> definiteAttacks;
	 /**
	  * uncertain arguments
	  */
	 public Collection<Argument> uncertainArgument;
	 /**
	  * uncertain attacks
	  */
	 public Collection<Attack> uncertainAttacks;
	 /**
	  * constructor
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
	  * adds uncertain argument
	  * @param arg1 argument 1
	  * @param arg2 argument 2
	  */
	 public void addPossibleAttack(Argument arg1, Argument arg2) {
		 this.uncertainAttacks.add(new Attack(arg1, arg2));

	 }

	 /**
	  * adds definite attack
	  * @param arg1 argument 1
	  * @param arg2 argument 2
	  */
	 public void addDefiniteAttack(Argument arg1, Argument arg2) {
		 this.definiteAttacks.add(new Attack(arg1, arg2));
	 }
	 /**
	  * instantiates some possible arguments and attacks
	  * @param usedPossibleArguments arguments from this.possibleArguments
	  * @param usedPossibleAttacks attacks from this.possibleAttacks
	  */
	 public void instantiate(Collection<Argument> usedPossibleArguments, Collection<Attack> usedPossibleAttacks) {
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

		 this.addAll(this.definiteArguments);
		 this.addAll(usedPossibleArguments);
		 // only include definite attacks if both arguments actually exist
		 for(Attack i : definiteAttacks) {
			 if (this.contains(i.getAttacker()) && this.contains(i.getAttacked()))
				 this.add(i);
		 }
		 for(Attack i : usedPossibleAttacks) {
			 this.add(i);
		 }
		 return;
	 }
	 /**
	  * merges DungTheories to one incomplete theory
	  * @param theories the theories to merge
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
			 if(isInAllTheories)
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
			 if(isInAllTheories)
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
	 public <T> Collection<Collection<T>> powerSet(Collection<T> originalSet) {
		 Collection<Collection<T>> sets = new HashSet<>();
		 if (originalSet.isEmpty()) {
			 sets.add(new HashSet<T>());
			 return sets;
		 }
		 ArrayList<T> list = new ArrayList<T>(originalSet);
		 T head = list.get(0);
		 Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		 for (Collection<T> set : powerSet(rest)) {
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
	  * @param s a set of arguments
	  */
	 public void optimisticCompletion(Collection<Argument> s) {
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
	  * @param s a set of arguments
	  */
	 public void pessimisticCompletion(Collection<Argument> s) {
		 HashSet<Attack> usedAttacks = new HashSet<Attack>();
		 for(Attack att : this.uncertainAttacks) {
			 if(!(this.definiteArguments.contains(att.getAttacker()) || s.contains(att.getAttacker())) &&
					 !(this.definiteArguments.contains(att.getAttacked()) || s.contains(att.getAttacked()))) {
				 usedAttacks.add(att);
			 }

		 }
		 HashSet<Argument> newArgs = new HashSet<>(this.uncertainArgument);
		 newArgs.removeAll(s);
		 this.instantiate(newArgs, usedAttacks);
	 }


	 /**
 * Generates all possible completions of the current Dung theory based on uncertain arguments and attacks.
 *
 * @return A collection of {@code DungTheory} objects representing all possible completions of the current theory.
 */
	 public Collection<DungTheory> getAllCompletions() {
		 Collection<DungTheory> theories = new HashSet<>();
		 Collection<Collection<Argument>> powerSet = this.powerSet(this.uncertainArgument);
		 for(Collection<Argument> instance : powerSet) {
			 Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
			 //uncertain attacks that can occur in this instance
			 HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
			 for(Attack att : this.uncertainAttacks) {
				 //only add attack to possible attacks if both parties appear in instance
				 if((this.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
						 (this.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
					 uncertainAttacksInInstance.add(att);
				 }
			 }
			 Collection<Collection<Attack>> powerSetAttacks = this.powerSet(uncertainAttacksInInstance);
			 //create new instance for each member of the power set and evaluate it
			 for(Collection<Attack> j : powerSetAttacks) {
				 this.instantiate(instance, j);
				 theories.add(new DungTheory(this));
			 }
		 }
		 return theories;
	 }
 }
