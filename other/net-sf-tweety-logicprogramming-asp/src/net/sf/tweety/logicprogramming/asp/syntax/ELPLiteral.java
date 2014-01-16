package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * this interface models a logical literal used in
 * extended logic programs. 
 * 
 * @author Thomas Vengels
 *
 */
public interface ELPLiteral {
	
	/**
	 * 
	 * @return	true if this literal is default negated
	 */
	public boolean	isDefaultNegated();

	/**
	 * 
	 * @return	true if this literal is strictly negated
	 */
	public boolean	isStrictNegated();
		
	/**
	 * 
	 * @return	true if this literal is an atom
	 */
	public boolean isAtom();
	
	
	/**
	 * this method indicates if the literal appearing in
	 * a program is a build-in or external dlv predicate
	 * 
	 * @return true if this object models a dlv predicate
	 */
	public boolean isPredicate();
	
	
	/**
	 * returns a possibly nested literal, or the object itself 
	 * if this literal is not (strict or default) negated.
	 * 
	 * @return	nested literal
	 */
	public ELPLiteral getLiteral();
	
	/**
	 * returns the atom of this literal
	 * 
	 * @return	atom
	 */
	public ELPAtom getAtom();
	
}
