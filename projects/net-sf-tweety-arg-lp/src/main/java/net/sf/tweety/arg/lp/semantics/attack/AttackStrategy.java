package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This interface is the common denominator for notions of attack between two arguments.
 * The implementation is analogous to the Attack interface in the "deductive" package.
 * @author Sebastian Homann
 */
public interface AttackStrategy {

	/**
	 * Returns "true" iff the first argument attacks the second argument.
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff <code>a</code> attacks <code>b</code>.
	 */
	public boolean attacks(Argument a, Argument b);
	
	/**
	 * Returns the abbreviated identifier of this notion of attack, i.e. "a" for attack
	 * @return a short identifier
	 */
	public String toAbbreviation();
}
