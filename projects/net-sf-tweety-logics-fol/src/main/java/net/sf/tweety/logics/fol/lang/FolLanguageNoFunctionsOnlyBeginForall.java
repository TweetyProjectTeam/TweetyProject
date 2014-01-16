package net.sf.tweety.logics.fol.lang;

import net.sf.tweety.*;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models a first-order language without functions and there may be
 * only forall-quantifiers at the beginning of the formula.
 * @author Matthias Thimm
 */
public class FolLanguageNoFunctionsOnlyBeginForall extends FolLanguageNoFunctions {
	
	/**
	 * Creates a new language on the given signature.
	 * @param signature a signature.
	 */
	public FolLanguageNoFunctionsOnlyBeginForall(Signature signature){
		super(signature);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.lang.FolLanguageNoFunctions#isRepresentable(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean isRepresentable(Formula formula){
		if(!super.isRepresentable(formula)) return false;
		//either the formula contains no quantifier at all
		if((new FolLanguageNoQuantifiers(this.getSignature()).isRepresentable(formula))) return true;		
		FolFormula folFormula = (FolFormula) formula;		
		//or the outermost formula is a forall-quantifier and the inner formula is representable
		//with this language
		if(folFormula instanceof ForallQuantifiedFormula){
			FolFormula inner = ((ForallQuantifiedFormula) folFormula).getFormula();
			return this.isRepresentable(inner);			
		}
		return false;		
	}	

}
