package net.sf.tweety.arg.deductive.semantics.attacks;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;

/**
 * This attack notion models the defeat relation; A is defeated by B iff claim(B) |- \neg support(A).
 * @author Matthias Thimm
 */
public class Defeat implements Attack{

	/** Singleton instance. */
	private static Defeat instance = new Defeat();
	
	/** Private constructor. */
	private Defeat(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Defeat getInstance(){
		return Defeat.instance;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.attacks.Attack#isAttackedBy(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument, net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b) {
		ClassicalEntailment entailment = new ClassicalEntailment();
		if(entailment.entails(b.getClaim(), new Negation(new Conjunction(a.getSupport()))))
			return true;
		return false;
	}

}
