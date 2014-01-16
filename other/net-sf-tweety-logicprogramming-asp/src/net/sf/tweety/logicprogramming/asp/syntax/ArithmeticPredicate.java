package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * private class to model arithmetic expressions
 * like "A = B + C".
 * 
 * @author Thomas Vengels
 *
 */
class ArithmeticPredicate extends ELPAtom {
	
	public ArithmeticPredicate(String pred, String ...terms) {
		super(pred, terms);
	}
	
	public String toString() {
		return terms[2] + "=" + terms[0] + pred + terms[1];
	}
		
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}