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
package org.tweetyproject.action.description.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.action.grounding.GroundingRequirement;
import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolFluentName;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Contradiction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.Tautology;

/**
 * The action description language C consists of two distinct expressions:
 * static laws and dynamic laws. Both share some common functionalities which
 * are implemented in this base class.
 *
 * @author Sebastian Homann
 */
public abstract class CLaw implements CausalLaw {
	/**
	 * The head formula of this causal law.
	 */
	protected FolFormula headFormula = new Contradiction();

	/**
	 * The conditional (if) formula of this causal law.
	 *
	 */
	protected FolFormula ifFormula = new Tautology();

	/**
	 * The set of grounding requirements for this causal law.
	 *
	 */
	protected Set<GroundingRequirement> requirements = new HashSet<GroundingRequirement>();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.commons.Formula#getSignature()
	 */
	public abstract Signature getSignature();

	/**
	 * Returns true iff this law is definite. A causal law is definite if it's head
	 * is a literal or a contradiction and all formulas are conjunctions of
	 * literals.
	 *
	 * @return true, if this law is definite, false otherwise.
	 */
	public abstract boolean isDefinite();

	/**
	 * Returns an equivalent definite causal law. A causal law is definite if it's
	 * head is a literal or a contradiction and all formulas are conjunctions of
	 * literals.
	 *
	 * @return the equivalent definite causal law if one exists.
	 * @throws IllegalStateException when there is no equivalent definite causal
	 *                               law.
	 */
	public abstract Set<CLaw> toDefinite() throws IllegalStateException;

	/**
	 * Retrieves the set of propositions (atoms) contained in all formulas within this law.
	 *
	 * @return a `Set` of `FolAtom` objects representing all the propositions in the formulas
	 *         that constitute this law.
	 */
	public abstract Set<FolAtom> getAtoms();


	/**
	 * Returns the set of formulas contained in this causal law, e.g. in a static
	 * law, this contains the head formula and the if formula.
	 *
	 * @return the set of formulas contained in this causal law.
	 */
	public abstract Set<FolFormula> getFormulas();

	/**
	 * Creates an empty causal law.
	 */
	public CLaw() {
	}

	/**
	 * Creates a causal law of the form "caused headFormula if True"
	 *
	 * @param headFormula some FOL formula
	 */
	public CLaw(FolFormula headFormula) {
		setHeadFormula(headFormula);
	}

	/**
	 * Creates a causal law of the form "caused headFormula if True requires
	 * requirements"
	 *
	 * @param headFormula  some FOL formula
	 * @param requirements a set of requirements
	 */
	public CLaw(FolFormula headFormula, Set<GroundingRequirement> requirements) {
		setHeadFormula(headFormula);
		setGroundingRequirements(requirements);
	}

	/**
	 * Creates a causal law of the form "caused headFormula if ifFormula requires
	 * requirements"
	 *
	 * @param headFormula  some FOL formula
	 * @param ifFormula    some FOL formula
	 * @param requirements a set of requirements
	 */
	public CLaw(FolFormula headFormula, FolFormula ifFormula, Set<GroundingRequirement> requirements) {
		setHeadFormula(headFormula);
		setIfFormula(ifFormula);
		setGroundingRequirements(requirements);
	}

	/**
	 * Creates a causal law of the form "caused headFormula if ifFormula"
	 *
	 * @param headFormula some FOL formula
	 * @param ifFormula   some FOL formula
	 */
	public CLaw(FolFormula headFormula, FolFormula ifFormula) {
		setHeadFormula(headFormula);
		setIfFormula(ifFormula);
	}

	private void setGroundingRequirements(Set<GroundingRequirement> requirements) {
		if (requirements != null)
			this.requirements.addAll(requirements);
	}

	/**
	 * Sets the headFormula of this causal law
	 *
	 * @param headFormula The new headFormula of this causal law.
	 */
	private void setHeadFormula(FolFormula headFormula) {
		if (headFormula == null) {
			this.headFormula = new Contradiction();
			return;
		}

		if (!(new ActionSignature(headFormula).isRepresentable(headFormula))) {
			throw new IllegalArgumentException("The formula given has an illegal form");
		}
		this.headFormula = (FolFormula) headFormula.collapseAssociativeFormulas();
	}

	/**
	 * Sets the IfFormula of this causal law
	 *
	 * @param ifFormula The new IfFormula of this causal law.
	 */
	private void setIfFormula(FolFormula ifFormula) {
		if (ifFormula == null) {
			this.ifFormula = new Tautology();
			return;
		}
		if (!(new ActionSignature(ifFormula).isRepresentable(ifFormula))) {
			throw new IllegalArgumentException("The formula given has an illegal form");
		}
		this.ifFormula = (FolFormula) ifFormula.collapseAssociativeFormulas();
	}

	/**
	 * Adds a grounding requirement to this CLaw.
	 *
	 * @param c the grounding requirement to be added
	 */
	public void addGroundingRequirement(GroundingRequirement c) {
		requirements.add(c);
	}

	/**
	 * Retrieves the head formula of this causal law.
	 *
	 * @return the `FolFormula` that constitutes the head of this causal law.
	 */
	public FolFormula getHeadFormula() {
		return headFormula;
	}

	/**
	 * Retrieves the conditional (if) formula of this causal law.
	 *
	 *
	 * @return the `FolFormula` that constitutes the condition (if clause) of this causal law.
	 */
	public FolFormula getIfFormula() {
		return ifFormula;
	}



	/**
	 * Checks if the CLaw is ground, i.e., if all the atoms in the CLaw are ground.
	 *
	 * @return true if the CLaw is ground, false otherwise.
	 */
	public boolean isGround() {
		for (FolAtom a : getAtoms())
			if (!a.isGround())
				return false;
		return true;
	}

	/**
	 * Retrieves the set of all grounded instances of this causal law.
	 *
	 * @return a `Set` of `CLaw` objects representing all grounded instances of this causal law.
	 */
	public abstract Set<CLaw> getAllGrounded();

	/**
	 * Checks if a propositional formula is a valid head formula for a definite
	 * causal law, which means either a contradiction, a fluent or the negation of a
	 * fluent.
	 *
	 * @param pl a propositional formula
	 * @return true, if pl is a valid definite head formula
	 */
	protected boolean isValidDefiniteHead(RelationalFormula pl) {

		if (pl instanceof Contradiction)
			return true;
		if (pl instanceof Negation)
			pl = ((Negation) pl).getFormula();
		if (pl instanceof FolAtom)
			return ((FolAtom) pl).getPredicate() instanceof FolFluentName;
		return false;
	}

	/**
	 * Checks if the given formula is a conjunctive clause, meaning
	 * either a literal or a conjunction of literals.
	 *
	 * @param pl a propositional formula
	 * @return true, if pl is a conjunctive clause
	 */
	protected boolean isConjunctiveClause(FolFormula pl) {
		if (pl instanceof Conjunction) {
			for (RelationalFormula p : ((Conjunction) pl)) {
				if (!((FolFormula) p).isLiteral())
					return false;
			}
		} else if (pl instanceof Disjunction)
			return false;
		return true;
	}
}
