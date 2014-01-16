package net.sf.tweety.arg.deductive.semantics.attacks;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;

/**
 * This interface is the common interface for notions of attack between two arguments.
 * @author Matthias Thimm
 */
public interface Attack {

	/**
	 * Returns "true" iff the first argument is attacked by the second argument.
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff <code>a</code> is attacked by <code>b</code>.
	 */
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b);
}
