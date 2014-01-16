package net.sf.tweety;

/**
 * A formula is a basic language construct.
 * @author Matthias Thimm
 */
public interface Formula {
	
	/**
	 * Returns the signature of the language of this formula.
	 * @return the signature of the language of this formula.
	 */
	Signature getSignature();
}
