package net.sf.tweety.lp.nlp.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.ComplexLogicalFormulaAdapter;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.lp.nlp.error.NestedLogicProgramException;
import net.sf.tweety.util.rules.Rule;

/**
 * A rule of a nested logic program. A nested logic program contains not quantified 
 * first order formulas.
 * 
 * @author Tim Janus
 */
public class NLPRule 
	extends ComplexLogicalFormulaAdapter 
	implements ComplexLogicalFormula, Rule<FolFormula, FolFormula> {

	FolFormula conclusion = null;
	
	Set<FolFormula> premise = new HashSet<FolFormula>();
	
	public NLPRule() {}
	
	public NLPRule(NLPRule other) {
		setConclusion(other.getConclusion().clone());
		for(FolFormula p : other.premise) {
			addPremise(p.clone());
		}
	}
	
	public NLPRule(FolFormula conclusion) {
		setConclusion(conclusion);
	}
	
	public NLPRule(FolFormula conclusion, FolFormula premise) {
		setConclusion(conclusion);
		addPremise(premise);
	}
	
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
	 * @throws LanguageException
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

	@Override
	public Set<FOLAtom> getAtoms() {
		Set<FOLAtom> reval = new HashSet<FOLAtom>();
		reval.addAll(conclusion.getAtoms());
		for(FolFormula f : premise) {
			reval.addAll(f.getAtoms());
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
