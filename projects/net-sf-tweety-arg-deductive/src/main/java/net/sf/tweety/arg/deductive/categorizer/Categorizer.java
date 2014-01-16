package net.sf.tweety.arg.deductive.categorizer;

import net.sf.tweety.arg.deductive.semantics.ArgumentTree;

/**
 * Classes implementing this interface represent categorizer in the sense
 * of Definition 8.10 in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 * 
 * @author Matthias Thimm
 */
public interface Categorizer {

	/** This method categorizes the given argument tree. In general,
	 * the semantics of this function is that a higher value of 
	 * this categorization means a higher belief in the claim
	 * of the root argument of the argument tree.
	 * @param argumentTree some argument tree.
	 * @return the categorization of the argument tree.
	 */
	public double categorize(ArgumentTree argumentTree);
}
