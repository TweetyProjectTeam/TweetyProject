package net.sf.tweety.lp.nlp.error;

import net.sf.tweety.logics.commons.error.LanguageException;

/**
 * NestedLogicProramException encapsulates those LanugageException that occur
 * in nested logic programs. It allows easier creation of language specific
 * exceptions. The language registered at the base exception is 
 * "Nested Logic Programs".
 * 
 * @author Tim Janus
 */
public class NestedLogicProgramException extends LanguageException {

	/** kill warning */
	private static final long serialVersionUID = 4406781843927755406L;
	
	public NestedLogicProgramException(LanguageExceptionReason reason) {
		super("Nested Logic Programs", reason);
	}
	
	public NestedLogicProgramException(LanguageExceptionReason reason, 
			String furtherInformation) {
		super("Nested Logic Programs", reason, furtherInformation);
	}
}
