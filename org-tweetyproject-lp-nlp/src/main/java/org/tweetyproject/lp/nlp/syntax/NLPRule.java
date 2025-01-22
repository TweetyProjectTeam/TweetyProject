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
package org.tweetyproject.lp.nlp.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.util.rules.Rule;
import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.error.LanguageException.LanguageExceptionReason;
import org.tweetyproject.logics.commons.syntax.ComplexLogicalFormulaAdapter;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.lp.nlp.error.NestedLogicProgramException;

/**
 * A rule of a nested logic program. A nested logic program contains not quantified
 * first order formulas.
 *
 * @author Tim Janus
 */
public class NLPRule
	extends ComplexLogicalFormulaAdapter
	implements ComplexLogicalFormula, Rule<FolFormula, FolFormula> {

	/**
     * The conclusion of the rule, represented as a first-order logic formula.
     */
	FolFormula conclusion = null;

	/**
     * The premises of the rule, represented as a set of first-order logic formulas.
     * These are the conditions that must hold for the conclusion to be valid.
     */
	Set<FolFormula> premise = new HashSet<FolFormula>();

	/**
     * Default constructor that creates an empty `NLPRule`.
     */
	public NLPRule() {}



	/**
     * Copy constructor that creates a new `NLPRule` by copying an existing rule.
     *
     * <p>
     * The new rule is created by cloning the conclusion and premises of the
     * provided `NLPRule` object. This ensures that the original rule and the
     * copied rule do not share references to the same formula objects.
     * </p>
     *
     * @param other the `NLPRule` to be copied.
     */
	public NLPRule(NLPRule other) {
		setConclusion(other.getConclusion().clone());
		for(FolFormula p : other.premise) {
			addPremise(p.clone());
		}
	}

	/**
     * Constructor that creates a `NLPRule` with a specified conclusion.
     *
     * <p>
     * This constructor initializes the rule with a given conclusion formula,
     * without any premises.
     * </p>
     *
     * @param conclusion the `FolFormula` that serves as the conclusion of the rule.
     */
	public NLPRule(FolFormula conclusion) {
		setConclusion(conclusion);
	}

	/**
     * Constructor that creates a `NLPRule` with a specified conclusion and a single premise.
     *
     * <p>
     * This constructor initializes the rule with a given conclusion formula and a
     * single premise formula.
     * </p>
     *
     * @param conclusion the `FolFormula` that serves as the conclusion of the rule.
     * @param premise the `FolFormula` that serves as the premise of the rule.
     */
	public NLPRule(FolFormula conclusion, FolFormula premise) {
		setConclusion(conclusion);
		addPremise(premise);
	}

	/**
     * Constructor that creates a `NLPRule` with a specified conclusion and a collection of premises.
     *
     * <p>
     * This constructor initializes the rule with a given conclusion formula and a
     * collection of premise formulas.
     * </p>
     *
     * @param conclusion the `FolFormula` that serves as the conclusion of the rule.
     * @param premise a collection of `FolFormula` objects that serve as the premises of the rule.
     */
	public NLPRule(FolFormula conclusion, Collection<FolFormula> premise) {
		setConclusion(conclusion);
		addPremises(premise);
	}

	@Override
	public void addPremise(FolFormula premise) throws LanguageException {
		checkFormula(premise);
		this.premise.add(premise);
	}

	@Override
	public void addPremises(Collection<? extends FolFormula> premises) {
		for(FolFormula f : premises) {
			checkFormula(f);
		}
		this.premise.addAll(premises);
	}

	@Override
	public void setConclusion(FolFormula conclusion) throws LanguageException {
		checkFormula(conclusion);
		this.conclusion = conclusion;
	}

	/**
	 * Helper methods checks if the given FOL formula is supported by the nested logic
	 * program language, that means it checks if it contains quantifiers and if that
	 * is the case it throws a LanguageException
	 * @param formula	The formula which gets checked
	 * @throws LanguageException if there is an issue with the language
	 */
	private void checkFormula(FolFormula formula) throws LanguageException {
		if(formula.containsQuantifier()) {
			throw new NestedLogicProgramException(
					LanguageExceptionReason.LER_QUANTIFICATION_NOT_SUPPORTED,
					formula.toString());
		}
	}

	@Override
	public Collection<FolFormula> getPremise() {
		return Collections.unmodifiableCollection(premise);
	}

	@Override
	public FolFormula getConclusion() {
		return conclusion;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature signature = new FolSignature();
		signature.addSignature(conclusion.getSignature());
		for(FolFormula preF : premise) {
			signature.addSignature(preF.getSignature());
		}
		return signature;
	}

	@Override
	public boolean isFact() {
		return conclusion != null && premise.isEmpty();
	}

	@Override
	public boolean isConstraint() {
		return conclusion == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		Set<FolAtom> reval = new HashSet<FolAtom>();
		reval.addAll((Collection<? extends FolAtom>) conclusion.getAtoms());
		for(FolFormula f : premise) {
			reval.addAll((Collection<? extends FolAtom>) f.getAtoms());
		}
		return reval;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> reval = new HashSet<Predicate>();
		reval.addAll(conclusion.getPredicates());
		for(FolFormula f : premise) {
			reval.addAll(f.getPredicates());
		}
		return reval;
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.addAll(conclusion.getTerms());
		for(FolFormula f : premise) {
			reval.addAll(f.getTerms());
		}
		return reval;
	}

	@Override
	public NLPRule substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		FolFormula conclusion = this.conclusion.substitute(v, t);
		List<FolFormula> premise = new LinkedList<FolFormula>();
		for(FolFormula f : this.premise) {
			premise.add(f.substitute(v, t));
		}
		return new NLPRule(conclusion, premise);
	}

	@Override
	public int hashCode() {
		int prime = 13;
		return (conclusion.hashCode() + premise.hashCode()) * prime;
	}

	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(!(other instanceof NLPRule))
			return false;
		NLPRule or = (NLPRule)other;
		return conclusion.equals(or.conclusion) && premise.equals(or.premise);
	}

	@Override
	public NLPRule clone() {
		return new NLPRule(this);
	}
}
