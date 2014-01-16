package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * class for default negated literals
 * 
 * @author Thomas Vengels
 *
 */
public class NotLiteral implements ELPLiteral {
	ELPLiteral lit;
	
	public NotLiteral(){
		
	}
	
	public NotLiteral(ELPLiteral arg) {
		this.lit = arg;
	}

	@Override
	public ELPLiteral getLiteral() {
		return lit;
	}

	@Override
	public boolean isAtom() {
		return false;
	}

	@Override
	public boolean isDefaultNegated() {
		return true;
	}

	@Override
	public boolean isStrictNegated() {
		return lit.isStrictNegated();
	}

	@Override
	public ELPAtom getAtom() {
		return this.lit.getAtom();
	}
	
	public String toString() {
		return "not "+lit.toString();
	}
	
	@Override
	public boolean isPredicate() {
		return false;
	}
}
