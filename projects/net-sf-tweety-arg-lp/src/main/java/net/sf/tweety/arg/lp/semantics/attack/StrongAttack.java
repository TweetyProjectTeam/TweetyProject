package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the strong attack relation. 
 * A strongly attacks B iff 
 *   (1) A attacks B and 
 *   (2) B does not undercut A.
 *  
 * @author Sebastian Homann
 *
 */
public class StrongAttack implements AttackStrategy {

	/** Singleton instance. */
	private static StrongAttack instance = new StrongAttack();
	
	private Attack attack = Attack.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private StrongAttack(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static StrongAttack getInstance(){
		return StrongAttack.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return attack.attacks(a, b) && (! undercut.attacks(b, a) );
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "strong attack";
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "sa";
	}
}
