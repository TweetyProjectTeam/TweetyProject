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

package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;

public class IncompleteReasoner{
	
	private static AbstractExtensionReasoner reasoner;
	
	/**
	 * constructor for direct initialization of semantics
	 * @param semantics the Dung semantics
	 */
	public IncompleteReasoner(Semantics semantics) {
		setSemantics(semantics);
		
	}
	/**
	 * 
	 * @param theory incomplete theory
	 * @return all possible models
	 */
	public Collection<Collection<Extension<DungTheory>>> getAllModels(IncompleteTheory theory){
		
		Collection<Collection<Extension<DungTheory>>> models = new HashSet<Collection<Extension<DungTheory>>>();
		Set<Set<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
		for(Set<Argument> instance : powerSet) {
			Collection<Extension<DungTheory>> instanceModels = new HashSet<Extension<DungTheory>>();
			//uncertain attacks that can occur in this instance
			HashSet<Attack> uncertainAttacksInInstance = new HashSet<Attack>();
			for(Attack att : theory.uncertainAttacks) {
				//only add attack to possible attacks if both parties appear in instance
				if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
						(theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
					uncertainAttacksInInstance.add(att);
				}				
			}
			Set<Set<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
			//create new instance for each member of the power set and evaluate it
			for(Set<Attack> j : powerSetAttacks) {
				theory.instantiate(instance, j);
				instanceModels.addAll(this.reasoner.getModels(theory));
			}

			models.add(instanceModels);
		}
		return models;
	}

	/**
	 * 
	 * @param theory incomplete theory
	 * @param arg argument to be checked
	 * @return if the argument is part of all extensions of all instances 
	 */
	public boolean VerificationNecessary(IncompleteTheory theory, Set<Argument> arg) {
		Collection<Collection<Extension<DungTheory>>> models = this.getAllModels(theory);
		for(Collection<Extension<DungTheory>> instanceModels : models) {
			boolean isArgExtensionForInstance = false;
			for(Extension<DungTheory> ext : instanceModels) {
				if((ext.containsAll(arg) && arg.containsAll(ext))){
					isArgExtensionForInstance = true;
					break;
				}
			}
			if(isArgExtensionForInstance == false)
				return false;
		}
		return true;
	}
	/**
	 * 
	 * @param theory incomplete theory
	 * @param arg argument to be checked
	 * @return if the argument is part of any extensions of all instances 
	 */
	public boolean VerificationPossible(IncompleteTheory theory, Set<Argument> arg) {
		Collection<Collection<Extension<DungTheory>>> models = this.getAllModels(theory);
		for(Collection<Extension<DungTheory>> instanceModels : models) {
			for(Extension<DungTheory> ext : instanceModels) {
				if(ext.containsAll(arg) && arg.containsAll(ext)){
					return true;
				}
			}
		}
		return false;	
	}
	
	public boolean credulousAcceptance(IncompleteTheory theory, Argument arg) {
		Collection<Collection<Extension<DungTheory>>> models = this.getAllModels(theory);
		for(Collection<Extension<DungTheory>> i : models) {
			for(Extension<DungTheory> j : i) {
				if(j.contains(arg)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean skepticalAcceptance(IncompleteTheory theory, Argument arg) {
		Collection<Collection<Extension<DungTheory>>> models = this.getAllModels(theory);
		for(Collection<Extension<DungTheory>> i : models) {
			for(Extension<DungTheory> j : i) {
				if(!j.contains(arg)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean existence(IncompleteTheory theory) {
		Collection<Collection<Extension<DungTheory>>> models = this.getAllModels(theory);
		if(models.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}


	/**
	 * manually sets the semantics
	 * @param semantics the Dung semantics
	 */
	public static void setSemantics(Semantics semantics){
		reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		
	}

	/**
	 * 
	 * @return wether the solver is installed
	 */
	public boolean isInstalled() {
		return true;
	}

}
