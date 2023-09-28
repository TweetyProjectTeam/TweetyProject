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

import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

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
	 * Builds the necessary elements of a causal model, from a specified set of equivalences, used as structural equations
	 */
	private static void buildModel(Set<Equivalence> structuralEquations, HashSet<Proposition> out_ExplainableAtoms,
			HashSet<Proposition> out_BackGroundAtoms) {
		for(var eq : structuralEquations) {
			var pair = eq.getFormulas();
			if(pair.getFirst() instanceof Proposition) {
				out_ExplainableAtoms.addAll(pair.getFirst().getAtoms());
			}
			else {
				throw new IllegalArgumentException("only literal are acceptable at the left hand side of the equation");
			}
		}

		for(var eq : structuralEquations) {
			var pair = eq.getFormulas();
			for(var atom : pair.getSecond().getAtoms()) {
				if(!out_ExplainableAtoms.contains(atom)) {
					out_BackGroundAtoms.add(atom);
				}
			}
		}
	}

	/**
	 * Checks if the specified parameter comply to the necessary form of a causal model
	 * @param backGroundAtoms set of background atoms
	 * @param explainableAtoms set of explainable atoms
	 * @param structuralEquations boolean structural equations; one equation for each explainable atom,
	 * which is the only literal on exactly one side of the equation
	 */
	private static boolean checkCorrectForm(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		for(var atom : explainableAtoms) {
			boolean hasEQ = false;
			for(var eq : structuralEquations) {
				var pair = eq.getFormulas();
				if((pair.getFirst() instanceof Proposition) && pair.getFirst().getAtoms().contains(atom)) {
					if(hasEQ) {
						throw new IllegalArgumentException("has more than one boolean structural equation for an explainable atom");
					}
					if(pair.getSecond().getAtoms().contains(atom)) {
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
			CausalModel.checkIfOnlyExplainableBackgroundAtoms(backGroundAtoms, explainableAtoms, pair.getFirst());
			CausalModel.checkIfOnlyExplainableBackgroundAtoms(backGroundAtoms, explainableAtoms, pair.getSecond());
		}

		return true;
	}
	/**
	 * Checks that the specified structural equations only use background or explainable atoms
	 */
	private static void checkIfOnlyExplainableBackgroundAtoms(Set<Proposition> backGroundAtoms,
			Set<Proposition> explainableAtoms, PlFormula formula) {
		for( var atom : formula.getAtoms()) {
			if(!backGroundAtoms.contains(atom) && !explainableAtoms.contains(atom)) {
				throw new IllegalArgumentException("boolean structural equation uses "
						+ "atoms different from the background or explainable atoms");
			}
		}
	}

	/**
	 * background atoms <br/> - <br/>
	 * Background atoms represent variables that are determined outside of the model. They are typically unobservable and uncontrollable.
	 */
	private HashSet<Proposition> backGroundAtoms;

	/**
	 * explainable atoms <br/> - <br/>
	 * Every explainable atom v is functionally dependent on other atoms of the model.
	 */
	private HashSet<Proposition> explainableAtoms;

	/**
	 * boolean structural equations <br/> - <br/>
	 * Represent the causal mechanism by which the explainable atoms are determined by the other atoms in the model.
	 */
	private HashSet<Equivalence> structuralEquations;

	/**
	 * Creates an empty causal model
	 */
	public CausalModel() {
		this.backGroundAtoms = new HashSet<>();
		this.explainableAtoms = new HashSet<>();
		this.structuralEquations = new HashSet<>();
	}

	/**
	 * Creates a causal model
	 * @param structuralEquations Set of boolean structural equations; One equation for each explainable atom,
	 * which only occurs on the left side of the equation
	 */
	public CausalModel(Set<Equivalence> structuralEquations) {
		var explainableAtoms = new HashSet<Proposition>();
		var backGroundAtoms = new HashSet<Proposition>();

		CausalModel.buildModel(structuralEquations, explainableAtoms, backGroundAtoms);
		this.commonConstructor(backGroundAtoms, explainableAtoms, structuralEquations);
	}

	/**
	 * Creates a causal model
	 * @param backGroundAtoms Set of background atoms
	 * @param explainableAtoms Set of explainable atoms
	 * @param structuralEquations Set of boolean structural equations; One equation for each explainable atom,
	 * which only occurs on the left side of the equation
	 */
	public CausalModel(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		this.commonConstructor(backGroundAtoms, explainableAtoms, structuralEquations);
	}

	/**
	 * Adds a specified atom to the background atoms of this instance.
	 * @param atom Atom to add to the background atoms of this instance.
	 * @return TRUE iff the atom was successfully added as a background atom to this instance.
	 * FALSE if the atom was already contained and leaves the set unchanged.
	 * @throws IllegalArgumentException if adding the specified atom would violate the definition of a causal model
	 */
	public boolean addBackgroundAtom(Proposition atom) {
		if(this.backGroundAtoms.contains(atom)) {
			return false;
		}
		var newBackgroundAtoms = new HashSet<>(this.backGroundAtoms);
		newBackgroundAtoms.add(atom);

		CausalModel.checkCorrectForm(newBackgroundAtoms, this.explainableAtoms, this.structuralEquations);
		return this.backGroundAtoms.add(atom);
	}

	/**
	 * Adds a specified atom to the explainable atoms of this instance and the specified equivalence as a structural equation for this atom.
	 * @param atom Atom to add to the explainable atoms of this instance.
	 * @param structuralEquation Equation, having only the specified atom on the left side and
	 * a formula consisting only of atoms different than the specified atom on the right hand side of this equivalence
	 * @return TRUE iff the atom and the formula were successfully added to this instance.
	 * FALSE if the atom or the equation were already contained and leaves at least one of the sets unchanged.
	 * @throws IllegalArgumentException if adding the specified atom and or equation would violate the definition of a causal model
	 */
	public boolean addExplainableAtom(Proposition atom, Equivalence structuralEquation) {
		if(this.explainableAtoms.contains(atom)) {
			return false;
		}
		var newExplainableAtoms = new HashSet<>(this.explainableAtoms);
		newExplainableAtoms.add(atom);
		var newEquations = new HashSet<>(this.structuralEquations);
		newEquations.add(structuralEquation);

		CausalModel.checkCorrectForm(this.backGroundAtoms, newExplainableAtoms, newEquations);
		this.explainableAtoms = newExplainableAtoms;
		this.structuralEquations = newEquations;
		return true;
	}

	/**
	 * Adds a specified equivalence to the structural equations of this instance. To this instance unknown explainable or background atoms
	 * are created and added in this process.
	 * @param equation Equation, having only one explainable atom on the left side and
	 * a formula on the right hand side of this equivalence consisting only of atoms different than the atom on the left
	 * @return TRUE iff the formula and all therein contained atoms were successfully added to this instance.
	 * FALSE if the equation was already contained and leaves the set unchanged.
	 * @throws IllegalArgumentException if adding the specified equation would violate the definition of a causal model
	 */
	public boolean addStructuralEquation(Equivalence equation) {
		if(this.structuralEquations.contains(equation)) {
			return false;
		}
		var newEquations = new HashSet<>(this.structuralEquations);
		newEquations.add(equation);
		var newExplainable = new HashSet<>(this.explainableAtoms);
		var newBackGround = new HashSet<>(this.backGroundAtoms);
		if(equation.getFormulas().getFirst() instanceof Proposition) {
			newExplainable.addAll(equation.getFormulas().getFirst().getAtoms());
		}
		else {
			throw new IllegalArgumentException("only literal are acceptable at the left hand side of the equation");
		}

		for(var atom : equation.getFormulas().getSecond().getAtoms()) {
			if(!newExplainable.contains(atom)) {
				newBackGround.add(atom);
			}
		}

		CausalModel.checkCorrectForm(newBackGround, newExplainable, newEquations);
		this.structuralEquations = newEquations;
		this.explainableAtoms = newExplainable;
		this.backGroundAtoms = newBackGround;
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CausalModel) {
			return false;
		}

		var other = (CausalModel) o;

		if( other.backGroundAtoms.equals(this.backGroundAtoms)
				&& other.explainableAtoms.equals(this.explainableAtoms)
				&& other.structuralEquations.equals(this.structuralEquations)) {
			return true;
		}
		else {
			return super.equals(o);
		}
	}

	public HashSet<Proposition> getBackGroundAtoms() {
		return new HashSet<Proposition>(this.backGroundAtoms);
	}

	public HashSet<Proposition> getExplainableAtoms() {
		return new HashSet<Proposition>(this.explainableAtoms);
	}

	public HashSet<Equivalence> getStructuralEquations(){
		return new HashSet<Equivalence>(this.structuralEquations);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.backGroundAtoms == null) ? 0 : this.backGroundAtoms.hashCode())
				+ ((this.explainableAtoms == null) ? 0 : this.explainableAtoms.hashCode())
				+ ((this.structuralEquations == null) ? 0 : this.structuralEquations.hashCode());
		return result;
	}

	private void commonConstructor(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		CausalModel.checkCorrectForm(backGroundAtoms, explainableAtoms, structuralEquations);
		this.backGroundAtoms = new HashSet<>(backGroundAtoms);
		this.explainableAtoms = new HashSet<>(explainableAtoms);
		this.structuralEquations = new HashSet<>(structuralEquations);
	}
}
