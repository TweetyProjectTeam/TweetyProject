package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;


/**
 * This notion of attack models the rebut relation. 
 * A rebuts B iff there is L in conclusion(A) and \neg L in conclusion(B).
 *  
 * @author Sebastian Homann
 *
 */
public class Rebut implements AttackStrategy {

	/** Singleton instance. */
	private static Rebut instance = new Rebut();
	
	/** Private constructor. */
	private Rebut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Rebut getInstance(){
		return Rebut.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
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
		return "rebut";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "r";
	}
}
