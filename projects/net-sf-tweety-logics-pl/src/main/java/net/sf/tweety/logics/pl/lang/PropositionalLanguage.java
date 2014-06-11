package net.sf.tweety.logics.pl.lang;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Language;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class models a propositional language for a given signature.
 * @author mthimm
 *
 */
public class PropositionalLanguage extends Language {

	/**
	 * Creates a new language on the given first-order signature.
	 * @param signature a first-order signature.
	 */
	public PropositionalLanguage(Signature signature){
		super(signature);
		if(!(signature instanceof PropositionalSignature))
			throw new IllegalArgumentException("Signatures for propositional languages must be propositional signatures.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Language#isRepresentable(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean isRepresentable(Formula formula) {
		return formula instanceof PropositionalFormula;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Language#isRepresentable(net.sf.tweety.kr.BeliefBase)
	 */
	@Override
	public boolean isRepresentable(BeliefBase beliefBase) {
		return beliefBase instanceof PlBeliefSet;
	}

}
