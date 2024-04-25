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

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class describes a causal model.
 *
 * Reference: "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class CausalModel extends PlBeliefSet {

/**
 * Constructs a causal model from a specified set of equivalences used as structural equations. 
 * This method populates two sets: one for explainable atoms and another for background atoms based on the provided structural equations.
 * Explainable atoms are derived from the left-hand side of the equations if they consist solely of propositions.
 * Atoms not explained by the left-hand side and appearing on the right-hand side of any equation are considered background atoms.
 *
 * @param structuralEquations A set of equivalences representing the structural equations of the causal model. Each equivalence must have a proposition on its left side.
 * @param out_ExplainableAtoms An initially empty set that will be populated with propositions derived from the left-hand side of the structural equations. These atoms are considered explainable within the model.
 * @param out_BackGroundAtoms An initially empty set that will be populated with atoms found in the right-hand side of the equations but not in the explainable atoms set, indicating these atoms are background conditions or factors.
 * @throws IllegalArgumentException if any left-hand side of the equations does not consist of a proposition, indicating an invalid structural equation format.
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
	 * @return status of form 
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
 	* @param backGroundAtoms A set of propositions identified as background atoms. These are not directly explainable by the model but are necessary for defining the conditions under which other atoms are explained.
 	* @param explainableAtoms A set of propositions identified as explainable atoms. These atoms are those that the model can directly explain through its structural equations.
 	* @param formula The formula being checked. This formula should ideally be a structural equation that needs validation to ensure it uses only known atoms.
 	* @throws IllegalArgumentException if the formula uses atoms that are neither explainable nor background atoms, which violates the model's constraints.
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
	protected HashSet<Proposition> backGroundAtoms;

	/**
	 * explainable atoms <br/> - <br/>
	 * Every explainable atom v is functionally dependent on other atoms of the model.
	 */
	protected HashSet<Proposition> explainableAtoms;

	/**
	 * Creates an empty causal model
	 */
	public CausalModel() {
		super();
		this.backGroundAtoms = new HashSet<>();
		this.explainableAtoms = new HashSet<>();
	}

	/**
	 * Creates a causal model
	 * @param structuralEquations Set of boolean structural equations; One equation for each explainable atom,
	 * which only occurs on the left side of the equation
	 */
	public CausalModel(Set<Equivalence> structuralEquations) {
		super(structuralEquations);
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

	@Override
	public boolean add(PlFormula formula) {
		if(formula instanceof Equivalence) {
			return super.add(formula);
		}

		throw new IllegalArgumentException("only Equivalence formulas are eligible");
	}

	@Override
	public boolean add(PlFormula... formulas) {
		for(var formula : formulas) {
			if(!(formula instanceof Equivalence)) {
				throw new IllegalArgumentException("only Equivalence formulas are eligible");
			}
		}

		return super.add(formulas);
	}

	@Override
	public boolean addAll(Collection<? extends PlFormula> formulas) {
		for(var formula : formulas) {
			if(!(formula instanceof Equivalence)) {
				throw new IllegalArgumentException("only Equivalence formulas are eligible");
			}
		}

		return super.addAll(formulas);
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

		CausalModel.checkCorrectForm(newBackgroundAtoms, this.explainableAtoms, this.getStructuralEquations());
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
		var newEquations = this.getStructuralEquations();
		newEquations.add(structuralEquation);

		CausalModel.checkCorrectForm(this.backGroundAtoms, newExplainableAtoms, newEquations);
		this.explainableAtoms.add(atom);
		this.add(structuralEquation);
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
		if(this.formulas.contains(equation)) {
			return false;
		}
		var newEquations = this.getStructuralEquations();
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
		this.add(equation);
		this.explainableAtoms.addAll(this.explainableAtoms);
		this.backGroundAtoms.addAll(this.backGroundAtoms);
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
				&& other.formulas.equals(this.formulas)) {
			return true;
		}
		else {
			return super.equals(o);
		}
	}

/**
 * Retrieves a copy of the set of background atoms currently defined in the causal model.
 * Background atoms are those elements that are used in the model's equations but are not directly explainable.
 * Returning a copy of the set ensures that the internal state of the causal model cannot be altered directly.
 *
 * @return A new {@link HashSet} containing all the background atoms.
 */
public HashSet<Proposition> getBackGroundAtoms() {
    return new HashSet<>(this.backGroundAtoms);
}

/**
 * Retrieves a copy of the set of explainable atoms currently defined in the causal model.
 * Explainable atoms are those elements that the model explains directly through its structural equations.
 * Returning a copy of the set helps maintain encapsulation and prevents external modifications to the model's state.
 *
 * @return A new {@link HashSet} containing all the explainable atoms.
 */
public HashSet<Proposition> getExplainableAtoms() {
    return new HashSet<>(this.explainableAtoms);
}

/**
 * Retrieves a copy of the set of structural equations currently defined in the causal model.
 * Structural equations are representations of equivalences that define how certain propositions (explainable atoms)
 * depend on other atoms (background or explainable). Each equation must be an instance of {@link Equivalence}.
 * This method filters the model's formulas to return only those that are instances of {@link Equivalence}.
 *
 * @return A new {@link HashSet} containing all the structural equations.
 */
public HashSet<Equivalence> getStructuralEquations() {
    var output = new HashSet<Equivalence>();
    for (var formula : this.formulas) {
        if (formula instanceof Equivalence) {
            output.add((Equivalence) formula);
        }
    }
    return output;
}


	/**
	 * @return A twin model to this instance.
	 */
	public CausalModel getTwinModel() {
		var explainableAtoms = new HashSet<>(this.getExplainableAtoms());
		var structuralEquations = this.getStructuralEquations();
		var counterFactualEquations = new HashSet<>(structuralEquations);

		for(var originalExpAtom : this.explainableAtoms) {
			var counterfactualCopy = new Proposition(originalExpAtom.getName() + "*");
			explainableAtoms.add(counterfactualCopy);

			var eqsToAdd = new HashSet<Equivalence>();
			var eqsToRemove = new HashSet<Equivalence>();

			for(var eq : counterFactualEquations) {
				if(!eq.getAtoms().contains(originalExpAtom)) {
					continue;
				}

				// replace all occurrences of the original atom with the counterfactual copy
				var eqReplaced = eq;
				int occ = eq.numberOfOccurrences(originalExpAtom);
				for(int i = 0; i < occ; i++) {
					eqReplaced = (Equivalence) eq.replace(originalExpAtom, counterfactualCopy, i + 1);
				}
				eqsToRemove.add(eq);
				eqsToAdd.add(eqReplaced);
			}

			counterFactualEquations.removeAll(eqsToRemove);
			counterFactualEquations.addAll(eqsToAdd);
		}

		structuralEquations.addAll(counterFactualEquations);
		return new CausalModel(structuralEquations);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.backGroundAtoms == null) ? 0 : this.backGroundAtoms.hashCode())
				+ ((this.explainableAtoms == null) ? 0 : this.explainableAtoms.hashCode())
				+ ((this.formulas == null) ? 0 : this.formulas.hashCode());
		return result;
	}

	/**
	 * This method implements the interventional statement, by replacing all structural equations for the specified explainable atom "v" of this causal model with a
	 * formula "v = x".
	 * @param v Explainable atom of this causal model, which is to be intervened on.
	 * @param x Truth value of the intervention.
	 */
	public void intervene(Proposition v, boolean x) {
		if(!this.explainableAtoms.contains(v)){
			throw new IllegalArgumentException("The specified variable has to be an explainable atom of the causal model");
		}

		var eqToRemove = new HashSet<PlFormula>();
		for(var eq : this.getStructuralEquations()) {
			var leftHandSide = eq.getFormulas().getFirst();
			if(leftHandSide.getAtoms().contains(v)) {
				eqToRemove.add(eq);
			}
		}
		if(eqToRemove.isEmpty()) {
			throw new NoSuchElementException("There is a explainable atom, without any structural equation");
		}

		this.formulas.removeAll(eqToRemove);
		if(x) {
			this.formulas.add(new Equivalence(v, new Tautology()));
		}else {
			this.formulas.add(new Equivalence(v, new Negation(new Tautology())));
		}
	}
	
	@Override
	public CausalModel clone() {
		return new CausalModel(this.getStructuralEquations());
	}

	private void commonConstructor(Set<Proposition> backGroundAtoms, Set<Proposition> explainableAtoms, Set<Equivalence> structuralEquations) {
		CausalModel.checkCorrectForm(backGroundAtoms, explainableAtoms, structuralEquations);
		this.backGroundAtoms = new HashSet<>(backGroundAtoms);
		this.explainableAtoms = new HashSet<>(explainableAtoms);
		this.addAll(structuralEquations);
	}
}
