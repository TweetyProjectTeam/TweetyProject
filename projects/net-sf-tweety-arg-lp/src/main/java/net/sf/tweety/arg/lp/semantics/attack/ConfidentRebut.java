package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;


/**
 * This notion of attack models the strong rebut relation. 
 * A strongly rebuts B iff assumptions(A) = \emptyset and
 *                         there is L in conclusion(A) and \neg L in conclusion(B).
 *  
 * @author Sebastian Homann
 *
 */
public class ConfidentRebut implements AttackStrategy {

	/** Singleton instance. */
	private static ConfidentRebut instance = new ConfidentRebut();
	
	/** Private constructor. */
	private ConfidentRebut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static ConfidentRebut getInstance(){
		return ConfidentRebut.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		if(!a.getAssumptions().isEmpty()) {
			return false;
		}
		for(DLPLiteral literalA : a.getConclusions()) {
			for(DLPLiteral literalB : b.getConclusions()) {
				if(literalA.complement().equals(literalB)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "confident rebut";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "cr";
	}
}
