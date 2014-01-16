package net.sf.tweety.arg.deductive.semantics.attacks;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;

/**
 * This attack notion models the canonical undercut relation; A is defeated by B iff claim(B) =- \neg support(A).
 * @author Matthias Thimm
 */
public class CanonicalUndercut implements Attack{

	/** Singleton instance. */
	private static CanonicalUndercut instance = new CanonicalUndercut();
	
	/** Private constructor. */
	private CanonicalUndercut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static CanonicalUndercut getInstance(){
		return CanonicalUndercut.instance;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.attacks.Attack#isAttackedBy(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument, net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b) {
		ClassicalEntailment entailment = new ClassicalEntailment();
		if(entailment.isEquivalent(b.getClaim(), new Negation(new Conjunction(a.getSupport()))))
			return true;
		return false;
	}

}
