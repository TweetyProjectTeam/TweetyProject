package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * class to model special build-in dlv
 * predicate #int(value), where value
 * is a natural number or a variable
 * 
 * @author Thomas Vengels
 *
 */
class IntPredicate extends ELPAtom {
	public IntPredicate(String value) {
		super("#int",value);
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
}
