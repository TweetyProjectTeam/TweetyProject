package net.sf.tweety.lp.nlp.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.lp.nlp.error.NestedLogicProgramException;

/**
 * A default negation of a first order formula, nested logic programs
 * only allow not quantified formulas. The NLPNot wraps a FOLFormula
 * and checks that this formula does not contain quantifiers, the functionality
 * is delegated to the used inner formula instance.
 * 
 * @author Tim Janus
 */
public class NLPNot extends FolFormula {
	private FolFormula inner;
	
	/**
	 * Ctor: Creates a new default not for nested logic programs by using the
	 * given FOL formula as inner formula.
	 * @param inner	The FOL formula used as inner formula.
	 */
	public NLPNot(FolFormula inner) {
		if(inner.containsQuantifier()) {
			throw new NestedLogicProgramException(
					LanguageExceptionReason.LER_QUANTIFICATION_NOT_SUPPORTED, 
					inner.toString());
		}
		this.inner = inner;
	}

	@Override
	public Set<? extends Predicate> getPredicates() {
		return inner.getPredicates();
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Variable> getUnboundVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public boolean containsQuantifier() {
		return false;
	}

	@Override
	public boolean isWellBound() {
		return true;
	}

	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return false;
	}

	@Override
	public boolean isClosed() {
		return true;
	}

	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return true;
	}

	@Override
	public Set<Term<?>> getTerms() {
		return inner.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return inner.getTerms(cls);
	}

	@Override
	public FolFormula toNnf() {
		return new NLPNot(inner.toNnf());
	}

	@Override
	public NLPNot collapseAssociativeFormulas() {
		return new NLPNot((FolFormula)inner.collapseAssociativeFormulas());
	}

	@Override
	public boolean isDnf() {
		return inner.isDnf();
	}

	@Override
	public NLPNot substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		return new NLPNot(inner.substitute(v, t));
	}

	@Override
	public NLPNot clone() {
		return new NLPNot(inner.clone());
	}

	@Override
	public Set<FOLAtom> getAtoms() {
		return inner.getAtoms();
	}

	@Override
	public Set<Functor> getFunctors() {
		return inner.getFunctors();
	}

	@Override
	public String toString() {
		return "not " + inner.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		
		if(!(other instanceof NLPNot)) {
			return false;
		}
		NLPNot o = (NLPNot)other;
		return inner.equals(o.inner);
	}
	
	@Override
	public int hashCode() {
		return inner.hashCode() + 7;
	}
}
