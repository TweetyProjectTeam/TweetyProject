package net.sf.tweety.lp.asp.syntax;

import net.sf.tweety.logics.commons.syntax.ComplexLogicalFormulaAdapter;
import net.sf.tweety.logics.commons.syntax.Predicate;

/**
 * This acts as abstract base class for classes implement 
 * the ELPElement interface
 * @author Tim Janus
 */
public abstract class DLPElementAdapter 
	extends ComplexLogicalFormulaAdapter 
	implements DLPElement {

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return DLPPredicate.class;
	}
	
	public abstract DLPElement clone();
}
