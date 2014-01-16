package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This notion of attack models the strong confident attack relation. 
 * A strongly attacks B iff 
 *   (1) A confidently attacks B and 
 *   (2) B does not undercut A.
 *  
 * @author Sebastian Homann
 *
 */
public class StrongConfidentAttack implements AttackStrategy {

	/** Singleton instance. */
	private static StrongConfidentAttack instance = new StrongConfidentAttack();
	
	private ConfidentAttack confidentAttack = ConfidentAttack.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private StrongConfidentAttack(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static StrongConfidentAttack getInstance(){
		return StrongConfidentAttack.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return confidentAttack.attacks(a, b) && (! undercut.attacks(b, a) );
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "strong confident attack";
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "sca";
	}
}
