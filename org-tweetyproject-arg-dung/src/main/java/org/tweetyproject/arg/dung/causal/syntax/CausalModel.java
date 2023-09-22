/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.causal.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.pl.syntax.*;

/**
 * This class describes a causal model. 
 * 
 * @see "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation 
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class CausalModel {

	/**
	 * background atoms <br/> - <br/>
	 * Background atoms represent variables that are determined outside of the model. They are typically unobservable and uncontrollable.
	 */
	public HashSet<Proposition> BackGroundAtoms;
	/**
	 * explainable atoms <br/> - <br/>
	 * Every explainable atom v is functionally dependent on other atoms of the model.
	 */
	public HashSet<Proposition> ExplainableAtoms;
	/**
	 * boolean structural equations <br/> - <br/>
	 * Represent the causal mechanism by which the explainable atoms are determined by the other atoms in the model.
	 */
	public HashSet<Equivalence> StructuralEquations;
	
	/**
	 * Creates a causal model
	 * @param backGroundAtoms set of background atoms
	 * @param explainableAtoms set of explainable atoms
	 * @param structuralEquations boolean structural equations; one equation for each explainable atom, 
	 * which is the only literal on exactly one side of the equation 
	 */
	public CausalModel(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {	
		commonConstructor(backGroundAtoms, explainableAtoms, structuralEquations);
	}
	
	public CausalModel(Set<Equivalence> structuralEquations) {
		var explainableAtoms = new HashSet<Proposition>();
		var backGroundAtoms = new HashSet<Proposition>();
		
		for(var eq : structuralEquations) {
			var pair = eq.getFormulas();
			buildV(explainableAtoms, pair.getFirst());
			buildV(explainableAtoms, pair.getSecond());
		}
		
		for(var eq : structuralEquations) {
			var pair = eq.getFormulas();
			buildU(explainableAtoms, backGroundAtoms, pair.getFirst());
			buildU(explainableAtoms, backGroundAtoms, pair.getSecond());
		}
		commonConstructor(backGroundAtoms, explainableAtoms, structuralEquations);
	}
	
	public Set<PlFormula> getBeliefs(){
		return new HashSet<PlFormula>(StructuralEquations);
	}
	
	private void buildV(HashSet<Proposition> explainableAtoms, PlFormula formula) {
		if(formula.isLiteral()) {
			explainableAtoms.addAll(formula.getAtoms());
		}
	}

	private void buildU(HashSet<Proposition> explainableAtoms, HashSet<Proposition> backGroundAtoms, PlFormula formula) {
		if(!formula.isLiteral()) {
			for(var atom : formula.getAtoms()) {
				if(!explainableAtoms.contains(atom)) {
					backGroundAtoms.add(atom);
				}
			}
		}
	}
	
	private void commonConstructor(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		checkCorrectForm(backGroundAtoms, explainableAtoms, structuralEquations);
		BackGroundAtoms = new HashSet<Proposition>(backGroundAtoms);
		ExplainableAtoms = new HashSet<Proposition>(explainableAtoms);
		StructuralEquations = new HashSet<Equivalence>(structuralEquations);
	}

	/**
	 * Checks if the specified parameter comply to the necessary form of a causal model
	 * @param backGroundAtoms set of background atoms
	 * @param explainableAtoms set of explainable atoms
	 * @param structuralEquations boolean structural equations; one equation for each explainable atom, 
	 * which is the only literal on exactly one side of the equation 
	 */
	private void checkCorrectForm(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		for(var atom : explainableAtoms) {
			boolean hasEQ = false;
			for(var eq : structuralEquations) {
				var pair = eq.getFormulas();
				if(pair.getFirst().isLiteral() && pair.getFirst().getAtoms().contains(atom)) {
					if(hasEQ) {
						throw new IllegalArgumentException("has more than one boolean structural equation for an explainable atom");
					}
					if(pair.getSecond().getAtoms().contains(atom)) {
						throw new IllegalArgumentException("boolean structural equation has same explainable atom on both sides");
					}
					hasEQ = true;
				}else if(pair.getSecond().isLiteral() && pair.getSecond().getAtoms().contains(atom)) {
					if(hasEQ) {
						throw new IllegalArgumentException("has more than one boolean structural equation for an explainable atom");
					}
					if(pair.getFirst().getAtoms().contains(atom)) {
						throw new IllegalArgumentException("boolean structural equation has same explainable atom on both sides");
					}
					hasEQ = true;
				}					
			}
			
			if(!hasEQ) {
				throw new IllegalArgumentException("has no boolean structural equation for an explainable atom");
			}
		}
		
		for(var eq : structuralEquations) {
			var pair = eq.getFormulas();
			for( var atom : pair.getFirst().getAtoms()) {
				if(!backGroundAtoms.contains(atom) && !explainableAtoms.contains(atom)) {
					throw new IllegalArgumentException("boolean structural equation uses "
							+ "atoms different from the background or explainable atoms");
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CausalModel) {
			return false;
		}
		
		var other = (CausalModel) o;
		
		if( other.BackGroundAtoms.equals(BackGroundAtoms)
				&& other.ExplainableAtoms.equals(ExplainableAtoms)
				&& other.StructuralEquations.equals(StructuralEquations)) {
			return true;
		}
		else {
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((BackGroundAtoms == null) ? 0 : BackGroundAtoms.hashCode()) 
				+ ((ExplainableAtoms == null) ? 0 : ExplainableAtoms.hashCode()) 
				+ ((StructuralEquations == null) ? 0 : StructuralEquations.hashCode());
		return result;
	}
}
