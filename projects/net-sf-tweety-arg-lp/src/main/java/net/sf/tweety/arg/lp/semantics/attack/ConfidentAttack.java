package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the confident attack relation. 
 * A attacks B iff A undercuts or confidently rebuts B.
 *  
 * @author Sebastian Homann
 *
 */
public class ConfidentAttack implements AttackStrategy {

	/** Singleton instance. */
	private static ConfidentAttack instance = new ConfidentAttack();
	
	private ConfidentRebut confidentRebut = ConfidentRebut.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private ConfidentAttack(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static ConfidentAttack getInstance(){
		return ConfidentAttack.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return confidentRebut.attacks(a, b) || undercut.attacks(a, b);
	}
	
	@Override
	public String toString() {
		return "confident attack";
	}
	
	public String toAbbreviation() {
		return "ca";
	}
}
