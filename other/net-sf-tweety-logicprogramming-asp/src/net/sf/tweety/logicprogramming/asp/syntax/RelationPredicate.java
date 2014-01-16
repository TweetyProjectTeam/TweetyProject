package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * private class to model comparison expressions
 * like "A > B" or $A = B$.
 * 
 * @author Thomas Vengels
 *
 */
public class RelationPredicate extends ELPAtom {
	
	public RelationPredicate(String pred, String ...terms) {
		super(pred, terms);
	}
	
	public String toString() {
		return terms[0] + pred + terms[1];
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
}