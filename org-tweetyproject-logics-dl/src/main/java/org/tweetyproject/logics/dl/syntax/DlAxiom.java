package org.tweetyproject.logics.dl.syntax;

import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula;

/**
 *
 * The common abstract class for axioms of the description logic ALC.
 * Belief bases in description logics consist of axioms.
 *
 * @author Anna Gessler
 *
 */
public abstract class DlAxiom implements SimpleLogicalFormula {

	/**Default */
	public DlAxiom(){
		super();
	}

	@Override
	public abstract DlSignature getSignature();

	@Override
	public Set<? extends Atom> getAtoms() {
		throw new UnsupportedOperationException("getAtoms not supported by Description-Logic");
	}

	@Override
	public abstract Set<Predicate> getPredicates();

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public abstract DlAxiom clone();

}
