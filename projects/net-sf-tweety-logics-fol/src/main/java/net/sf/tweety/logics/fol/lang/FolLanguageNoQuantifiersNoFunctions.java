package net.sf.tweety.logics.fol.lang;

import net.sf.tweety.*;

/**
 * This class models a first-order language without quantifiers and without functions.
 * @author Matthias Thimm
 */
public class FolLanguageNoQuantifiersNoFunctions extends FolLanguageNoQuantifiers{
	/**
	 * Creates a new language on the given signature.
	 * @param signature a signature.
	 */
	public FolLanguageNoQuantifiersNoFunctions(Signature signature){
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.lang.FolLanguageNoQuantifiers#isRepresentable(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean isRepresentable(Formula formula){
		if(!super.isRepresentable(formula)) return false;
		return (new FolLanguageNoFunctions(this.getSignature()).isRepresentable(formula));		
	}

}
