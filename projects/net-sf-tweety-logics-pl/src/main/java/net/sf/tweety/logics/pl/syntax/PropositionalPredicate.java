package net.sf.tweety.logics.pl.syntax;

import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.pl.error.PropositionalException;

/**
 * A specialized predicate for propositional logic that only allows an identifier
 * but has no arguments and therefore has an arity of zero.
 * 
 * @author Tim Janus
 */
public class PropositionalPredicate extends Predicate {
	
	/** Default-Ctor for dynamic instantiation */
	public PropositionalPredicate() {
		this("");
	}
	
	/**
	 * Ctor: Creates a new propositional predicate with the given
	 * name.
	 * @param name	The name of the predicate
	 */
	public PropositionalPredicate(String name) {
		super(name, 0);
	}
	
	@Override
	public void setArity(int arity) {
		if(arity != 0) {
			throw new PropositionalException(LanguageExceptionReason.LER_ILLEGAL_PREDICATE,
					"The arity must be zero.");
		}
	}
	
	@Override
	public void addArgumentType(Sort argType) {
		throw new PropositionalException(LanguageExceptionReason.LER_ILLEGAL_PREDICATE,
				"The predicates must not have any arguments.");
	}
	
	@Override
	public PropositionalPredicate clone() {
		return new PropositionalPredicate(this.getName());
	}
}
