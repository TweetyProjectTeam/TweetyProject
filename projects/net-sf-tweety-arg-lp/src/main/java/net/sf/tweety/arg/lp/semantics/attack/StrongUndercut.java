package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the strong undercut relation. 
 * A strongly undercuts B iff 
 * (1) A undercuts B and
 * (2) B does not undercut A
 *  
 * @author Sebastian Homann
 *
 */
public class StrongUndercut implements AttackStrategy {

	/** Singleton instance. */
	private static StrongUndercut instance = new StrongUndercut();
	
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private StrongUndercut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static StrongUndercut getInstance(){
		return StrongUndercut.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return undercut.attacks(a, b) && (! undercut.attacks(b, a) );
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "strong undercut";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "su";
	}
}
