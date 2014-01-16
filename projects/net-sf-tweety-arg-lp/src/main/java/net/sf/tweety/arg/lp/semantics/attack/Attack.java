package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the attack relation. 
 * A attacks B iff A undercuts or rebuts B.
 *  
 * @author Sebastian Homann
 *
 */
public class Attack implements AttackStrategy {

	/** Singleton instance. */
	private static Attack instance = new Attack();
	
	private Rebut rebut = Rebut.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private Attack(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Attack getInstance(){
		return Attack.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return rebut.attacks(a, b) || undercut.attacks(a, b);
	}
	
	@Override
	public String toString() {
		return "attack";
	}
	
	public String toAbbreviation() {
		return "a";
	}
}
