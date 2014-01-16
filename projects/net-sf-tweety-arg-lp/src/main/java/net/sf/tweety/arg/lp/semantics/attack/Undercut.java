package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;


/**
 * This notion of attack models the undercut relation. 
 * A undercuts B iff there is L in conclusion(A) and not L in assumption(B).
 *  
 * @author Sebastian Homann
 *
 */
public class Undercut implements AttackStrategy {

	/** Singleton instance. */
	private static Undercut instance = new Undercut();
	
	/** Private constructor. */
	private Undercut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Undercut getInstance(){
		return Undercut.instance;
	}	
	

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		for(DLPLiteral literal : a.getConclusions()) {
			if(b.getAssumptions().contains(literal)) {
				return true;
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
		return "undercut";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "u";
	}
}
