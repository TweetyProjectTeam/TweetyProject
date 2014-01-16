package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the defeat relation. 
 * A defeats B iff 
 * (1) A undercuts B or
 * (2) A rebuts B and B does not undercut A
 *  
 * @author Sebastian Homann
 *
 */
public class Defeat implements AttackStrategy {

	/** Singleton instance. */
	private static Defeat instance = new Defeat();
	
	private Rebut rebut = Rebut.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private Defeat(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Defeat getInstance(){
		return Defeat.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		if(undercut.attacks(a,b)) {
			return true;
		}
		return rebut.attacks(a,b) && !undercut.attacks(b,a);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "defeat";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "d";
	}
}
