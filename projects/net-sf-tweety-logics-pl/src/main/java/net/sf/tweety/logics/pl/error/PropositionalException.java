package net.sf.tweety.logics.pl.error;

import net.sf.tweety.logics.commons.error.LanguageException;

/**
 * An Exception for the propositional language, it is thrown if a developer
 * tries to create illegal propositional statements like a predicate with an
 * arity greater than zero. 
 * 
 * @author Tim Janus
 */
public class PropositionalException extends LanguageException {
	
	/** kill warning */
	private static final long serialVersionUID = 843894579984076905L;

	public PropositionalException(LanguageExceptionReason reason) {
		this(reason, "");
	}
	
	public PropositionalException(LanguageExceptionReason reason, String info) {
		super("Propositional-Logic", reason, info);
	}
}
